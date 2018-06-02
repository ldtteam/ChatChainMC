package com.minecolonies.chatchainmc.coremod.util;

import com.minecolonies.chatchainmc.coremod.ChatChainMC;

import static com.minecolonies.chatchainmc.coremod.config.TemplatesConfigReplacements.*;

/**
 * Util class for replacing the various values in TemplatesConfig values.
 */
public class TemplateMessages
{

    private TemplateMessages() {/* Private constructor to hide implicit */}

    public static String genericConnection(final String clientType, final String clientName, final String channelName)
    {
        return ChatChainMC.instance.getTemplatesConfig().genericConnection
                 .replace(CLIENT_TYPE, clientType)
                 .replace(CLIENT_NAME, clientName)
                 .replace(CHANNEL_NAME, channelName);
    }

    public static String genericDisconnection(final String clientType, final String clientName, final String channelName)
    {
        return ChatChainMC.instance.getTemplatesConfig().genericDisconnection
                 .replace(CLIENT_TYPE, clientType)
                 .replace(CLIENT_NAME, clientName)
                 .replace(CHANNEL_NAME, channelName);
    }

    public static String genericMessage(final String clientType, final String clientName, final String channelName, final String user, final String message)
    {
        return ChatChainMC.instance.getTemplatesConfig().genericMessage
                 .replace(CLIENT_TYPE, clientType)
                 .replace(CLIENT_NAME, clientName)
                 .replace(CHANNEL_NAME, channelName)
                 .replace(USER_NAME, user)
                 .replace(USER_MESSAGE, message);
    }

    public static String genericJoin(final String clientType, final String clientName, final String channelName, final String user)
    {
        return ChatChainMC.instance.getTemplatesConfig().genericJoin
                 .replace(CLIENT_TYPE, clientType)
                 .replace(CLIENT_NAME, clientName)
                 .replace(CHANNEL_NAME, channelName)
                 .replace(USER_NAME, user);
    }

    public static String genericLeave(final String clientType, final String clientName, final String channelName, final String user)
    {
        return ChatChainMC.instance.getTemplatesConfig().genericLeave
                 .replace(CLIENT_TYPE, clientType)
                 .replace(CLIENT_NAME, clientName)
                 .replace(CHANNEL_NAME, channelName)
                 .replace(USER_NAME, user);
    }
}
