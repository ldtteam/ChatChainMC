package com.minecolonies.discordianmc;

import com.google.common.reflect.TypeToken;
import com.minecolonies.discordianconnect.DiscordianConnectAPI;
import com.minecolonies.discordianconnect.api.connection.ConnectionState;
import com.minecolonies.discordianconnect.api.connection.IDiscordianConnectConnection;
import com.minecolonies.discordianconnect.api.connection.auth.IDiscordianConnectAuthenticationBuilder;
import com.minecolonies.discordianmc.config.BaseConfig;
import com.minecolonies.discordianmc.config.MainConfig;
import com.minecolonies.discordianmc.config.TemplatesConfig;
import com.minecolonies.discordianmc.handlers.api.ChatHandlers;
import com.minecolonies.discordianmc.util.TemplateMessages;
import lombok.Getter;
import lombok.NonNull;
import net.minecraft.server.MinecraftServer;
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
  modid = DiscordianMC.MOD_ID,
  name = DiscordianMC.MOD_NAME,
  version = DiscordianMC.VERSION
)
public class DiscordianMC
{

    public static final String MOD_ID   = "discordianmc";
    public static final String MOD_NAME = "DiscordianMC";
    public static final String VERSION  = "1.0-SNAPSHOT";

    /**
     * This is the instance of your mod as created by Forge. It will never be null.
     */
    @SuppressWarnings("squid:S1444")
    @Mod.Instance(MOD_ID)
    @NonNull
    public static DiscordianMC instance;

    @Getter
    private Logger logger = null;

    @Getter
    private IDiscordianConnectConnection connection = null;

    @Getter
    private MinecraftServer server = null;

    @Getter
    private MainConfig mainConfig = null;

    @Getter
    private TemplatesConfig templatesConfig = null;

    /**
     * This is the first initialization event. Register tile entities here.
     * The registry events below will have fired prior to entry to this method.
     */
    @Mod.EventHandler
    public void preinit(FMLPreInitializationEvent event)
    {
        logger = event.getModLog();

        final File configDir = event.getSuggestedConfigurationFile().getParentFile().toPath().resolve(MOD_NAME).toFile();

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

        mainConfig = getConfig(mainConfigPath, MainConfig.class,
          HoconConfigurationLoader.builder().setPath(mainConfigPath).build());

        templatesConfig = getConfig(templateConfigPath, TemplatesConfig.class,
          HoconConfigurationLoader.builder().setPath(templateConfigPath).build());
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
     * This is where we connect to the api and send
     * the various messages needed when the server starts!
     */
    @Mod.EventHandler
    @SuppressWarnings("squid:S2589")
    public synchronized void serverStart(FMLServerStartingEvent event)
    {
        server = event.getServer();

        logger.info("Connecting to API");

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

            connection = DiscordianConnectAPI.getInstance().getNewConnection(builder -> {

                builder.connectTo(finalApiURL);

                builder.usingAuthentication(IDiscordianConnectAuthenticationBuilder::withNoAuthentication);

                builder.withEventHandler(eventBuilder -> eventBuilder.registerMessageHandler("DiscordChatMessage", ChatHandlers::discordMessage));

                builder.withErrorHandler(errorBuilder -> errorBuilder.registerHandler(this::errorHandler));
            });

            connection.connect();
        }

        while (connection.getConnectionState() != ConnectionState.OPEN)
        {
            if (connection.getConnectionState().equals(ConnectionState.CLOSED))
            {
                break;
            }

            try
            {
                wait(1);
            }
            catch (Exception e)
            {
                logger.info("wait failed", e);
            }
        }

        if (connection.getConnectionState().equals(ConnectionState.OPEN))
        {
            connection.send("MinecraftGenericMessage", getMainConfig().mainChannel, getMainConfig().serverName, TemplateMessages.getServerStart());
        }

        logger.info("Successfully connected to API!");
    }

    /**
     * Used to set our API Auth token. This is retrieved from the config.
     *
     * @param builder the Authentication builder used.
     */
    private void setAuthenticationToken(IDiscordianConnectAuthenticationBuilder builder)
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
        if (connection.getConnectionState().equals(ConnectionState.OPEN))
        {
            getConnection().send("MinecraftGenericMessage", getMainConfig().mainChannel, getMainConfig().serverName, TemplateMessages.getServerStop());
        }
        logger.info("Successfully Disconnected from API!");
    }
}
