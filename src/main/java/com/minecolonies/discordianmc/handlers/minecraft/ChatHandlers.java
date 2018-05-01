package com.minecolonies.discordianmc.handlers.minecraft;

import com.minecolonies.discordianconnect.api.connection.ConnectionState;
import com.minecolonies.discordianmc.APIChannels;
import com.minecolonies.discordianmc.DiscordianMC;
import com.minecolonies.discordianmc.util.APIMesssages;
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
        if (DiscordianMC.instance.getConnection() != null
              && DiscordianMC.instance.getConnection().getConnectionState().equals(ConnectionState.OPEN))
        {
            APIMesssages.chatMessage(APIChannels.MAIN, event.getPlayer().getName(), event.getMessage());
        }
    }
}
