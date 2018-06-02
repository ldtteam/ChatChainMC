package com.minecolonies.chatchainmc.coremod.handlers.minecraft;

import com.google.common.base.Joiner;
import com.minecolonies.chatchainmc.coremod.ChatChainMC;
import com.minecolonies.chatchainconnect.api.connection.ConnectionState;
import com.minecolonies.chatchainmc.coremod.APIChannels;
import com.minecolonies.chatchainmc.coremod.util.APIMesssages;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class ChatHandlers
{

    private ChatHandlers() { /* private to hide implicit */ }

    @SubscribeEvent
    public static void onChatMessage(ServerChatEvent event)
    {
        if (ChatChainMC.instance.getConnection() != null
              && ChatChainMC.instance.getConnection().getConnectionState().equals(ConnectionState.OPEN))
        {
            APIMesssages.chatMessage(APIChannels.MAIN, event.getPlayer().getName(), event.getMessage());
        }
    }

    @SubscribeEvent
    public static void onChatMessage(CommandEvent event)
    {
        if (ChatChainMC.instance.getConnection() != null
              && ChatChainMC.instance.getConnection().getConnectionState().equals(ConnectionState.OPEN)
              && event.getCommand().getName().equalsIgnoreCase("say"))
        {
            final String message = Joiner.on(" ").join(event.getParameters());
            ChatChainMC.instance.getLogger().info("Command Event, Name: {}, Parameters: {}", event.getSender().getName(), message);
            APIMesssages.chatMessage(APIChannels.MAIN, event.getSender().getName(), message);
        }
    }
}
