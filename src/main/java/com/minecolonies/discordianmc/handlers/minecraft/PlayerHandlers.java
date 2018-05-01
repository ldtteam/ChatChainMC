package com.minecolonies.discordianmc.handlers.minecraft;

import com.minecolonies.discordianmc.APIChannels;
import com.minecolonies.discordianmc.util.APIMesssages;
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
        if (event.player != null
              && !event.player.world.isRemote)
        {
            APIMesssages.playerJoin(APIChannels.MAIN, event.player.getName());
        }
    }

    @SubscribeEvent
    public static void onPlayerLeave(PlayerEvent.PlayerLoggedOutEvent event)
    {
        if (event.player != null
              && !event.player.world.isRemote)
        {
            APIMesssages.playerLeave(APIChannels.MAIN, event.player.getName());
        }
    }
}
