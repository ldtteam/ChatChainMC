package co.chatchain.mc;

import co.chatchain.mc.capabilities.GroupProvider;
import co.chatchain.mc.capabilities.IGroupSettings;
import co.chatchain.mc.commands.BaseCommand;
import co.chatchain.mc.configs.AbstractConfig;
import co.chatchain.mc.configs.FormattingConfig;
import co.chatchain.mc.configs.GroupsConfig;
import co.chatchain.mc.configs.MainConfig;
import co.chatchain.mc.message.handling.APIMessages;
import co.chatchain.mc.message.objects.*;
import com.google.common.reflect.TypeToken;
import com.microsoft.signalr.HubConnection;
import com.microsoft.signalr.HubConnectionBuilder;
import com.microsoft.signalr.HubConnectionState;
import io.reactivex.Single;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.gson.GsonConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.StringJoiner;

import static co.chatchain.mc.Constants.*;

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
    @SuppressWarnings("squid:S1192")
    public static final String CLIENT_TYPE = "ChatChainMC";

    /**
     * This is the instance of your mod as created by Forge. It will never be null.
     */
    @SuppressWarnings("squid:S1444")
    @Mod.Instance(MOD_ID)
    @NonNull
    public static ChatChainMC instance;

    @Getter
    private Logger logger = null;

    private String accessToken = "";

    @Getter
    private HubConnection connection = null;

    @Getter
    private MainConfig mainConfig;

    @Getter
    private GroupsConfig groupsConfig;

    @Getter
    private FormattingConfig formattingConfig;

    @Getter
    private MinecraftServer server;

    @Getter
    @Setter
    private Client client;

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

        final Path mainConfigPath = configDir.toPath().resolve("main.json");
        mainConfig = getConfig(mainConfigPath, MainConfig.class,
                GsonConfigurationLoader.builder().setPath(mainConfigPath).build());

        final Path groupsConfigPath = configDir.toPath().resolve("groups.json");
        groupsConfig = getConfig(groupsConfigPath, GroupsConfig.class,
                GsonConfigurationLoader.builder().setPath(groupsConfigPath).build());

        final Path formattingConfigPath = configDir.toPath().resolve("formatting.json");
        formattingConfig = getConfig(formattingConfigPath, FormattingConfig.class,
                GsonConfigurationLoader.builder().setPath(formattingConfigPath).build());

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
        server = event.getServer();
        try
        {
            accessToken = getAccessToken();
        } catch (Exception e)
        {
            logger.error("Exception while attempting to get ChatChain Access Token from IdentityServer", e);
        }

        connection = HubConnectionBuilder.create(mainConfig.getApiUrl() /*"https://api.chatchain.co/hubs/chatchain"*/)
                .withAccessTokenProvider(Single.defer(() -> Single.just(accessToken)))
                .build();
        connection.start().blockingAwait();

        logger.info("Connection Status: " + connection.getConnectionState());

        connection.on("ReceiveGenericMessage", APIMessages::ReceiveGenericMessage, GenericMessage.class);
        connection.on("GetGroupsResponse", APIMessages::GetGroupsResponse, GetGroupsResponseMessage.class);
        connection.on("GetClientResponse", APIMessages::GetClientResponse, GetClientResponseMessage.class);

        connection.send("GetGroups");
        connection.send("GetClient");

        event.registerServerCommand(new BaseCommand());
        //event.registerServerCommand(new ReloadCommand());
    }

    public synchronized void serverStop(FMLServerStoppingEvent event)
    {
        logger.info("FMLServerStoppingEvent");
        connection.stop().blockingAwait();
    }

    @SubscribeEvent
    public static void mcChatMessage(ServerChatEvent event)
    {
        final IGroupSettings groupSettings = event.getPlayer().getCapability(GroupProvider.GROUP_SETTINGS_CAP, null);

        if (groupSettings != null)
        {
            final User user = new User(event.getUsername());

            final GenericMessage message = new GenericMessage(groupSettings.getTalkingGroup(), user, event.getMessage());

            if (groupSettings.getMutedGroups().contains(groupSettings.getTalkingGroup()))
            {
                groupSettings.removeMutedGroup(groupSettings.getTalkingGroup());
                event.getPlayer().sendMessage(new TextComponentString("Group unmuted"));
            }

            if (ChatChainMC.instance.connection.getConnectionState() == HubConnectionState.CONNECTED)
            {
                instance.logger.info("Message Sent");
                ChatChainMC.instance.connection.send("SendGenericMessage", message);
            }

            final ITextComponent messageToSend;

            if (ChatChainMC.instance.getFormattingConfig().getGenericMessageFormats().containsKey(message.getGroup().getGroupId()))
            {
                messageToSend = new TextComponentString(ChatChainMC.instance.getFormattingConfig().getGenericMessageFormats().get(message.getGroup().getGroupId())
                        .replace(GROUP_NAME, message.getGroup().getGroupName())
                        .replace(GROUP_ID, message.getGroup().getGroupId())
                        .replace(USER_NAME, message.getUser().getName())
                        .replace(SENDING_CLIENT_NAME, ChatChainMC.instance.getClient().getClientName())
                        .replace(SENDING_CLIENT_GUID, ChatChainMC.instance.getClient().getClientGuid())
                        .replace(MESSAGE, message.getMessage()));
            } else
            {
                messageToSend = new TextComponentString(ChatChainMC.instance.getFormattingConfig().getDefaultGenericMessageFormat()
                        .replace(GROUP_NAME, message.getGroup().getGroupName())
                        .replace(GROUP_ID, message.getGroup().getGroupId())
                        .replace(USER_NAME, message.getUser().getName())
                        .replace(SENDING_CLIENT_NAME, ChatChainMC.instance.getClient().getClientName())
                        .replace(SENDING_CLIENT_GUID, ChatChainMC.instance.getClient().getClientGuid())
                        .replace(MESSAGE, message.getMessage()));
            }

            event.setComponent(messageToSend);
        }
    }

    private String getAccessToken() throws MalformedURLException, IOException
    {
        System.out.println("Ran Here");

        URL url = new URL(mainConfig.getIdentityUrl() /*"https://identity.chatchain.co/connect/token"*/);

        URLConnection con = url.openConnection();
        HttpURLConnection http = (HttpURLConnection) con;
        http.setRequestMethod("POST");
        http.setDoOutput(true);

        final String clientId = mainConfig.getClientId();//System.getenv("CLIENT_ID");
        final String clientSecret = mainConfig.getClientSecret();//System.getenv("CLIENT_SECRET");

        logger.info("clientId: " + clientId);
        logger.info("clientSecret: " + clientSecret);

        Map<String, String> arguments = new HashMap<>();
        arguments.put("client_id", clientId);
        arguments.put("client_secret", clientSecret);
        arguments.put("grant_type", "client_credentials");
        StringJoiner sj = new StringJoiner("&");
        for (Map.Entry<String, String> entry : arguments.entrySet())
        {
            sj.add(URLEncoder.encode(entry.getKey(), "UTF-8") + "=" + URLEncoder.encode(entry.getValue(), "UTF-8"));
        }
        byte[] out = sj.toString().getBytes(StandardCharsets.UTF_8);
        int length = out.length;
        http.setFixedLengthStreamingMode(length);
        http.connect();
        try (OutputStream os = http.getOutputStream())
        {
            os.write(out);
        }

        Scanner s = new Scanner(http.getInputStream()).useDelimiter("\\A");
        String output = s.hasNext() ? s.next() : "";

        JSONObject jsonObject = new JSONObject(output);

        return jsonObject.getString("access_token");
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

            TypeToken token = TypeToken.of(clazz);
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
    }
}
