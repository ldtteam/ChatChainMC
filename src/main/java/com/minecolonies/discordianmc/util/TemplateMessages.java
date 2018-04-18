package com.minecolonies.discordianmc.util;

import com.minecolonies.discordianmc.DiscordianMC;

import static com.minecolonies.discordianmc.config.TemplatesConfigReplacements.*;

/**
 * Util class for replacing the various values in TemplatesConfig values.
 */
public class TemplateMessages
{

    public static String getChatMessage(final String playerName, final String message)
    {
        return DiscordianMC.instance.getTemplatesConfig().chatMessage
                 .replace(SERVER_NAME, DiscordianMC.instance.getMainConfig().serverName)
                 .replace(PLAYER_NAME, playerName)
                 .replace(PLAYER_MESSAGE, message);
    }

    public static String getDiscordMessage(final String channelID, final String userName, final String message)
    {
        return DiscordianMC.instance.getTemplatesConfig().discordMessage
                 .replace(CHANNEL_ID, channelID)
                 .replace(PLAYER_NAME, userName)
                 .replace(PLAYER_MESSAGE, message);
    }


    public static String getPlayerJoin(final String playerName)
    {
        return DiscordianMC.instance.getTemplatesConfig().playerJoin
                 .replace(SERVER_NAME, DiscordianMC.instance.getMainConfig().serverName)
                 .replace(PLAYER_NAME, playerName);
    }

    public static String getPlayerLeave(final String playerName)
    {
        return DiscordianMC.instance.getTemplatesConfig().playerLeave
                 .replace(SERVER_NAME, DiscordianMC.instance.getMainConfig().serverName)
                 .replace(PLAYER_NAME, playerName);
    }

    public static String getServerStart()
    {
        return DiscordianMC.instance.getTemplatesConfig().serverStart
                 .replace(SERVER_NAME, DiscordianMC.instance.getMainConfig().serverName);
    }

    public static String getServerStop()
    {
        return DiscordianMC.instance.getTemplatesConfig().serverStop
                 .replace(SERVER_NAME, DiscordianMC.instance.getMainConfig().serverName);
    }
}
