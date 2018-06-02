package com.minecolonies.chatchainmc.coremod;

import com.google.common.reflect.TypeToken;
import com.minecolonies.chatchainconnect.ChatChainConnectAPI;
import com.minecolonies.chatchainconnect.api.connection.IChatChainConnectConnection;
import com.minecolonies.chatchainconnect.api.connection.auth.IChatChainConnectAuthenticationBuilder;
import com.minecolonies.chatchainmc.api.events.connection.ConnectionOpenEvent;
import com.minecolonies.chatchainmc.api.events.connection.ConnectionSetEventHandler;
import com.minecolonies.chatchainmc.coremod.commands.CommandEntryPoint;
import com.minecolonies.chatchainmc.coremod.config.BaseConfig;
import com.minecolonies.chatchainmc.coremod.config.ClientConfigs;
import com.minecolonies.chatchainmc.coremod.config.MainConfig;
import com.minecolonies.chatchainmc.coremod.config.TemplatesConfig;
import com.minecolonies.chatchainmc.coremod.handlers.api.GenericHandlers;
import com.minecolonies.chatchainmc.coremod.util.APIMesssages;
import lombok.Getter;
import lombok.NonNull;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

@Mod(
  modid = ChatChainMC.MOD_ID,
  name = ChatChainMC.MOD_NAME,
  version = ChatChainMC.VERSION,
  acceptableRemoteVersions = "*"
)
public class ChatChainMC
{

    public static final String MOD_ID   = "chatchainmc";
    public static final String MOD_NAME = "ChatChainMC";
    public static final String VERSION  = "1.0-SNAPSHOT";
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
            configDir.mkdirs();

            if (!configDir.getParentFile().exists())
            {
                logger.error("Couldn't create config directory!", new IOException());
            }
        }

        final Path mainConfigPath = configDir.toPath().resolve("main.conf");
        final Path templateConfigPath = configDir.toPath().resolve("templates.conf");
        final Path clientConfigsPath = configDir.toPath().resolve("clients.conf");

        mainConfig = getConfig(mainConfigPath, MainConfig.class,
          HoconConfigurationLoader.builder().setPath(mainConfigPath).build());

        templatesConfig = getConfig(templateConfigPath, TemplatesConfig.class,
          HoconConfigurationLoader.builder().setPath(templateConfigPath).build());

        clientConfigs = getConfig(clientConfigsPath, ClientConfigs.class,
          HoconConfigurationLoader.builder().setPath(clientConfigsPath).build());
    }

    public void reloadConfigs()
    {
        final Path mainConfigPath = configDir.toPath().resolve("main.conf");
        final Path templateConfigPath = configDir.toPath().resolve("templates.conf");
        final Path clientConfigsPath = configDir.toPath().resolve("clients.conf");

        mainConfig = getConfig(mainConfigPath, MainConfig.class,
          HoconConfigurationLoader.builder().setPath(mainConfigPath).build());

        templatesConfig = getConfig(templateConfigPath, TemplatesConfig.class,
          HoconConfigurationLoader.builder().setPath(templateConfigPath).build());

        clientConfigs = getConfig(clientConfigsPath, ClientConfigs.class,
          HoconConfigurationLoader.builder().setPath(clientConfigsPath).build());
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
     * (Re)conect to the ChatChainServer API.
     */
    public synchronized void connectToAPI()
    {
        logger.info("Connecting to API");

        if (connection != null)
        {
            APIMesssages.serverStop(APIChannels.MAIN);
            connection.disconnect();
        }

        URL apiURL = null;

        try
        {
            apiURL = new URL(mainConfig.apiUrl + mainConfig.apiHub);
        }
        catch (MalformedURLException e)
        {
            getLogger().error("Unable to create url: ", e);
        }

        if (apiURL != null)
        {
            final URL finalApiURL = apiURL;

            connection = ChatChainConnectAPI.getInstance().getNewConnection(builder -> {

                builder.connectTo(finalApiURL);

                builder.usingAuthentication(IChatChainConnectAuthenticationBuilder::withNoAuthentication);

                builder.withEventHandler(eventBuilder -> {
                    MinecraftForge.EVENT_BUS.post(new ConnectionSetEventHandler(eventBuilder));
                    eventBuilder.registerMessageHandler("GenericConnectionEvent", GenericHandlers::genericConnectionEvent);
                    eventBuilder.registerMessageHandler("GenericDisconnectionEvent", GenericHandlers::genericDisconnectionEvent);
                    eventBuilder.registerMessageHandler("GenericMessageEvent", GenericHandlers::genericMessageEvent);
                    eventBuilder.registerMessageHandler("GenericJoinEvent", GenericHandlers::genericJoinEvent);
                    eventBuilder.registerMessageHandler("GenericLeaveEvent", GenericHandlers::genericLeaveEvent);
                    eventBuilder.registerMessageHandler("RequestJoined", GenericHandlers::requestJoined);
                    eventBuilder.registerMessageHandler("RespondJoined", GenericHandlers::respondJoined);
                });

                builder.withErrorHandler(errorBuilder -> errorBuilder.registerHandler(this::errorHandler));
            });

            connection.connect(() -> APIMesssages.serverStart(APIChannels.MAIN));

            //MinecraftForge.EVENT_BUS.post(new ConnectionOpenEvent());
        }

        logger.info("Successfully connected to API!");
    }

    /**
     * This is where we connect to the api and send
     * the various messages needed when the server starts!
     */
    @Mod.EventHandler
    @SuppressWarnings("squid:S2589")
    public synchronized void serverStart(FMLServerStartingEvent event)
    {
        server = event.getServer();

        event.registerServerCommand(new CommandEntryPoint());

        connectToAPI();
    }

    /**
     * Used to set our API Auth token. This is retrieved from the config.
     *
     * @param builder the Authentication builder used.
     */
    private void setAuthenticationToken(IChatChainConnectAuthenticationBuilder builder)
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
        APIMesssages.serverStop(APIChannels.MAIN);
        connection.disconnect();
        logger.info("Successfully Disconnected from API!");
    }
}
