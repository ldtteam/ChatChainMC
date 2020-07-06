package co.chatchain.mc.forge;

import co.chatchain.commons.ChatChainHubConnection;
import co.chatchain.commons.HubModule;
import co.chatchain.commons.configuration.ConfigurationModule;
import co.chatchain.commons.core.CoreModule;
import co.chatchain.commons.core.entities.Client;
import co.chatchain.commons.core.entities.ClientUser;
import co.chatchain.commons.core.entities.Group;
import co.chatchain.commons.core.entities.messages.GenericMessageMessage;
import co.chatchain.commons.core.entities.requests.GenericMessageRequest;
import co.chatchain.commons.core.entities.requests.events.UserEventRequest;
import co.chatchain.commons.core.interfaces.formatters.IGenericMessageFormatter;
import co.chatchain.commons.infrastructure.formatters.GenericMessageFormatter;
import co.chatchain.mc.forge.capabilities.GroupProvider;
import co.chatchain.mc.forge.capabilities.IGroupSettings;
import co.chatchain.mc.forge.commands.EntryPoint;
import co.chatchain.mc.forge.configs.*;
import co.chatchain.mc.forge.serializers.GroupTypeSerializer;
import co.chatchain.mc.forge.util.Log;
import co.chatchain.mc.forge.util.UserUtils;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.reflect.TypeToken;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.microsoft.signalr.HubConnectionState;
import lombok.Getter;
import lombok.NonNull;
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
import ninja.leaping.configurate.gson.GsonConfigurationLoader;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

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

    @Getter
    private ChatChainHubConnection connection = null;

    @Getter
    private MainConfig mainConfig;

    @Getter
    private GroupsConfig groupsConfig;

    @Getter
    private Injector injector;

    private File configDir;

    /**
     * KV map of RequestIDs to Player UUIDs
     */
    @Getter
    private final Cache<String, UUID> statsRequestsCache = CacheBuilder.newBuilder()
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .build();

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

        Path formattingConfigPath = configDir.toPath().resolve("formatting.json");
        if (mainConfig.getAdvancedFormatting())
        {
            formattingConfigPath = configDir.toPath().resolve("advanced-formatting.json");
        }

        injector = Guice.createInjector(new HubModule(), new CoreModule(), new ConfigurationModule(formattingConfigPath.toFile(), mainConfig.getAdvancedFormatting()), new ChatChainMCModule());

        connection = injector.getInstance(ChatChainHubConnection.class);
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
        final ClientUser user = UserUtils.getClientUserFromPlayer(event.getPlayer());
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

        final GenericMessageRequest request = new GenericMessageRequest(groupSettings.getTalkingGroup().getId(), event.getMessage(), user);

        final GroupConfig groupConfig = getGroupsConfig().getGroupStorage().get(groupSettings.getTalkingGroup().getId());

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
            connection.sendGenericMessage(request);
        }

        Client client = connection.getClient();
        if (client == null)
        {
            client = new Client("client-id", "owner-id", getMainConfig().getClientNameIfOffline(), null);
        }

        final GenericMessageMessage message = new GenericMessageMessage(client, client.getId(), groupSettings.getTalkingGroup(), event.getMessage(), user);

        final IGenericMessageFormatter formatter = injector.getInstance(GenericMessageFormatter.class);
        final ITextComponent messageToSend = new StringTextComponent(formatter.format(message));

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
            final ClientUser user = new ClientUser(event.getPlayer().getName().getString(), event.getPlayer().getUniqueID().toString(), null, null, new ArrayList<>());

            connection.sendUserEventMessage(new UserEventRequest(user, "LOGIN", null));
        }
    }

    @SubscribeEvent
    public void playerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event)
    {
        if (event.getPlayer() != null && !event.getPlayer().world.isRemote && connection.getConnectionState() == HubConnectionState.CONNECTED)
        {
            final ClientUser user = new ClientUser(event.getPlayer().getName().getString(), event.getPlayer().getUniqueID().toString(), null, null, new ArrayList<>());

            connection.sendUserEventMessage(new UserEventRequest(user, "LOGOUT", null));
        }
    }

    @SubscribeEvent
    public void playerDied(LivingDeathEvent event)
    {
        if (event.getEntity() instanceof PlayerEntity
                && !event.getEntity().world.isRemote && connection.getConnectionState() == HubConnectionState.CONNECTED)
        {
            final ClientUser user = new ClientUser(event.getEntity().getName().getString(), event.getEntity().getUniqueID().toString(), null, null, new ArrayList<>());

            connection.sendUserEventMessage(new UserEventRequest(user, "DEATH", null));
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
        mainConfig = AbstractConfig.getConfig(mainConfigPath, MainConfig.class,
                GsonConfigurationLoader.builder().setPath(mainConfigPath).build());

        final Path groupsConfigPath = configDir.toPath().resolve("groups.json");
        groupsConfig = AbstractConfig.getConfig(groupsConfigPath, GroupsConfig.class,
                GsonConfigurationLoader.builder().setPath(groupsConfigPath).build());
    }
}
