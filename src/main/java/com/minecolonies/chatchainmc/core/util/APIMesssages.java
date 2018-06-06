package com.minecolonies.chatchainmc.core.util;

import com.google.gson.Gson;
import com.minecolonies.chatchainconnect.ChatChainConnectAPI;
import com.minecolonies.chatchainconnect.api.objects.User;
import com.minecolonies.chatchainmc.core.ChatChainMC;
import com.minecolonies.chatchainconnect.api.connection.ConnectionState;
import com.minecolonies.chatchainconnect.api.connection.IChatChainConnectConnection;
import com.minecolonies.chatchainmc.core.APIChannels;

public class APIMesssages
{

    private APIMesssages() {/* Private constructor to hide implicit */}

    public static void chatMessage(final APIChannels channel, final User user, final String message)
    {
        IChatChainConnectConnection connection = ChatChainMC.instance.getConnection();

        if (connection.getConnectionState().equals(ConnectionState.OPEN))
        {
            final String clientName = ChatChainMC.instance.getMainConfig().clientName;
            connection.send("GenericMessageEvent", ChatChainMC.CLIENT_TYPE, clientName, channel.getName(), new Gson().toJson(user), message);
        }
    }

    public static void playerJoin(final APIChannels channel, final User user)
    {
        IChatChainConnectConnection connection = ChatChainMC.instance.getConnection();

        if (connection.getConnectionState().equals(ConnectionState.OPEN))
        {
            final String clientName = ChatChainMC.instance.getMainConfig().clientName;
            connection.send("GenericJoinEvent", ChatChainMC.CLIENT_TYPE, clientName, channel.getName(), new Gson().toJson(user));
        }
    }

    public static void playerLeave(final APIChannels channel, final User user)
    {
        IChatChainConnectConnection connection = ChatChainMC.instance.getConnection();

        if (connection.getConnectionState().equals(ConnectionState.OPEN))
        {
            final String clientName = ChatChainMC.instance.getMainConfig().clientName;
            connection.send("GenericLeaveEvent", ChatChainMC.CLIENT_TYPE, clientName, channel.getName(), new Gson().toJson(user));
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
