package com.ldt.chatchainmc.core;

import com.minecolonies.chatchainconnect.api.connection.IChatChainConnectConnection;
import com.minecolonies.chatchainconnect.connection.ChatChainConnectConnection;
import com.minecolonies.chatchainconnect.connection.ChatChainConnectConnectionBuilder;
import lombok.Getter;
import lombok.NonNull;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

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

    /**
     * This is the first initialization event. Register tile entities here.
     * The registry events below will have fired prior to entry to this method.
     */
    @Mod.EventHandler
    public void preinit(FMLPreInitializationEvent event)
    {
        logger = event.getModLog();

        IChatChainConnectConnection connection = null;

        try
        {
            connection = new ChatChainConnectConnectionBuilder()
                    .connectTo(new URL("http://localhost:5000/hubs/chatchain"))
                    .withEventHandler( builder ->
                            builder.registerMessageHandler("ConnectionEvent", message -> logger.info("message: " + message.getArguments()))
                            )
                    /*.usingAuthentication( builder ->
                            builder.withBearerToken()
                            )*/
                    .build();
        }
        catch (MalformedURLException e)
        {
            logger.error("Malformed Url: ", e);
        }

        if (connection != null)
        {
            connection.connect();
        }
    }
}
