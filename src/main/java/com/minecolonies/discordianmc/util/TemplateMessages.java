package com.minecolonies.discordianmc.util;

import com.minecolonies.discordianmc.DiscordianMC;

import static com.minecolonies.discordianmc.config.TemplatesConfigReplacements.*;

/**
 * Util class for replacing the various values in TemplatesConfig values.
 */
public class TemplateMessages
{

    private TemplateMessages() {/* Private constructor to hide implicit */}

    public static String getGenericAnyDiscordChatMessage(final String channelID, final String userName, final String message)
    {
        return DiscordianMC.instance.getTemplatesConfig().discordMessage
                 .replace(CHANNEL_ID, channelID)
                 .replace(PLAYER_NAME, userName)
                 .replace(PLAYER_MESSAGE, message);
    }

    public static String getAnyChatMessage(final String serverName, final String playerName, final String message)
    {
        return DiscordianMC.instance.getTemplatesConfig().anyChatMessage
                 .replace(SERVER_NAME, serverName)
                 .replace(PLAYER_NAME, playerName)
                 .replace(PLAYER_MESSAGE, message);
    }

    public static String getAnyPlayerJoin(final String serverName, final String playerName)
    {
        return DiscordianMC.instance.getTemplatesConfig().anyPlayerJoin
                 .replace(SERVER_NAME, serverName)
                 .replace(PLAYER_NAME, playerName);
    }

    public static String getAnyPlayerLeave(final String serverName, final String playerName)
    {
        return DiscordianMC.instance.getTemplatesConfig().anyPlayerLeave
                 .replace(SERVER_NAME, serverName)
                 .replace(PLAYER_NAME, playerName);
    }

    public static String getAnyServerStart(final String serverName)
    {
        return DiscordianMC.instance.getTemplatesConfig().anyServerStart
                 .replace(SERVER_NAME, serverName);
    }

    public static String getAnyServerStop(final String serverName)
    {
        return DiscordianMC.instance.getTemplatesConfig().anyServerStop
                 .replace(SERVER_NAME, serverName);
    }

    public static String getDiscordChatMessage(final String playerName, final String message)
    {
        return DiscordianMC.instance.getTemplatesConfig().discordChatMessage
                 .replace(SERVER_NAME, DiscordianMC.instance.getMainConfig().serverName)
                 .replace(PLAYER_NAME, playerName)
                 .replace(PLAYER_MESSAGE, message);
    }

    public static String getDiscordPlayerJoin(final String playerName)
    {
        return DiscordianMC.instance.getTemplatesConfig().discordPlayerJoin
                 .replace(SERVER_NAME, DiscordianMC.instance.getMainConfig().serverName)
                 .replace(PLAYER_NAME, playerName);
    }

    public static String getDiscordPlayerLeave(final String playerName)
    {
        return DiscordianMC.instance.getTemplatesConfig().discordPlayerLeave
                 .replace(SERVER_NAME, DiscordianMC.instance.getMainConfig().serverName)
                 .replace(PLAYER_NAME, playerName);
    }

    public static String getDiscordServerStart()
    {
        return DiscordianMC.instance.getTemplatesConfig().discordServerStart
                 .replace(SERVER_NAME, DiscordianMC.instance.getMainConfig().serverName);
    }

    public static String getDiscordServerStop()
    {
        return DiscordianMC.instance.getTemplatesConfig().discordServerStop
                 .replace(SERVER_NAME, DiscordianMC.instance.getMainConfig().serverName);
    }
}
