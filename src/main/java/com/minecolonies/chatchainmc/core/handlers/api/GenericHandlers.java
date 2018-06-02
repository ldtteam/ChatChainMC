package com.minecolonies.chatchainmc.core.handlers.api;

import com.minecolonies.chatchainmc.core.ChatChainMC;
import com.minecolonies.chatchainconnect.api.connection.ConnectionState;
import com.minecolonies.chatchainconnect.api.message.IChatChainConnectMessage;
import com.minecolonies.chatchainmc.core.APIChannels;
import com.minecolonies.chatchainmc.core.config.ClientConfigs;
import com.minecolonies.chatchainmc.core.util.SerializeUtils;
import com.minecolonies.chatchainmc.core.util.TemplateMessages;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class GenericHandlers
{

    private GenericHandlers() {/* Private constructor to hide the implicit */}

    public static void sendMessage(final String clientType, final String clientName, final String channelName, final ITextComponent message)
    {
        if (clientName.equalsIgnoreCase(ChatChainMC.instance.getMainConfig().clientName))
        {
            return;
        }

        ChatChainMC.instance.getClientConfigs().load();
        if (ChatChainMC.instance.getClientConfigs().clientConfigs.containsKey(clientName)
              && ChatChainMC.instance.getServer() != null
              && ChatChainMC.instance.getClientConfigs().clientTypesConfig.containsKey(clientType)
              && ChatChainMC.instance.getClientConfigs().clientTypesConfig.get(clientType))
        {
            ClientConfigs.ClientConfig clientConfig = ChatChainMC.instance.getClientConfigs().clientConfigs.get(clientName);
            if (clientConfig.display)
            {
                for (final String channel : clientConfig.channels.keySet())
                {
                    if (clientConfig.channels.get(channel).contains(channelName.toLowerCase()))
                    {
                        ChatChainMC.instance.getServer().sendMessage(message);
                        ChatChainMC.instance.getServer().getPlayerList().getPlayers().forEach(player -> player.sendMessage(message));
                    }
                }
            }
        }

        if (!ChatChainMC.instance.getClientConfigs().clientConfigs.containsKey(clientName))
        {
            ChatChainMC.instance.getLogger().info("Adding New Client: {}", clientName);
            ClientConfigs.ClientConfig clientConfig = new ClientConfigs.ClientConfig();
            clientConfig.channels = new HashMap<>();
            clientConfig.display = false;

            ChatChainMC.instance.getClientConfigs().clientConfigs.put(clientName, clientConfig);
            ChatChainMC.instance.getClientConfigs().save();
        }

        if (!ChatChainMC.instance.getClientConfigs().clientTypesConfig.containsKey(clientType))
        {
            ChatChainMC.instance.getLogger().info("Adding New ClientType: {}", clientType);
            ChatChainMC.instance.getClientConfigs().clientTypesConfig.put(clientType, true);
            ChatChainMC.instance.getClientConfigs().save();
        }
    }

    public static void genericConnectionEvent(final IChatChainConnectMessage message)
    {
        final String clientType = message.getArguments()[0].getAsString();
        final String clientName = message.getArguments()[1].getAsString();
        final String channelName = message.getArguments()[2].getAsString();

        final ITextComponent discordMessage = new TextComponentString(TemplateMessages.genericConnection(clientType,
          clientName,
          channelName));

        sendMessage(clientType, clientName, channelName, discordMessage);
    }

    public static void genericDisconnectionEvent(final IChatChainConnectMessage message)
    {
        final String clientType = message.getArguments()[0].getAsString();
        final String clientName = message.getArguments()[1].getAsString();
        final String channelName = message.getArguments()[2].getAsString();

        final ITextComponent discordMessage = new TextComponentString(TemplateMessages.genericDisconnection(clientType,
          clientName,
          channelName));

        sendMessage(clientType, clientName, channelName, discordMessage);
    }

    public static void genericMessageEvent(final IChatChainConnectMessage message)
    {
        final String clientType = message.getArguments()[0].getAsString();
        final String clientName = message.getArguments()[1].getAsString();
        final String channelName = message.getArguments()[2].getAsString();
        final String user = message.getArguments()[3].getAsString();
        final String sentMessage = message.getArguments()[4].getAsString();

        final ITextComponent discordMessage = new TextComponentString((TemplateMessages.genericMessage(clientType,
          clientName,
          channelName,
          user,
          sentMessage)));

        sendMessage(clientType, clientName, channelName, discordMessage);
    }

    public static void genericJoinEvent(final IChatChainConnectMessage message)
    {
        final String clientType = message.getArguments()[0].getAsString();
        final String clientName = message.getArguments()[1].getAsString();
        final String channelName = message.getArguments()[2].getAsString();
        final String user = message.getArguments()[3].getAsString();

        final ITextComponent discordMessage = new TextComponentString(TemplateMessages.genericJoin(clientType,
          clientName,
          channelName,
          user));

        sendMessage(clientType, clientName, channelName, discordMessage);
    }

    public static void genericLeaveEvent(final IChatChainConnectMessage message)
    {
        final String clientType = message.getArguments()[0].getAsString();
        final String clientName = message.getArguments()[1].getAsString();
        final String channelName = message.getArguments()[2].getAsString();
        final String user = message.getArguments()[3].getAsString();

        final ITextComponent discordMessage = new TextComponentString(TemplateMessages.genericLeave(clientType,
          clientName,
          channelName,
          user));

        sendMessage(clientType, clientName, channelName, discordMessage);
    }

    public static void requestJoined(final IChatChainConnectMessage message)
    {
        final String clientType = message.getArguments()[0].getAsString();
        final String clientName = message.getArguments()[1].getAsString();
        final String requestedClient = message.getArguments()[2].getAsString();

        if (requestedClient.equalsIgnoreCase(ChatChainMC.instance.getMainConfig().clientName)
              && ChatChainMC.instance.getClientConfigs().clientConfigs.containsKey(clientName)
              && ChatChainMC.instance.getClientConfigs().clientConfigs.get(clientName).display
              && ChatChainMC.instance.getClientConfigs().clientTypesConfig.containsKey(clientType)
              && ChatChainMC.instance.getClientConfigs().clientTypesConfig.get(clientType))
        {
            final ArrayList<String> players = (ArrayList<String>) Arrays.asList(ChatChainMC.instance.getServer().getOnlinePlayerNames());
            final String serializedPlayers = SerializeUtils.serialize(players, ChatChainMC.instance.getLogger());

            if (ChatChainMC.instance.getConnection().getConnectionState().equals(ConnectionState.OPEN))
            {
                ChatChainMC.instance.getConnection().send("RespondJoined",
                  ChatChainMC.CLIENT_TYPE,
                  ChatChainMC.instance.getMainConfig().clientName,
                  APIChannels.MAIN.getName(),
                  clientName,
                  serializedPlayers);
            }
        }
    }

    public static void respondJoined(final IChatChainConnectMessage message)
    {
        final String clientType = message.getArguments()[0].getAsString();
        final String clientName = message.getArguments()[1].getAsString();
        final String channelID = message.getArguments()[2].getAsString();
        final String destinationClient = message.getArguments()[3].getAsString();
        final String serializedList = message.getArguments()[4].getAsString();

        if (destinationClient.equalsIgnoreCase(ChatChainMC.instance.getMainConfig().clientName))
        {
            final Object object = SerializeUtils.deserialize(serializedList, ChatChainMC.instance.getLogger());

            if (object instanceof ArrayList)
            {
                final List<String> players = (ArrayList<String>) object;

                for (final String name : new ArrayList<>(players))
                {
                    if (name == null || name.equals("null"))
                    {
                        players.remove(name);
                    }
                }

                final ITextComponent text = new TextComponentString("Online: " + players.toString());

                sendMessage(clientType, clientName, channelID, text);
            }
        }
    }
}
