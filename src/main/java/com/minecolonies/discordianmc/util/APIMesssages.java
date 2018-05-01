package com.minecolonies.discordianmc.util;

import com.minecolonies.discordianconnect.api.connection.ConnectionState;
import com.minecolonies.discordianconnect.api.connection.IDiscordianConnectConnection;
import com.minecolonies.discordianmc.APIChannels;
import com.minecolonies.discordianmc.DiscordianMC;

public class APIMesssages
{

    private APIMesssages() {/* Private constructor to hide implicit */}

    public static void chatMessage(final APIChannels channel, final String author, final String message)
    {
        IDiscordianConnectConnection connection = DiscordianMC.instance.getConnection();

        if (connection.getConnectionState().equals(ConnectionState.OPEN))
        {
            final String clientName = DiscordianMC.instance.getMainConfig().clientName;
            connection.send("GenericMessageEvent", DiscordianMC.CLIENT_TYPE, clientName, channel.getName(), author, message);
        }
    }

    public static void playerJoin(final APIChannels channel, final String username)
    {
        IDiscordianConnectConnection connection = DiscordianMC.instance.getConnection();

        if (connection.getConnectionState().equals(ConnectionState.OPEN))
        {
            final String clientName = DiscordianMC.instance.getMainConfig().clientName;
            connection.send("GenericJoinEvent", DiscordianMC.CLIENT_TYPE, clientName, channel.getName(), username);
        }
    }

    public static void playerLeave(final APIChannels channel, final String username)
    {
        IDiscordianConnectConnection connection = DiscordianMC.instance.getConnection();

        if (connection.getConnectionState().equals(ConnectionState.OPEN))
        {
            final String clientName = DiscordianMC.instance.getMainConfig().clientName;
            connection.send("GenericLeaveEvent", DiscordianMC.CLIENT_TYPE, clientName, channel.getName(), username);
        }
    }

    public static void serverStart(final APIChannels channel)
    {
        IDiscordianConnectConnection connection = DiscordianMC.instance.getConnection();

        if (connection.getConnectionState().equals(ConnectionState.OPEN))
        {
            final String clientName = DiscordianMC.instance.getMainConfig().clientName;
            connection.send("GenericConnectionEvent", DiscordianMC.CLIENT_TYPE, clientName, channel.getName());
        }
    }

    public static void serverStop(final APIChannels channel)
    {
        IDiscordianConnectConnection connection = DiscordianMC.instance.getConnection();

        if (connection.getConnectionState().equals(ConnectionState.OPEN))
        {
            final String clientName = DiscordianMC.instance.getMainConfig().clientName;
            connection.send("GenericDisconnectionEvent", DiscordianMC.CLIENT_TYPE, clientName, channel.getName());
        }
    }
}
