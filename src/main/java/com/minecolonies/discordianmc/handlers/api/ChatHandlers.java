package com.minecolonies.discordianmc.handlers.api;

import com.minecolonies.discordianconnect.api.message.IDiscordianConnectMessage;
import com.minecolonies.discordianmc.DiscordianMC;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

public class ChatHandlers
{

    public static void discordMessage(IDiscordianConnectMessage message)
    {
        final ITextComponent text = new TextComponentString("[Discord] "
                                                              + message.getArguments()[1].getAsString()
                                                              + ": "
                                                              + message.getArguments()[2].getAsString());

        DiscordianMC.instance.getServer().getPlayerList().getPlayers().forEach(player -> player.sendMessage(text));
    }
}
