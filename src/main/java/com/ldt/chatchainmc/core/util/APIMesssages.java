package com.ldt.chatchainmc.core.util;

import com.google.gson.Gson;
import com.ldt.chatchainmc.core.ChatChainMC;
import com.minecolonies.chatchainconnect.api.connection.ConnectionState;
import com.minecolonies.chatchainconnect.api.connection.IChatChainConnectConnection;
import com.minecolonies.chatchainconnect.api.objects.User;

public class APIMesssages
{

    private APIMesssages() {/* Private constructor to hide implicit */}

    public static void chatMessage(final String channel, final User user, final String message)
    {
        IChatChainConnectConnection connection = ChatChainMC.instance.getConnection();

        if (connection != null
              && connection.getConnectionState().equals(ConnectionState.OPEN))
        {
            final String clientName = ChatChainMC.instance.getMainConfig().clientName;
            connection.send("GenericMessageEvent", ChatChainMC.CLIENT_TYPE, clientName, channel, new Gson().toJson(user), message);
        }
    }

    public static void playerJoin(final String channel, final User user)
    {
        IChatChainConnectConnection connection = ChatChainMC.instance.getConnection();

        if (connection != null
              && connection.getConnectionState().equals(ConnectionState.OPEN))
        {
            final String clientName = ChatChainMC.instance.getMainConfig().clientName;
            connection.send("GenericJoinEvent", ChatChainMC.CLIENT_TYPE, clientName, channel, new Gson().toJson(user));
        }
    }

    public static void playerLeave(final String channel, final User user)
    {
        IChatChainConnectConnection connection = ChatChainMC.instance.getConnection();

        if (connection != null
              && connection.getConnectionState().equals(ConnectionState.OPEN))
        {
            final String clientName = ChatChainMC.instance.getMainConfig().clientName;
            connection.send("GenericLeaveEvent", ChatChainMC.CLIENT_TYPE, clientName, channel, new Gson().toJson(user));
        }
    }

    public static void connect(final String channel)
    {
        IChatChainConnectConnection connection = ChatChainMC.instance.getConnection();

        if (connection != null
              && connection.getConnectionState().equals(ConnectionState.OPEN))
        {
            final String clientName = ChatChainMC.instance.getMainConfig().clientName;
            connection.send("GenericConnectionEvent", ChatChainMC.CLIENT_TYPE, clientName, channel);
        }
    }

    public static void disconnect(final String channel)
    {
        IChatChainConnectConnection connection = ChatChainMC.instance.getConnection();

        if (connection != null
              && connection.getConnectionState().equals(ConnectionState.OPEN))
        {
            final String clientName = ChatChainMC.instance.getMainConfig().clientName;
            connection.send("GenericDisconnectionEvent", ChatChainMC.CLIENT_TYPE, clientName, channel);
        }
    }
}
