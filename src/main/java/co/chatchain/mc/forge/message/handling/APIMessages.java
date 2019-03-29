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

    public static void ReceiveGenericMessage(final GenericMessage message)
    {
        if (!ChatChainMC.instance.getGroupsConfig().getGroupStorage().containsKey(message.getGroup().getGroupId()))
        {
            GroupConfig config = new GroupConfig();
            config.setGroup(message.getGroup());

            ChatChainMC.instance.getGroupsConfig().getGroupStorage().put(message.getGroup().getGroupId(), config);
            ChatChainMC.instance.getGroupsConfig().save();
        }

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

        ChatChainMC.instance.getLogger().info("New Generic Message" + messageToSend.getFormattedText());
    }

    public static void ReceiveClientEvent(final ClientEventMessage message)
    {
        for (final String groupId : ChatChainMC.instance.getGroupsConfig().getClientEventGroups())
        {
            if (ChatChainMC.instance.getGroupsConfig().getGroupStorage().containsKey(groupId))
            {
                final GroupConfig groupConfig = ChatChainMC.instance.getGroupsConfig().getGroupStorage().get(groupId);
                final ITextComponent messageToSend = ChatChainMC.instance.getFormattingConfig().getClientEventMessage(message, groupConfig.getGroup());

                if (messageToSend == null)
                {
                    return;
                }

                for (final EntityPlayer player : groupConfig.getPlayersForGroupUnignored())
                {
                    player.sendMessage(messageToSend);
                }

                ChatChainMC.instance.getLogger().info("New Client Message" + messageToSend.getFormattedText());
            }
        }
    }

    public static void ReceiveUserEvent(final UserEventMessage message)
    {
        for (final String groupId : ChatChainMC.instance.getGroupsConfig().getUserEventGroups())
        {
            if (ChatChainMC.instance.getGroupsConfig().getGroupStorage().containsKey(groupId))
            {
                final GroupConfig groupConfig = ChatChainMC.instance.getGroupsConfig().getGroupStorage().get(groupId);
                final ITextComponent messageToSend = ChatChainMC.instance.getFormattingConfig().getUserEventMessage(message, groupConfig.getGroup());

                if (messageToSend == null)
                {
                    return;
                }

                for (final EntityPlayer player : groupConfig.getPlayersForGroupUnignored())
                {
                    player.sendMessage(messageToSend);
                }

                ChatChainMC.instance.getLogger().info("New User Message" + messageToSend.getFormattedText());
            }
        }
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
