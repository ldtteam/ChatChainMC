package com.ldt.chatchainmc.core;

import com.google.common.reflect.TypeToken;
import com.ldt.chatchainmc.api.StaticAPIChannels;
import com.ldt.chatchainmc.api.capabilities.ChannelProvider;
import com.ldt.chatchainmc.api.capabilities.IChannelStorage;
import com.ldt.chatchainmc.api.events.connection.ConnectionOpenEvent;
import com.ldt.chatchainmc.core.commands.CommandEntryPoint;
import com.ldt.chatchainmc.core.config.BaseConfig;
import com.ldt.chatchainmc.core.config.ClientConfigs;
import com.ldt.chatchainmc.core.config.MainConfig;
import com.ldt.chatchainmc.core.config.TemplatesConfig;
import com.ldt.chatchainmc.core.handlers.api.GenericHandlers;
import com.ldt.chatchainmc.core.util.APIMesssages;
import com.ldt.chatchainmc.core.util.Log4JFilter;
import com.minecolonies.chatchainconnect.ChatChainConnectAPI;
import com.minecolonies.chatchainconnect.api.connection.ConnectionState;
import com.minecolonies.chatchainconnect.api.connection.IChatChainConnectConnection;
import com.minecolonies.chatchainconnect.api.connection.auth.IChatChainConnectAuthenticationBuilder;
import lombok.Getter;
import lombok.NonNull;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.AttachCapabilitiesEvent;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

@Mod(
  modid = ChatChainMC.MOD_ID,
  name = ChatChainMC.MOD_NAME,
  version = ChatChainMC.VERSION,
  acceptableRemoteVersions = "*"
)
@Mod.EventBusSubscriber
public class ChatChainMC
{

    public static final String MOD_ID      = "chatchainmc";
    public static final String MOD_NAME    = "ChatChainMC";
    public static final String VERSION     = "1.0-SNAPSHOT";
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

    @Getter
    private IChatChainConnectConnection connection = null;

    @Getter
    private MinecraftServer server = null;

    @Getter
    private MainConfig mainConfig = null;

    @Getter
    private TemplatesConfig templatesConfig = null;

    @Getter
    private ClientConfigs clientConfigs = null;

    private File configDir = null;

    /**
     * This is the first initialization event. Register tile entities here.
     * The registry events below will have fired prior to entry to this method.
     */
    @Mod.EventHandler
    public void preinit(FMLPreInitializationEvent event)
    {
        logger = event.getModLog();

        configDir = event.getSuggestedConfigurationFile().getParentFile().toPath().resolve(MOD_NAME).toFile();

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
        final Path templateConfigPath = configDir.toPath().resolve("templates.json");
        final Path clientConfigsPath = configDir.toPath().resolve("clients.json");

        mainConfig = getConfig(mainConfigPath, MainConfig.class,
          GsonConfigurationLoader.builder().setPath(mainConfigPath).build());

        templatesConfig = getConfig(templateConfigPath, TemplatesConfig.class,
          GsonConfigurationLoader.builder().setPath(templateConfigPath).build());

        clientConfigs = getConfig(clientConfigsPath, ClientConfigs.class,
          GsonConfigurationLoader.builder().setPath(clientConfigsPath).build());

        CapabilityManager.INSTANCE.register(IChannelStorage.class, new IChannelStorage.Storage(), new IChannelStorage.Factory());
    }

    public void reloadConfigs()
    {
        final Path mainConfigPath = configDir.toPath().resolve("main.json");
        final Path templateConfigPath = configDir.toPath().resolve("templates.json");
        final Path clientConfigsPath = configDir.toPath().resolve("clients.json");

        mainConfig = getConfig(mainConfigPath, MainConfig.class,
          GsonConfigurationLoader.builder().setPath(mainConfigPath).build());

        templatesConfig = getConfig(templateConfigPath, TemplatesConfig.class,
          GsonConfigurationLoader.builder().setPath(templateConfigPath).build());

        clientConfigs = getConfig(clientConfigsPath, ClientConfigs.class,
          GsonConfigurationLoader.builder().setPath(clientConfigsPath).build());
    }

    @SuppressWarnings("unchecked")
    public <M extends BaseConfig> M getConfig(Path file, Class<M> clazz, ConfigurationLoader loader)
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
        }
        catch (IOException | ObjectMappingException | IllegalAccessException | InstantiationException e)
        {
            logger.warn("Get Config failed", e);
            return null;
        }
    }

    /**
     * (Re)connect to the ChatChainServer API.
     */
    public synchronized void connectToAPI()
    {
        logger.info("Connecting to API");

        if (connection != null)
        {
            APIMesssages.disconnect(StaticAPIChannels.MAIN);
            connection.disconnect();
        }

        URL apiURL = null;

        try
        {
            apiURL = new URL(mainConfig.apiUrl + mainConfig.apiHub);
        }
        catch (Exception e)
        {
            logger.error("MainConfig: " + mainConfig);
            logger.error("Unable to create url: ", e);
        }

        if (apiURL != null)
        {
            final URL finalApiURL = apiURL;

            connection = ChatChainConnectAPI.getInstance().getNewConnection(builder -> {

                builder.connectTo(finalApiURL);

                builder.usingAuthentication(IChatChainConnectAuthenticationBuilder::withNoAuthentication);

                builder.withEventHandler(eventBuilder -> {
                    eventBuilder.registerMessageHandler("GenericConnectionEvent", GenericHandlers::genericConnectionEvent);
                    eventBuilder.registerMessageHandler("GenericDisconnectionEvent", GenericHandlers::genericDisconnectionEvent);
                    eventBuilder.registerMessageHandler("GenericJoinEvent", GenericHandlers::genericJoinEvent);
                    eventBuilder.registerMessageHandler("GenericLeaveEvent", GenericHandlers::genericLeaveEvent);
                    eventBuilder.registerMessageHandler("GenericMessageEvent", GenericHandlers::genericMessageEvent);
                });

                builder.withErrorHandler(errorBuilder -> errorBuilder.registerHandler(this::errorHandler));

                MinecraftForge.EVENT_BUS.post(new ConnectionOpenEvent(builder));
            });

            new Thread(this::connect).start();
            //ChatChainMC.instance.getServer().addScheduledTask() <- Run something on Minecraft main thread!
        }
        else
        {
            logger.warn("ChatChain could not connect to the API!!");
        }
    }

    private synchronized void connect()
    {
        connection.connect();
        logger.info("Successfully connected to API!");

        while (!connection.getConnectionState().equals(ConnectionState.OPEN)
                 || connection.getConnectionState().equals(ConnectionState.CLOSED)
                 || (connection.getConnectionState().equals(ConnectionState.CLOSING)))
        {
            try
            {
                wait(500);
            }
            catch (Exception e)
            {
                logger.error("Couldn't wait for connection to open", e);
            }
        }

        if (connection.getConnectionState().equals(ConnectionState.OPEN))
        {
            APIMesssages.connect(StaticAPIChannels.MAIN);
        }
    }

    /**
     * This is where we connect to the api and send
     * the various messages needed when the server starts!
     */
    @Mod.EventHandler
    @SuppressWarnings("squid:S2589")
    public synchronized void serverStart(FMLServerStartingEvent event)
    {
        if (mainConfig.bridgeConsole)
        {
            ((LoggerContext) LogManager.getContext(false)).getConfiguration().getRootLogger().addFilter(new Log4JFilter());
        }

        server = event.getServer();

        //event.registerServerCommand(new CommandEntryPoint());
        event.registerServerCommand(new CommandEntryPoint());

        ChatChainMC.instance.connectToAPI();
    }

    @SubscribeEvent
    public static void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event)
    {
        if (event.getObject() instanceof EntityPlayer)
        {
            event.addCapability(new ResourceLocation(MOD_ID, "channelstorage"), new ChannelProvider());
        }
    }

    /**
     * Used to set our API Auth token. This is retrieved from the config.
     *
     * @param builder the Authentication builder used.
     */
    private void setAuthenticationToken(@NotNull IChatChainConnectAuthenticationBuilder builder)
    {
        builder.withBearerToken(mainConfig.apiToken);
    }

    /**
     * How we handle Exceptions from the API client.
     * Honestly, we just log them and move on for now.
     *
     * @param thrown The exception thrown!
     */
    private void errorHandler(Throwable thrown)
    {
        getLogger().error("API ERROR: ", thrown);
    }

    /**
     * This is where we server stops!
     */
    @Mod.EventHandler
    public void serverStop(FMLServerStoppingEvent event)
    {
        logger.info("Disconnecting from API");
        APIMesssages.disconnect(StaticAPIChannels.MAIN);
        connection.disconnect();
        logger.info("Successfully Disconnected from API!");
    }
}
