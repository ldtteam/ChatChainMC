package com.minecolonies.discordianmc.handlers.minecraft;

import com.google.common.base.Joiner;
import com.minecolonies.discordianconnect.api.connection.ConnectionState;
import com.minecolonies.discordianmc.APIChannels;
import com.minecolonies.discordianmc.DiscordianMC;
import com.minecolonies.discordianmc.util.APIMesssages;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Arrays;

@Mod.EventBusSubscriber
public class ChatHandlers
{

    private ChatHandlers() { /* private to hide implicit */ }

    @SubscribeEvent
    public static void onChatMessage(ServerChatEvent event)
    {
        if (DiscordianMC.instance.getConnection() != null
              && DiscordianMC.instance.getConnection().getConnectionState().equals(ConnectionState.OPEN))
        {
            APIMesssages.chatMessage(APIChannels.MAIN, event.getPlayer().getName(), event.getMessage());
        }
    }

    @SubscribeEvent
    public static void onChatMessage(CommandEvent event)
    {
        if (DiscordianMC.instance.getConnection() != null
              && DiscordianMC.instance.getConnection().getConnectionState().equals(ConnectionState.OPEN)
              && event.getCommand().getName().equalsIgnoreCase("say"))
        {
            final String message = Joiner.on(" ").join(event.getParameters());
            DiscordianMC.instance.getLogger().info("Command Event, Name: {}, Parameters: {}", event.getSender().getName(), message);
            APIMesssages.chatMessage(APIChannels.MAIN, event.getSender().getName(), message);
        }
    }
}
