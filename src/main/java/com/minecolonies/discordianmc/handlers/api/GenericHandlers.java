package com.minecolonies.discordianmc.handlers.api;

import com.minecolonies.discordianconnect.api.connection.ConnectionState;
import com.minecolonies.discordianconnect.api.message.IDiscordianConnectMessage;
import com.minecolonies.discordianmc.APIChannels;
import com.minecolonies.discordianmc.DiscordianMC;
import com.minecolonies.discordianmc.config.ClientConfigs;
import com.minecolonies.discordianmc.util.SerializeUtils;
import com.minecolonies.discordianmc.util.TemplateMessages;
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
        if (clientName.equalsIgnoreCase(DiscordianMC.instance.getMainConfig().clientName))
        {
            return;
        }

        if (DiscordianMC.instance.getClientConfigs().clientConfigs.containsKey(clientName)
              && DiscordianMC.instance.getServer() != null
              && DiscordianMC.instance.getClientConfigs().clientTypesConfig.containsKey(clientType)
              && DiscordianMC.instance.getClientConfigs().clientTypesConfig.get(clientType))
        {
            ClientConfigs.ClientConfig clientConfig = DiscordianMC.instance.getClientConfigs().clientConfigs.get(clientName);
            if (clientConfig.display)
            {
                for (final String channel : clientConfig.channels.keySet())
                {
                    if (clientConfig.channels.get(channel).contains(channelName.toLowerCase()))
                    {
                        DiscordianMC.instance.getServer().getPlayerList().getPlayers().forEach(player -> player.sendMessage(message));
                    }
                }
            }
        }

        if (!DiscordianMC.instance.getClientConfigs().clientConfigs.containsKey(clientName))
        {
            DiscordianMC.instance.getLogger().info("Adding New Client: {}", clientName);
            ClientConfigs.ClientConfig clientConfig = new ClientConfigs.ClientConfig();
            clientConfig.channels = new HashMap<>();
            clientConfig.display = false;

            DiscordianMC.instance.getClientConfigs().clientConfigs.put(clientName, clientConfig);
        }

        if (!DiscordianMC.instance.getClientConfigs().clientTypesConfig.containsKey(clientType))
        {
            DiscordianMC.instance.getLogger().info("Adding New ClientType: {}", clientType);
            DiscordianMC.instance.getClientConfigs().clientTypesConfig.put(clientType, true);
        }
    }

    public static void genericConnectionEvent(final IDiscordianConnectMessage message)
    {
        final String clientType = message.getArguments()[0].getAsString();
        final String clientName = message.getArguments()[1].getAsString();
        final String channelName = message.getArguments()[2].getAsString();

        final ITextComponent discordMessage = new TextComponentString(TemplateMessages.genericConnection(clientType,
          clientName,
          channelName));

        sendMessage(clientType, clientName, channelName, discordMessage);
    }

    public static void genericDisconnectionEvent(final IDiscordianConnectMessage message)
    {
        final String clientType = message.getArguments()[0].getAsString();
        final String clientName = message.getArguments()[1].getAsString();
        final String channelName = message.getArguments()[2].getAsString();

        final ITextComponent discordMessage = new TextComponentString(TemplateMessages.genericDisconnection(clientType,
          clientName,
          channelName));

        sendMessage(clientType, clientName, channelName, discordMessage);
    }

    public static void genericMessageEvent(final IDiscordianConnectMessage message)
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

    public static void genericJoinEvent(final IDiscordianConnectMessage message)
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

    public static void genericLeaveEvent(final IDiscordianConnectMessage message)
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

    public static void requestJoined(final IDiscordianConnectMessage message)
    {
        final String clientType = message.getArguments()[0].getAsString();
        final String clientName = message.getArguments()[1].getAsString();
        final String requestedClient = message.getArguments()[2].getAsString();

        if (requestedClient.equalsIgnoreCase(DiscordianMC.instance.getMainConfig().clientName)
              && DiscordianMC.instance.getClientConfigs().clientConfigs.containsKey(clientName)
              && DiscordianMC.instance.getClientConfigs().clientConfigs.get(clientName).display
              && DiscordianMC.instance.getClientConfigs().clientTypesConfig.containsKey(clientType)
              && DiscordianMC.instance.getClientConfigs().clientTypesConfig.get(clientType))
        {
            final ArrayList<String> players = (ArrayList<String>) Arrays.asList(DiscordianMC.instance.getServer().getOnlinePlayerNames());
            final String serializedPlayers = SerializeUtils.serialize(players, DiscordianMC.instance.getLogger());

            if (DiscordianMC.instance.getConnection().getConnectionState().equals(ConnectionState.OPEN))
            {
                DiscordianMC.instance.getConnection().send("RespondJoined",
                  DiscordianMC.CLIENT_TYPE,
                  DiscordianMC.instance.getMainConfig().clientName,
                  APIChannels.MAIN.getName(),
                  clientName,
                  serializedPlayers);
            }
        }
    }

    public static void respondJoined(final IDiscordianConnectMessage message)
    {
        final String clientType = message.getArguments()[0].getAsString();
        final String clientName = message.getArguments()[1].getAsString();
        final String channelID = message.getArguments()[2].getAsString();
        final String destinationClient = message.getArguments()[3].getAsString();
        final String serializedList = message.getArguments()[4].getAsString();

        if (destinationClient.equalsIgnoreCase(DiscordianMC.instance.getMainConfig().clientName))
        {
            final Object object = SerializeUtils.deserialize(serializedList, DiscordianMC.instance.getLogger());

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
