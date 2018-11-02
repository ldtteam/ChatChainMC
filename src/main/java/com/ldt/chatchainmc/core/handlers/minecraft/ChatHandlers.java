package com.ldt.chatchainmc.core.handlers.minecraft;

import com.google.common.base.Joiner;
import com.ldt.chatchainmc.api.StaticAPIChannels;
import com.ldt.chatchainmc.api.capabilities.ChannelProvider;
import com.ldt.chatchainmc.api.capabilities.IChannelStorage;
import com.ldt.chatchainmc.core.ChatChainMC;
import com.ldt.chatchainmc.core.config.TemplatesConfig;
import com.ldt.chatchainmc.core.util.APIMesssages;
import com.minecolonies.chatchainconnect.api.connection.ConnectionState;
import com.minecolonies.chatchainconnect.api.objects.User;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import static com.ldt.chatchainmc.core.config.TemplatesConfigReplacements.*;

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
            final User user = new User();
            user.setName(event.getPlayer().getName());
            user.setAvatarURL("https://crafatar.com/avatars/" + event.getPlayer().getUniqueID().toString());

            final IChannelStorage channelStorage = event.getPlayer().getCapability(ChannelProvider.CHANNEL_STORAGE_CAP, null);

            if (channelStorage != null)
            {
                final String channelName = channelStorage.getTalkingChannel();

                APIMesssages.chatMessage(channelName, user, event.getMessage());

                event.setCanceled(true);

                final TemplatesConfig config = ChatChainMC.instance.getTemplatesConfig();

                final ITextComponent message;

                if (config.localChannelOverrideMessages.containsKey(channelName))
                {
                    message = new TextComponentString(config.localChannelOverrideMessages.get(channelName)
                                                        .replace(CHANNEL_NAME, channelName)
                                                        .replace(USER_NAME, event.getPlayer().getName())
                                                        .replace(USER_MESSAGE, event.getMessage()));
                }
                else
                {
                    message = event.getComponent();
                }

                for (final EntityPlayer player : ChatChainMC.instance.getServer().getPlayerList().getPlayers())
                {
                    final IChannelStorage playerCap = player.getCapability(ChannelProvider.CHANNEL_STORAGE_CAP, null);

                    if (playerCap != null && playerCap.getListeningChannels().contains(channelName))
                    {
                        player.sendMessage(message);
                    }
                }
            }
            else
            {
                APIMesssages.chatMessage(StaticAPIChannels.MAIN, user, event.getMessage());
            }
        }
    }

    @SubscribeEvent
    public static void onChatMessage(CommandEvent event)
    {
        if (ChatChainMC.instance.getConnection() != null
              && ChatChainMC.instance.getConnection().getConnectionState().equals(ConnectionState.OPEN))
        {
            if (event.getCommand().getName().equalsIgnoreCase("say"))
            {
                final User user = new User();
                user.setName("Server");
                user.setAvatarURL("https://cdn.discordapp.com/channel-icons/354208766285185027/6fd2f2d04ef1c4a79a970ae96af42e75");

                final String message = Joiner.on(" ").join(event.getParameters());
                APIMesssages.chatMessage(StaticAPIChannels.MAIN, user, message);
            }
            else if (event.getCommand().getName().equalsIgnoreCase("op"))
            {
                final String user = event.getParameters()[0];
                final EntityPlayer player = ChatChainMC.instance.getServer().getPlayerList().getPlayerByUsername(user);
                if (player != null)
                {
                    final IChannelStorage channelStorage = player.getCapability(ChannelProvider.CHANNEL_STORAGE_CAP, null);
                    if (channelStorage != null)
                    {
                        channelStorage.addChannel(StaticAPIChannels.STAFF);
                        channelStorage.addLChannel(StaticAPIChannels.STAFF);
                    }
                }
            }
            else if (event.getCommand().getName().equalsIgnoreCase("deop"))
            {
                final String user = event.getParameters()[0];
                final EntityPlayer player = ChatChainMC.instance.getServer().getPlayerList().getPlayerByUsername(user);
                if (player != null)
                {
                    final IChannelStorage channelStorage = player.getCapability(ChannelProvider.CHANNEL_STORAGE_CAP, null);
                    if (channelStorage != null)
                    {
                        channelStorage.setTalkingChannel(StaticAPIChannels.MAIN);
                        channelStorage.removeLChannel(StaticAPIChannels.STAFF);
                        channelStorage.removeChannel(StaticAPIChannels.STAFF);
                    }
                }
            }
        }
    }
}
