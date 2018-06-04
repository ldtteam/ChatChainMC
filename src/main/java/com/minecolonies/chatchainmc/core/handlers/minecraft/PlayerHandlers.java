package com.minecolonies.chatchainmc.core.handlers.minecraft;

import com.minecolonies.chatchainconnect.api.objects.User;
import com.minecolonies.chatchainmc.core.APIChannels;
import com.minecolonies.chatchainmc.core.util.APIMesssages;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
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
            final User user = new User();
            user.setName(event.player.getName());
            user.setAvatarURL("https://crafatar.com/avatars/" + event.player.getUniqueID().toString());

            APIMesssages.playerJoin(APIChannels.MAIN, user);
        }
    }

    @SubscribeEvent
    public static void onPlayerLeave(PlayerEvent.PlayerLoggedOutEvent event)
    {
        if (event.player != null
              && !event.player.world.isRemote)
        {
            final User user = new User();
            user.setName(event.player.getName());
            user.setAvatarURL("https://crafatar.com/avatars/" + event.player.getUniqueID().toString());

            APIMesssages.playerLeave(APIChannels.MAIN, user);
        }
    }

    public static void onPlayerDeath(LivingDeathEvent event)
    {
        if (event.getEntity() instanceof EntityPlayer
          && !event.getEntity().world.isRemote)
        {
        }
    }
}
