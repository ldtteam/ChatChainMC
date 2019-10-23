package co.chatchain.mc.forge;

import co.chatchain.commons.AccessTokenResolver;
import co.chatchain.commons.ChatChainHubConnection;
import co.chatchain.commons.messages.objects.Client;
import co.chatchain.commons.messages.objects.Group;
import co.chatchain.commons.messages.objects.User;
import co.chatchain.commons.messages.objects.messages.*;
import co.chatchain.mc.forge.capabilities.GroupProvider;
import co.chatchain.mc.forge.capabilities.IGroupSettings;
import co.chatchain.mc.forge.commands.EntryPoint;
import co.chatchain.mc.forge.configs.*;
import co.chatchain.mc.forge.configs.formatting.AdvancedFormattingConfig;
import co.chatchain.mc.forge.configs.formatting.ReplacementUtils;
import co.chatchain.mc.forge.configs.formatting.FormattingConfig;
import co.chatchain.mc.forge.message.handling.APIMessages;
import co.chatchain.mc.forge.serializers.GroupTypeSerializer;
import co.chatchain.mc.forge.util.Log;
import com.google.common.reflect.TypeToken;
import com.microsoft.signalr.HubConnectionState;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.gson.GsonConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Mod(ChatChainMC.MOD_ID)
@Mod.EventBusSubscriber
public class ChatChainMC
{

    public static final String MOD_ID = "chatchainmc";

    /**
     * This is the instance of your mod as created by Forge. It will never be null.
     */
    @SuppressWarnings("squid:S1444")
    @NonNull
    public static ChatChainMC INSTANCE;

    public static MinecraftServer MINECRAFT_SERVER;

    private AccessTokenResolver accessToken = null;

    @Getter
    private ChatChainHubConnection connection = null;

    @Getter
    private MainConfig mainConfig;

    @Getter
    private GroupsConfig groupsConfig;

    @Getter
    private FormattingConfig formattingConfig;

    @Getter
    private AdvancedFormattingConfig advancedFormattingConfig;

    @Getter
    @Setter
    private Client client;

    private File configDir;

    public ChatChainMC()
    {
        ChatChainMC.INSTANCE = this;

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::preInit);

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void preInit(FMLCommonSetupEvent event)
    {
        configDir = new File("config/ChatChainMC/");

        reloadConfigs();

        CapabilityManager.INSTANCE.register(IGroupSettings.class, new IGroupSettings.Storage(), new IGroupSettings.Factory());
    }

    @SubscribeEvent
    public void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event)
    {
        if (event.getObject() instanceof PlayerEntity)
        {
            event.addCapability(new ResourceLocation(MOD_ID, "groupsettings"), new GroupProvider());
        }
    }

    @SubscribeEvent
    public void serverStart(FMLServerStartingEvent event)
    {
        MINECRAFT_SERVER = event.getServer();

        try
        {
            accessToken = new AccessTokenResolver(getMainConfig().getClientId(), getMainConfig().getClientSecret(), getMainConfig().getIdentityUrl());
        } catch (IOException e)
        {
            Log.getLogger().error("Exception while attempting to get ChatChain Access Token from IdentityServer", e);
        }

        connection = new ChatChainHubConnection(getMainConfig().getApiUrl(), accessToken);
        connection.onConnection(hub -> {
            hub.onGenericMessage(APIMessages::ReceiveGenericMessage, GenericMessage.class);
            hub.onClientEventMessage(APIMessages::ReceiveClientEvent, ClientEventMessage.class);
            hub.onUserEventMessage(APIMessages::ReceiveUserEvent, UserEventMessage.class);
            hub.onGetGroupsResponse(APIMessages::ReceiveGroups, GetGroupsResponse.class);
            hub.onGetClientResponse(APIMessages::ReceiveClient, GetClientResponse.class);

            hub.sendGetGroups();
            hub.sendGetClient();
            hub.sendClientEventMessage(new ClientEventMessage("START"));

            Log.getLogger().info("Connection Status: " + hub.getConnectionState());
        });
        connection.connect(false);

        EntryPoint.register(event.getCommandDispatcher());
    }

    @SubscribeEvent
    public void serverStop(FMLServerStoppingEvent event)
    {
        ChatChainMC.INSTANCE.getConnection().disconnect();
    }

    @SubscribeEvent
    public void mcChatMessage(ServerChatEvent event)
    {
        final LazyOptional<IGroupSettings> optionalGroupSettings = event.getPlayer().getCapability(GroupProvider.GROUP_SETTINGS_CAP, null);

        optionalGroupSettings.ifPresent(settings -> this.handleChatMessage(settings, event));
    }

    private void handleChatMessage(final IGroupSettings groupSettings, ServerChatEvent event)
    {
        final User user = new User(event.getUsername(), event.getPlayer().getUniqueID().toString(), null);

        final GenericMessage message = new GenericMessage(groupSettings.getTalkingGroup(), user, event.getMessage(), false);

        if (groupSettings.getTalkingGroup() == null)
        {
            final String defaultGroupString = getGroupsConfig().getDefaultGroup();
            if (defaultGroupString != null && !defaultGroupString.isEmpty())
            {
                final GroupConfig groupConfig = getGroupsConfig().getGroupStorage().get(defaultGroupString);
                if (groupConfig.getPlayersCanTalk().contains(event.getPlayer()))
                {
                    groupSettings.setTalkingGroup(groupConfig.getGroup());
                }
                else
                {
                    event.getPlayer().sendMessage(new StringTextComponent("§cYou do not have perms for default group in the config!"));
                    event.setCanceled(true);
                    return;
                }
            }
            else
            {
                event.getPlayer().sendMessage(new StringTextComponent("§cPlease set a default group for chat in the config!"));
                event.setCanceled(true);
                return;
            }
        }

        final GroupConfig groupConfig = getGroupsConfig().getGroupStorage().get(groupSettings.getTalkingGroup().getGroupId());

        if (!groupConfig.getPlayersCanTalk().contains(event.getPlayer()))
        {
            event.getPlayer().sendMessage(new StringTextComponent("§cYou do not have perms for your talking group!"));
            event.setCanceled(true);
            return;
        }

        if (groupSettings.getIgnoredGroups().contains(groupSettings.getTalkingGroup()))
        {
            groupSettings.removeIgnoredGroup(groupSettings.getTalkingGroup());
            event.getPlayer().sendMessage(new StringTextComponent("Group unmuted"));
        }

        if (connection.getConnectionState() == HubConnectionState.CONNECTED)
        {
            connection.sendGenericMessage(message);
        }

        final ITextComponent messageToSend = new StringTextComponent(ReplacementUtils.getFormat(message, getClient()));

        event.setComponent(messageToSend);

        if (groupConfig.isCancelChatEvent())
        {
            Log.getLogger().info("New Generic Message " + messageToSend.toString());
            event.setCanceled(true);

            for (final ServerPlayerEntity player: groupConfig.getPlayersListening())
            {
                player.sendMessage(messageToSend);
            }
        }
    }

    @SubscribeEvent
    public void playerLoggedIn(PlayerEvent.PlayerLoggedInEvent event)
    {
        if (event.getPlayer() != null && !event.getPlayer().world.isRemote && connection.getConnectionState() == HubConnectionState.CONNECTED)
        {
            final User user = new User(event.getPlayer().getName().getString(), event.getPlayer().getUniqueID().toString());

            connection.sendUserEventMessage(new UserEventMessage("LOGIN", user));
        }
    }

    @SubscribeEvent
    public void playerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event)
    {
        if (event.getPlayer() != null && !event.getPlayer().world.isRemote && connection.getConnectionState() == HubConnectionState.CONNECTED)
        {
            final User user = new User(event.getPlayer().getName().getString(), event.getPlayer().getUniqueID().toString());

            connection.sendUserEventMessage(new UserEventMessage("LOGOUT", user));
        }
    }

    @SubscribeEvent
    public void playerDied(LivingDeathEvent event)
    {
        if (event.getEntity() instanceof PlayerEntity
                && !event.getEntity().world.isRemote && connection.getConnectionState() == HubConnectionState.CONNECTED)
        {
            final User user = new User(event.getEntity().getName().getString(), event.getEntity().getUniqueID().toString());

            connection.sendUserEventMessage(new UserEventMessage("DEATH", user));
        }
    }

    @SuppressWarnings("unchecked")
    private <M extends AbstractConfig> M getConfig(Path file, Class<M> clazz, ConfigurationLoader loader)
    {
        try
        {
            if (!file.toFile().exists())
            {
                Files.createFile(file);
            }

            @SuppressWarnings("UnstableApiUsage") TypeToken token = TypeToken.of(clazz);
            ConfigurationNode node = loader.load(ConfigurationOptions.defaults());
            M config = (M) node.getValue(token, clazz.newInstance());
            config.init(loader, node, token);
            config.save();
            return config;
        } catch (IOException | ObjectMappingException | IllegalAccessException | InstantiationException e)
        {
            Log.getLogger().warn("Getting the config failed", e);
            return null;
        }
    }

    public void reloadConfigs()
    {
        if (!configDir.exists())
        {
            //noinspection ResultOfMethodCallIgnored
            configDir.mkdirs();

            if (!configDir.getParentFile().exists())
            {
                Log.getLogger().error("Couldn't create config directory!", new IOException());
            }
        }

        //noinspection UnstableApiUsage
        TypeSerializers.getDefaultSerializers().registerType(TypeToken.of(Group.class), new GroupTypeSerializer());

        final Path mainConfigPath = configDir.toPath().resolve("main.json");
        mainConfig = getConfig(mainConfigPath, MainConfig.class,
                GsonConfigurationLoader.builder().setPath(mainConfigPath).build());

        final Path groupsConfigPath = configDir.toPath().resolve("groups.json");
        groupsConfig = getConfig(groupsConfigPath, GroupsConfig.class,
                GsonConfigurationLoader.builder().setPath(groupsConfigPath).build());

        final Path formattingConfigPath = configDir.toPath().resolve("formatting.json");
        formattingConfig = getConfig(formattingConfigPath, FormattingConfig.class,
                GsonConfigurationLoader.builder().setPath(formattingConfigPath).build());

        final Path advancedFormattingConfigPath = configDir.toPath().resolve("advanced-formatting.json");
        advancedFormattingConfig = getConfig(advancedFormattingConfigPath, AdvancedFormattingConfig.class,
                GsonConfigurationLoader.builder().setPath(advancedFormattingConfigPath).build());
    }
}
