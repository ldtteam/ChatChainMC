package com.minecolonies.chatchainmc.core.handlers.minecraft;

import com.google.common.base.Joiner;
import com.minecolonies.chatchainconnect.api.objects.User;
import com.minecolonies.chatchainmc.core.ChatChainMC;
import com.minecolonies.chatchainconnect.api.connection.ConnectionState;
import com.minecolonies.chatchainmc.core.APIChannels;
import com.minecolonies.chatchainmc.core.util.APIMesssages;
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
            final User user = new User();
            user.setName(event.getPlayer().getName());
            user.setAvatarURL("https://crafatar.com/avatars/" + event.getPlayer().getUniqueID().toString());

            APIMesssages.chatMessage(APIChannels.MAIN, user, event.getMessage());
        }
    }

    @SubscribeEvent
    public static void onChatMessage(CommandEvent event)
    {
        if (ChatChainMC.instance.getConnection() != null
              && ChatChainMC.instance.getConnection().getConnectionState().equals(ConnectionState.OPEN)
              && event.getCommand().getName().equalsIgnoreCase("say"))
        {
            final User user = new User();
            user.setName("Server");
            user.setAvatarURL("https://cdn.discordapp.com/channel-icons/354208766285185027/6fd2f2d04ef1c4a79a970ae96af42e75");

            final String message = Joiner.on(" ").join(event.getParameters());
            APIMesssages.chatMessage(APIChannels.MAIN, user, message);
        }
    }
}
