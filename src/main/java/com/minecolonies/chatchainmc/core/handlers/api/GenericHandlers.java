package com.minecolonies.chatchainmc.core.handlers.api;

import com.minecolonies.chatchainconnect.api.message.IChatChainConnectMessage;
import com.minecolonies.chatchainconnect.api.objects.User;
import com.minecolonies.chatchainmc.core.ChatChainMC;
import com.minecolonies.chatchainmc.core.config.ClientConfigs;
import com.minecolonies.chatchainmc.core.util.TemplateMessages;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

import java.util.HashMap;

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
            if (clientConfig.display
                  && clientConfig.channels.containsKey(channelName))
            {
                for (final String channel : clientConfig.channels.get(channelName))
                {
                    //We'll send it in the "Channel" later..
                    Minecraft.getMinecraft().addScheduledTask(() -> ChatChainMC.instance.getServer().sendMessage(message));
                    Minecraft.getMinecraft().addScheduledTask(() -> ChatChainMC.instance.getServer().getPlayerList().getPlayers().forEach(player -> player.sendMessage(message)));
                    //ChatChainMC.instance.getServer().sendMessage(message);
                    //ChatChainMC.instance.getServer().getPlayerList().getPlayers().forEach(player -> player.sendMessage(message));
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
        new Thread(() -> {
            final String clientType = message.getArguments()[0].getAsString();
            final String clientName = message.getArguments()[1].getAsString();
            final String channelName = message.getArguments()[2].getAsString();

            final ITextComponent apiMessage = new TextComponentString(TemplateMessages.genericConnection(clientType,
              clientName,
              channelName));

            sendMessage(clientType, clientName, channelName, apiMessage);
        }).start();
    }

    public static void genericDisconnectionEvent(final IChatChainConnectMessage message)
    {
        new Thread(() -> {
            final String clientType = message.getArguments()[0].getAsString();
            final String clientName = message.getArguments()[1].getAsString();
            final String channelName = message.getArguments()[2].getAsString();

            final ITextComponent apiMessage = new TextComponentString(TemplateMessages.genericDisconnection(clientType,
              clientName,
              channelName));

            sendMessage(clientType, clientName, channelName, apiMessage);
        }).start();
    }

    public static void genericMessageEvent(final IChatChainConnectMessage message)
    {
        new Thread(() -> {
            final String clientType = message.getArguments()[0].getAsString();
            final String clientName = message.getArguments()[1].getAsString();
            final String channelName = message.getArguments()[2].getAsString();
            final User user = User.fromJson(message.getArguments()[3]);
            final String sentMessage = message.getArguments()[4].getAsString();

            final ITextComponent apiMessage = new TextComponentString((TemplateMessages.genericMessage(clientType,
              clientName,
              channelName,
              user.getName(),
              sentMessage)));

            sendMessage(clientType, clientName, channelName, apiMessage);
        }).start();
    }

    public static void genericJoinEvent(final IChatChainConnectMessage message)
    {
        new Thread(() -> {
            final String clientType = message.getArguments()[0].getAsString();
            final String clientName = message.getArguments()[1].getAsString();
            final String channelName = message.getArguments()[2].getAsString();
            final User user = User.fromJson(message.getArguments()[3]);

            final ITextComponent apiMessage = new TextComponentString(TemplateMessages.genericJoin(clientType,
              clientName,
              channelName,
              user.getName()));

            sendMessage(clientType, clientName, channelName, apiMessage);
        }).start();
    }

    public static void genericLeaveEvent(final IChatChainConnectMessage message)
    {
        new Thread(() -> {
            final String clientType = message.getArguments()[0].getAsString();
            final String clientName = message.getArguments()[1].getAsString();
            final String channelName = message.getArguments()[2].getAsString();
            final User user = User.fromJson(message.getArguments()[3]);

            final ITextComponent apiMessage = new TextComponentString(TemplateMessages.genericLeave(clientType,
              clientName,
              channelName,
              user.getName()));

            sendMessage(clientType, clientName, channelName, apiMessage);
        }).start();
    }
}
