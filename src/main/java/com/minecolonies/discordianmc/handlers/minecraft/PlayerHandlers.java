package com.minecolonies.discordianmc.handlers.minecraft;

import com.minecolonies.discordianconnect.api.connection.ConnectionState;
import com.minecolonies.discordianmc.DiscordianMC;
import com.minecolonies.discordianmc.config.MainConfig;
import com.minecolonies.discordianmc.util.TemplateMessages;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

/**
 * Class where we handle the Player based events.
 */
@Mod.EventBusSubscriber
public class PlayerHandlers
{

    private PlayerHandlers() { /* private to hide implicit */ }

    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event)
    {
        final MainConfig config = DiscordianMC.instance.getMainConfig();
        if (event.player != null
              && !event.player.world.isRemote
              && DiscordianMC.instance.getConnection() != null
              && DiscordianMC.instance.getConnection().getConnectionState().equals(ConnectionState.OPEN)
              && config != null)
        {
            DiscordianMC.instance.getConnection()
              .send("MinecraftGenericMessage", config.mainChannel, config.serverName, TemplateMessages.getPlayerJoin(event.player.getName()));
        }
    }

    @SubscribeEvent
    public static void onPlayerLeave(PlayerEvent.PlayerLoggedOutEvent event)
    {
        final MainConfig config = DiscordianMC.instance.getMainConfig();
        if (event.player != null
              && !event.player.world.isRemote
              && DiscordianMC.instance.getConnection() != null
              && DiscordianMC.instance.getConnection().getConnectionState().equals(ConnectionState.OPEN)
              && config != null)
        {
            DiscordianMC.instance.getConnection()
              .send("MinecraftGenericMessage", config.mainChannel, config.serverName, TemplateMessages.getPlayerLeave(event.player.getName()));
        }
    }
}
