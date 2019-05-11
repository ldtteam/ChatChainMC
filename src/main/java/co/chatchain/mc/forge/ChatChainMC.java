package co.chatchain.mc.forge;

import co.chatchain.commons.AccessTokenResolver;
import co.chatchain.commons.ChatChainHubConnection;
import co.chatchain.commons.messages.objects.Client;
import co.chatchain.commons.messages.objects.ClientRank;
import co.chatchain.commons.messages.objects.Group;
import co.chatchain.commons.messages.objects.User;
import co.chatchain.commons.messages.objects.messages.*;
import co.chatchain.mc.forge.capabilities.GroupProvider;
import co.chatchain.mc.forge.capabilities.IGroupSettings;
import co.chatchain.mc.forge.commands.BaseCommand;
import co.chatchain.mc.forge.compatibility.sponge.ChatChainSpongePlugin;
import co.chatchain.mc.forge.configs.*;
import co.chatchain.mc.forge.configs.formatting.AdvancedFormattingConfig;
import co.chatchain.mc.forge.configs.formatting.ReplacementUtils;
import co.chatchain.mc.forge.configs.formatting.FormattingConfig;
import co.chatchain.mc.forge.message.handling.APIMessages;
import co.chatchain.mc.forge.serializers.GroupTypeSerializer;
import com.google.common.reflect.TypeToken;
import com.microsoft.signalr.HubConnectionState;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.gson.GsonConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializers;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Mod(
        modid = ChatChainMC.MOD_ID,
        name = ChatChainMC.MOD_NAME,
        version = ChatChainMC.VERSION,
        acceptableRemoteVersions = "*"
)
@Mod.EventBusSubscriber
public class ChatChainMC
{

    public static final String MOD_ID = "chatchainmc";
    public static final String MOD_NAME = "ChatChainMC";
    public static final String VERSION = "1.0-SNAPSHOT";

    /**
     * This is the instance of your mod as created by Forge. It will never be null.
     */
    @SuppressWarnings("squid:S1444")
    @Mod.Instance(MOD_ID)
    @NonNull
    public static ChatChainMC instance;

    @Getter
    private Logger logger = null;

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

    @Getter
    @Setter
    private boolean spongeIsPresent;

    private File configDir;

    @Mod.EventHandler
    public synchronized void preinit(FMLPreInitializationEvent event)
    {
        logger = event.getModLog();
        configDir = event.getModConfigurationDirectory().toPath().resolve(MOD_NAME).toFile();

        if (!configDir.exists())
        {
            //noinspection ResultOfMethodCallIgnored
            configDir.mkdirs();

            if (!configDir.getParentFile().exists())
            {
                logger.error("Couldn't create config directory!", new IOException());
            }
        }

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

        CapabilityManager.INSTANCE.register(IGroupSettings.class, new IGroupSettings.Storage(), new IGroupSettings.Factory());
    }

    @SubscribeEvent
    public static void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event)
    {
        if (event.getObject() instanceof EntityPlayer)
        {
            event.addCapability(new ResourceLocation(MOD_ID, "groupsettings"), new GroupProvider());
        }
    }

    @Mod.EventHandler
    public synchronized void serverStart(FMLServerStartingEvent event)
    {
        try
        {
            accessToken = new AccessTokenResolver(mainConfig.getClientId(), mainConfig.getClientSecret(), mainConfig.getIdentityUrl());
        } catch (IOException e)
        {
            logger.error("Exception while attempting to get ChatChain Access Token from IdentityServer", e);
        }

        connection = new ChatChainHubConnection(mainConfig.getApiUrl(), accessToken);
        connection.connect();

        logger.info("Connection Status: " + connection.getConnectionState());

        connection.onConnection(hub -> {
            hub.onGenericMessage(APIMessages::ReceiveGenericMessage, GenericMessage.class);
            hub.onClientEventMessage(APIMessages::ReceiveClientEvent, ClientEventMessage.class);
            hub.onUserEventMessage(APIMessages::ReceiveUserEvent, UserEventMessage.class);
            hub.onGetGroupsResponse(APIMessages::ReceiveGroups, GetGroupsResponse.class);
            hub.onGetClientResponse(APIMessages::ReceiveClient, GetClientResponse.class);

            hub.sendGetGroups();
            hub.sendGetClient();
            hub.sendClientEventMessage(new ClientEventMessage("START"));
        });

        event.registerServerCommand(new BaseCommand());
    }

    @Mod.EventHandler
    public synchronized void serverStop(FMLServerStoppingEvent event)
    {
        connection.disconnect();
    }

    @SubscribeEvent
    public static void mcChatMessage(ServerChatEvent event)
    {
        final IGroupSettings groupSettings = event.getPlayer().getCapability(GroupProvider.GROUP_SETTINGS_CAP, null);

        if (groupSettings != null)
        {
            final List<ClientRank> clientRanks = new ArrayList<>();
            if (ChatChainMC.instance.isSpongeIsPresent() && ChatChainMC.instance.getMainConfig().isUseSponge())
            {
                clientRanks.addAll(ChatChainSpongePlugin.getPlayerRank(event.getPlayer()));
            }
            else
            {
                ChatChainMC.instance.getLogger().info("SPONGE IS NOT PRESENT");
            }

            final User user = new User(event.getUsername(), event.getPlayer().getUniqueID().toString(), null, clientRanks);

            final GenericMessage message = new GenericMessage(groupSettings.getTalkingGroup(), user, event.getMessage(), false);

            if (groupSettings.getTalkingGroup() == null)
            {
                final String defaultGroupString = ChatChainMC.instance.getGroupsConfig().getDefaultGroup();
                if (defaultGroupString != null && !defaultGroupString.isEmpty())
                {
                    final GroupConfig groupConfig = ChatChainMC.instance.getGroupsConfig().getGroupStorage().get(defaultGroupString);
                    if (groupConfig.getPlayersCanTalk().contains(event.getPlayer()))
                    {
                        groupSettings.setTalkingGroup(groupConfig.getGroup());
                    }
                    else
                    {
                        event.getPlayer().sendMessage(new TextComponentString("§cYou do not have perms for default group in the config!"));
                        event.setCanceled(true);
                        return;
                    }
                }
                else
                {
                    event.getPlayer().sendMessage(new TextComponentString("§cPlease set a default group for chat in the config!"));
                    event.setCanceled(true);
                    return;
                }
            }

            final GroupConfig groupConfig = ChatChainMC.instance.getGroupsConfig().getGroupStorage().get(groupSettings.getTalkingGroup().getGroupId());

            if (!groupConfig.getPlayersCanTalk().contains(event.getPlayer()))
            {
                event.getPlayer().sendMessage(new TextComponentString("§cYou do not have perms for your talking group!"));
                event.setCanceled(true);
                return;
            }

            if (groupSettings.getIgnoredGroups().contains(groupSettings.getTalkingGroup()))
            {
                groupSettings.removeIgnoredGroup(groupSettings.getTalkingGroup());
                event.getPlayer().sendMessage(new TextComponentString("Group unmuted"));
            }

            if (ChatChainMC.instance.connection.getConnectionState() == HubConnectionState.CONNECTED)
            {
                ChatChainMC.instance.connection.sendGenericMessage(message);
            }

            final ITextComponent messageToSend = new TextComponentString(ReplacementUtils.getFormat(message, ChatChainMC.instance.getClient()));

            event.setComponent(messageToSend);

            if (groupConfig.isCancelChatEvent())
            {
                ChatChainMC.instance.getLogger().info("New Generic Message " + messageToSend.toString());
                event.setCanceled(true);

                for (final EntityPlayer player: groupConfig.getPlayersListening())
                {
                    player.sendMessage(messageToSend);
                }
            }
        }
    }

    @SubscribeEvent
    public static void playerLoggedIn(PlayerEvent.PlayerLoggedInEvent event)
    {
        if (event.player != null && !event.player.world.isRemote && ChatChainMC.instance.connection.getConnectionState() == HubConnectionState.CONNECTED)
        {
            final User user = new User(event.player.getName(), event.player.getUniqueID().toString());

            ChatChainMC.instance.connection.sendUserEventMessage(new UserEventMessage("LOGIN", user));
        }
    }

    @SubscribeEvent
    public static void playerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event)
    {
        if (event.player != null && !event.player.world.isRemote && ChatChainMC.instance.connection.getConnectionState() == HubConnectionState.CONNECTED)
        {
            final User user = new User(event.player.getName(), event.player.getUniqueID().toString());

            ChatChainMC.instance.connection.sendUserEventMessage(new UserEventMessage("LOGOUT", user));
        }
    }

    @SubscribeEvent
    public static void playerDied(LivingDeathEvent event)
    {
        if (event.getEntity() instanceof EntityPlayer
                && !event.getEntity().world.isRemote && ChatChainMC.instance.connection.getConnectionState() == HubConnectionState.CONNECTED)
        {
            final User user = new User(event.getEntity().getName(), event.getEntity().getUniqueID().toString());

            ChatChainMC.instance.connection.sendUserEventMessage(new UserEventMessage("DEATH", user));
        }
    }

    /*@SubscribeEvent TODO: Disabled until i can figure out a better way to do this, currently the names for advancements aren't always there (some have names, some dont.....)
    public static void playerAdvancement(AdvancementEvent event)
    {
        if (event.getEntityPlayer() != null
                && !event.getEntity().world.isRemote && ChatChainMC.instance.connection.getConnection().getConnectionState() == HubConnectionState.CONNECTED)
        {
            final User user = new User(event.getEntity().getName());

            final Map<String, String> extraEventData = new HashMap<>();
            if (event.getAdvancement().getDisplay() != null)
            {
                extraEventData.put("achievement-name", event.getAdvancement().getDisplayText().getUnformattedText());
                ChatChainMC.instance.getLogger().info("achievement: " + event.getAdvancement().getDisplay().getTitle().getFormattedText());
            }

            final UserEventMessage messages = new UserEventMessage("ACHIEVEMENT", user, false, extraEventData);
            //messages.getExtraEventData().put("ACHIEVEMENT_NAME", event.getAdvancement().getDisplayText().getUnformattedText());

            ChatChainMC.instance.connection.sendUserEventMessage(messages);
        }
    }*/

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
            logger.warn("Getting the config failed", e);
            return null;
        }
    }

    public void reloadConfigs()
    {
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
