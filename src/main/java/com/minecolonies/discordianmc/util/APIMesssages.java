package com.minecolonies.discordianmc.util;

import com.minecolonies.discordianconnect.api.connection.ConnectionState;
import com.minecolonies.discordianconnect.api.connection.IDiscordianConnectConnection;
import com.minecolonies.discordianmc.DiscordianMC;

public class APIMesssages
{

    private APIMesssages() {/* Private constructor to hide implicit */}

    private static final String GENERIC_DISCORD_MINECRAFT_MESSAGE = "GenericDiscordMinecraftMessage";

    public static void chatMessage(final String author, final String message)
    {
        IDiscordianConnectConnection connection = DiscordianMC.instance.getConnection();

        if (connection.getConnectionState().equals(ConnectionState.OPEN))
        {
            final String serverName = DiscordianMC.instance.getMainConfig().serverName;

            connection.send(GENERIC_DISCORD_MINECRAFT_MESSAGE,
              DiscordianMC.instance.getMainConfig().mainChannel,
              serverName,
              TemplateMessages.getDiscordChatMessage(author, message));
            connection.send("AnyMinecraftChatMessage", serverName, author, message);
        }
    }

    public static void playerJoin(final String username)
    {
        IDiscordianConnectConnection connection = DiscordianMC.instance.getConnection();

        if (connection.getConnectionState().equals(ConnectionState.OPEN))
        {
            final String serverName = DiscordianMC.instance.getMainConfig().serverName;

            connection.send(GENERIC_DISCORD_MINECRAFT_MESSAGE, DiscordianMC.instance.getMainConfig().mainChannel, serverName, TemplateMessages.getDiscordPlayerJoin(username));
            connection.send("AnyMinecraftPlayerJoin", serverName, username);
        }
    }

    public static void playerLeave(final String username)
    {
        IDiscordianConnectConnection connection = DiscordianMC.instance.getConnection();

        if (connection.getConnectionState().equals(ConnectionState.OPEN))
        {
            final String serverName = DiscordianMC.instance.getMainConfig().serverName;

            connection.send(GENERIC_DISCORD_MINECRAFT_MESSAGE, DiscordianMC.instance.getMainConfig().mainChannel, serverName, TemplateMessages.getDiscordPlayerLeave(username));
            connection.send("AnyMinecraftPlayerLeave", serverName, username);
        }
    }

    public static void serverStart()
    {
        IDiscordianConnectConnection connection = DiscordianMC.instance.getConnection();

        if (connection.getConnectionState().equals(ConnectionState.OPEN))
        {
            final String serverName = DiscordianMC.instance.getMainConfig().serverName;

            connection.send(GENERIC_DISCORD_MINECRAFT_MESSAGE, DiscordianMC.instance.getMainConfig().mainChannel, serverName, TemplateMessages.getDiscordServerStart());
            connection.send("AnyMinecraftServerStart", serverName);
        }
    }

    public static void serverStop()
    {
        IDiscordianConnectConnection connection = DiscordianMC.instance.getConnection();

        if (connection.getConnectionState().equals(ConnectionState.OPEN))
        {
            final String serverName = DiscordianMC.instance.getMainConfig().serverName;

            connection.send(GENERIC_DISCORD_MINECRAFT_MESSAGE, DiscordianMC.instance.getMainConfig().mainChannel, serverName, TemplateMessages.getDiscordServerStop());
            connection.send("AnyMinecraftServerStop", serverName);
        }
    }
}
