package com.minecolonies.discordianmc.handlers.minecraft;

import com.minecolonies.discordianconnect.api.connection.ConnectionState;
import com.minecolonies.discordianmc.DiscordianMC;
import com.minecolonies.discordianmc.config.MainConfig;
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
        final MainConfig config = DiscordianMC.instance.getMainConfig();
        if (DiscordianMC.instance.getConnection() != null
              && DiscordianMC.instance.getConnection().getConnectionState().equals(ConnectionState.OPEN)
              && config != null)
        {
            APIMesssages.chatMessage(event.getPlayer().getName(), event.getMessage());
        }
    }
}
