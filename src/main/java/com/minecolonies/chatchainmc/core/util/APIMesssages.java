package com.minecolonies.chatchainmc.core.util;

import com.minecolonies.chatchainmc.core.ChatChainMC;
import com.minecolonies.chatchainconnect.api.connection.ConnectionState;
import com.minecolonies.chatchainconnect.api.connection.IChatChainConnectConnection;
import com.minecolonies.chatchainmc.core.APIChannels;

public class APIMesssages
{

    private APIMesssages() {/* Private constructor to hide implicit */}

    public static void chatMessage(final APIChannels channel, final String author, final String message)
    {
        IChatChainConnectConnection connection = ChatChainMC.instance.getConnection();

        if (connection.getConnectionState().equals(ConnectionState.OPEN))
        {
            final String clientName = ChatChainMC.instance.getMainConfig().clientName;
            connection.send("GenericMessageEvent", ChatChainMC.CLIENT_TYPE, clientName, channel.getName(), author, message);
        }
    }

    public static void playerJoin(final APIChannels channel, final String username)
    {
        IChatChainConnectConnection connection = ChatChainMC.instance.getConnection();

        if (connection.getConnectionState().equals(ConnectionState.OPEN))
        {
            final String clientName = ChatChainMC.instance.getMainConfig().clientName;
            connection.send("GenericJoinEvent", ChatChainMC.CLIENT_TYPE, clientName, channel.getName(), username);
        }
    }

    public static void playerLeave(final APIChannels channel, final String username)
    {
        IChatChainConnectConnection connection = ChatChainMC.instance.getConnection();

        if (connection.getConnectionState().equals(ConnectionState.OPEN))
        {
            final String clientName = ChatChainMC.instance.getMainConfig().clientName;
            connection.send("GenericLeaveEvent", ChatChainMC.CLIENT_TYPE, clientName, channel.getName(), username);
        }
    }

    public static void serverStart(final APIChannels channel)
    {
        IChatChainConnectConnection connection = ChatChainMC.instance.getConnection();

        if (connection.getConnectionState().equals(ConnectionState.OPEN))
        {
            final String clientName = ChatChainMC.instance.getMainConfig().clientName;
            connection.send("GenericConnectionEvent", ChatChainMC.CLIENT_TYPE, clientName, channel.getName());
        }
    }

    public static void serverStop(final APIChannels channel)
    {
        IChatChainConnectConnection connection = ChatChainMC.instance.getConnection();

        if (connection.getConnectionState().equals(ConnectionState.OPEN))
        {
            final String clientName = ChatChainMC.instance.getMainConfig().clientName;
            connection.send("GenericDisconnectionEvent", ChatChainMC.CLIENT_TYPE, clientName, channel.getName());
        }
    }
}
