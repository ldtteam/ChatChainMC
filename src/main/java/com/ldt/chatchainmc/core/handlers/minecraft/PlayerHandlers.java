package com.ldt.chatchainmc.core.handlers.minecraft;

import com.ldt.chatchainmc.api.StaticAPIChannels;
import com.ldt.chatchainmc.api.capabilities.ChannelProvider;
import com.ldt.chatchainmc.api.capabilities.IChannelStorage;
import com.ldt.chatchainmc.core.ChatChainMC;
import com.ldt.chatchainmc.core.util.APIMesssages;
import com.minecolonies.chatchainconnect.api.objects.User;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
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

            APIMesssages.playerJoin(StaticAPIChannels.MAIN, user);

            final IChannelStorage channelStorage = event.player.getCapability(ChannelProvider.CHANNEL_STORAGE_CAP, null);

            if (channelStorage != null)
            {
                channelStorage.addChannel(StaticAPIChannels.MAIN);
                if (FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList()
                      .canSendCommands((event.player).getGameProfile()))
                {
                    channelStorage.addChannel(StaticAPIChannels.STAFF);
                }

                for (final String channel : channelStorage.getChannels())
                {
                    if (!ChatChainMC.instance.getMainConfig().createdChannels.contains(channel) && !(channel.equalsIgnoreCase(StaticAPIChannels.MAIN) || channel.equalsIgnoreCase(
                      StaticAPIChannels.STAFF)))
                    {
                        channelStorage.getChannels().remove(channel);
                    }
                }
            }
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

            APIMesssages.playerLeave(StaticAPIChannels.MAIN, user);
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
