package co.chatchain.mc.forge;

import co.chatchain.commons.ChatChainHubConnection;
import co.chatchain.commons.HubModule;
import co.chatchain.commons.configuration.AbstractConfig;
import co.chatchain.commons.configuration.ConfigurationModule;
import co.chatchain.commons.core.CoreModule;
import co.chatchain.commons.core.entities.Client;
import co.chatchain.commons.core.entities.ClientRank;
import co.chatchain.commons.core.entities.Group;
import co.chatchain.commons.core.entities.ClientUser;
import co.chatchain.commons.core.entities.messages.*;
import co.chatchain.commons.core.entities.requests.GenericMessageRequest;
import co.chatchain.commons.core.entities.requests.UserEventRequest;
import co.chatchain.commons.core.interfaces.formatters.IGenericMessageFormatter;
import co.chatchain.commons.infrastructure.formatters.GenericMessageFormatter;
import co.chatchain.mc.forge.capabilities.GroupProvider;
import co.chatchain.mc.forge.capabilities.IGroupSettings;
import co.chatchain.mc.forge.commands.BaseCommand;
import co.chatchain.mc.forge.compatibility.sponge.ChatChainSpongePlugin;
import co.chatchain.mc.forge.configs.*;
import co.chatchain.mc.forge.serializers.GroupTypeSerializer;
import com.google.common.reflect.TypeToken;
import com.google.inject.Guice;
import com.google.inject.Injector;
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
import ninja.leaping.configurate.gson.GsonConfigurationLoader;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializers;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
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

    @Getter
    private ChatChainHubConnection connection = null;

    @Getter
    private MainConfig mainConfig;

    @Getter
    private GroupsConfig groupsConfig;

    @Getter
    private Injector injector;

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

        //noinspection UnstableApiUsage
        TypeSerializers.getDefaultSerializers().registerType(TypeToken.of(Group.class), new GroupTypeSerializer());

        final Path mainConfigPath = configDir.toPath().resolve("main.json");
        mainConfig = AbstractConfig.getConfig(mainConfigPath, MainConfig.class,
                GsonConfigurationLoader.builder().setPath(mainConfigPath).build());

        final Path groupsConfigPath = configDir.toPath().resolve("groups.json");
        groupsConfig = AbstractConfig.getConfig(groupsConfigPath, GroupsConfig.class,
                GsonConfigurationLoader.builder().setPath(groupsConfigPath).build());

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

        Path formattingConfigPath = configDir.toPath().resolve("formatting.json");
        if (mainConfig.getAdvancedFormatting())
        {
            formattingConfigPath = configDir.toPath().resolve("advanced-formatting.json");
        }

        injector = Guice.createInjector(new HubModule(), new CoreModule(), new ConfigurationModule(formattingConfigPath, mainConfig.getAdvancedFormatting()), new ChatChainMCModule());

        connection = injector.getInstance(ChatChainHubConnection.class);
        connection.connect(false);

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
            String userColour = null;
            if (ChatChainMC.instance.isSpongeIsPresent() && ChatChainMC.instance.getMainConfig().isUseSponge())
            {
                clientRanks.addAll(ChatChainSpongePlugin.getPlayerRanks(event.getPlayer()));
                userColour = ChatChainSpongePlugin.getPlayerColour(event.getPlayer());
            }

            final ClientUser user = new ClientUser(event.getUsername(), event.getPlayer().getUniqueID().toString(), null, userColour, clientRanks);

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

            final GenericMessageRequest request = new GenericMessageRequest(groupSettings.getTalkingGroup().getId(), event.getMessage(), user);

            final GroupConfig groupConfig = ChatChainMC.instance.getGroupsConfig().getGroupStorage().get(groupSettings.getTalkingGroup().getId());

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
                ChatChainMC.instance.connection.sendGenericMessage(request);
            }

            Client client = ChatChainMC.instance.connection.getClient();
            if (client == null)
            {
                client = new Client("client-id", "owner-id", ChatChainMC.instance.getMainConfig().getClientNameIfOffline(), null);
            }

            final GenericMessageMessage message = new GenericMessageMessage(client, client.getId(), groupSettings.getTalkingGroup(), event.getMessage(), user);

            final IGenericMessageFormatter formatter = ChatChainMC.instance.injector.getInstance(GenericMessageFormatter.class);
            final ITextComponent messageToSend = new TextComponentString(formatter.format(message));

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
            final List<ClientRank> clientRanks = new ArrayList<>();
            String userColour = null;
            if (ChatChainMC.instance.isSpongeIsPresent() && ChatChainMC.instance.getMainConfig().isUseSponge())
            {
                clientRanks.addAll(ChatChainSpongePlugin.getPlayerRanks(event.player));
                userColour = ChatChainSpongePlugin.getPlayerColour(event.player);
            }

            final ClientUser user = new ClientUser(event.player.getName(), event.player.getUniqueID().toString(), null, userColour, clientRanks);

            ChatChainMC.instance.connection.sendUserEventMessage(new UserEventRequest(user, "LOGIN", null));
        }
    }

    @SubscribeEvent
    public static void playerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event)
    {
        if (event.player != null && !event.player.world.isRemote && ChatChainMC.instance.connection.getConnectionState() == HubConnectionState.CONNECTED)
        {
            final List<ClientRank> clientRanks = new ArrayList<>();
            String userColour = null;
            if (ChatChainMC.instance.isSpongeIsPresent() && ChatChainMC.instance.getMainConfig().isUseSponge())
            {
                clientRanks.addAll(ChatChainSpongePlugin.getPlayerRanks(event.player));
                userColour = ChatChainSpongePlugin.getPlayerColour(event.player);
            }

            final ClientUser user = new ClientUser(event.player.getName(), event.player.getUniqueID().toString(), null, userColour, clientRanks);

            ChatChainMC.instance.connection.sendUserEventMessage(new UserEventRequest(user, "LOGOUT", null));
        }
    }

    @SubscribeEvent
    public static void playerDied(LivingDeathEvent event)
    {
        if (event.getEntity() instanceof EntityPlayer
                && !event.getEntity().world.isRemote && ChatChainMC.instance.connection.getConnectionState() == HubConnectionState.CONNECTED)
        {
            final EntityPlayer player = (EntityPlayer) event.getEntity();

            final List<ClientRank> clientRanks = new ArrayList<>();
            String userColour = null;
            if (ChatChainMC.instance.isSpongeIsPresent() && ChatChainMC.instance.getMainConfig().isUseSponge())
            {
                clientRanks.addAll(ChatChainSpongePlugin.getPlayerRanks(player));
                userColour = ChatChainSpongePlugin.getPlayerColour(player);
            }

            final ClientUser user = new ClientUser(player.getName(), player.getUniqueID().toString(), null, userColour, clientRanks);

            ChatChainMC.instance.connection.sendUserEventMessage(new UserEventRequest(user, "DEATH", null));
        }
    }

    public void reloadConfigs()
    {
        final Path mainConfigPath = configDir.toPath().resolve("main.json");
        mainConfig = AbstractConfig.getConfig(mainConfigPath, MainConfig.class,
                GsonConfigurationLoader.builder().setPath(mainConfigPath).build());

        final Path groupsConfigPath = configDir.toPath().resolve("groups.json");
        groupsConfig = AbstractConfig.getConfig(groupsConfigPath, GroupsConfig.class,
                GsonConfigurationLoader.builder().setPath(groupsConfigPath).build());
    }
}
