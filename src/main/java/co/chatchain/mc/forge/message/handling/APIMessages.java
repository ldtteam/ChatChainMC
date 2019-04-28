package co.chatchain.mc.forge.message.handling;

import co.chatchain.commons.messages.objects.Group;
import co.chatchain.commons.messages.objects.messages.*;
import co.chatchain.mc.forge.ChatChainMC;
import co.chatchain.mc.forge.configs.GroupConfig;
import co.chatchain.mc.forge.configs.GroupsConfig;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.ITextComponent;

public class APIMessages
{

    public static void createGroupInConfig(final Group group)
    {
        if (!ChatChainMC.instance.getGroupsConfig().getGroupStorage().containsKey(group.getGroupId()))
        {
            GroupConfig config = new GroupConfig();
            config.setGroup(group);

            ChatChainMC.instance.getGroupsConfig().getGroupStorage().put(group.getGroupId(), config);
            ChatChainMC.instance.getGroupsConfig().save();
        }
    }

    public static void ReceiveGenericMessage(final GenericMessage message)
    {
        createGroupInConfig(message.getGroup());

        final ITextComponent messageToSend = ChatChainMC.instance.getFormattingConfig().getGenericMessage(message);

        if (messageToSend == null)
        {
            return;
        }

        final GroupConfig groupConfig = ChatChainMC.instance.getGroupsConfig().getGroupStorage().get(message.getGroup().getGroupId());

        for (final EntityPlayer player : groupConfig.getPlayersForGroupUnignored())
        {
            player.sendMessage(messageToSend);
        }

        ChatChainMC.instance.getLogger().info("New Generic Message " + messageToSend.getFormattedText());
    }

    public static void ReceiveClientEvent(final ClientEventMessage message)
    {
        createGroupInConfig(message.getGroup());

        final ITextComponent messageToSend = ChatChainMC.instance.getFormattingConfig().getClientEventMessage(message);

        if (messageToSend == null)
        {
            return;
        }

        final GroupConfig groupsConfig = ChatChainMC.instance.getGroupsConfig().getGroupStorage().get(message.getGroup().getGroupId());

        for (final EntityPlayer player : groupsConfig.getPlayersForGroupUnignored())
        {
            player.sendMessage(messageToSend);
        }

        ChatChainMC.instance.getLogger().info("New Client Event Message " + messageToSend.getFormattedText());
    }

    public static void ReceiveUserEvent(final UserEventMessage message)
    {
        createGroupInConfig(message.getGroup());

        final ITextComponent messageToSend = ChatChainMC.instance.getFormattingConfig().getUserEventMessage(message);

        if (messageToSend == null)
        {
            return;
        }

        final GroupConfig groupsConfig = ChatChainMC.instance.getGroupsConfig().getGroupStorage().get(message.getGroup().getGroupId());

        for (final EntityPlayer player : groupsConfig.getPlayersForGroupUnignored())
        {
            player.sendMessage(messageToSend);
        }

        ChatChainMC.instance.getLogger().info("New Client Event Message " + messageToSend.getFormattedText());
    }

    public static void ReceiveGroups(final GetGroupsResponse message)
    {
        final GroupsConfig groupsConfig = ChatChainMC.instance.getGroupsConfig();

        for (final Group group : message.getGroups())
        {
            if (!groupsConfig.getGroupStorage().containsKey(group.getGroupId()))
            {
                GroupConfig config = new GroupConfig();
                config.setGroup(group);
                groupsConfig.getGroupStorage().put(group.getGroupId(), config);
            }
        }
        groupsConfig.save();
    }

    public static void ReceiveClient(final GetClientResponse message)
    {
        ChatChainMC.instance.setClient(message.getClient());
    }

}
