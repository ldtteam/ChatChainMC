package co.chatchain.mc.forge.message.handling;

import co.chatchain.commons.objects.Group;
import co.chatchain.commons.objects.messages.*;
import co.chatchain.mc.forge.ChatChainMC;
import co.chatchain.mc.forge.configs.GroupConfig;
import co.chatchain.mc.forge.configs.GroupsConfig;
import co.chatchain.mc.forge.configs.formatting.ReplacementUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

public class APIMessages
{

    public static void createGroupInConfig(final Group group)
    {
        if (!ChatChainMC.instance.getGroupsConfig().getGroupStorage().containsKey(group.getId()))
        {
            GroupConfig config = new GroupConfig();
            config.setGroup(group);

            ChatChainMC.instance.getGroupsConfig().getGroupStorage().put(group.getId(), config);
            ChatChainMC.instance.getGroupsConfig().save();
        }
    }

    public static void ReceiveGenericMessage(final GenericMessageMessage message)
    {
        createGroupInConfig(message.getGroup());

        final ITextComponent messageToSend = new TextComponentString(ReplacementUtils.getFormat(message));


        final GroupConfig groupConfig = ChatChainMC.instance.getGroupsConfig().getGroupStorage().get(message.getGroup().getId());

        for (final EntityPlayer player : groupConfig.getPlayersListening())
        {
            player.sendMessage(messageToSend);
        }

        ChatChainMC.instance.getLogger().info("New Generic Message " + messageToSend.getFormattedText());
    }

    public static void ReceiveClientEvent(final ClientEventMessage message)
    {
        createGroupInConfig(message.getGroup());

        final ITextComponent messageToSend = new TextComponentString(ReplacementUtils.getFormat(message));


        final GroupConfig groupsConfig = ChatChainMC.instance.getGroupsConfig().getGroupStorage().get(message.getGroup().getId());

        for (final EntityPlayer player : groupsConfig.getPlayersListening())
        {
            player.sendMessage(messageToSend);
        }

        ChatChainMC.instance.getLogger().info("New Client Event Message " + messageToSend.getFormattedText());
    }

    public static void ReceiveUserEvent(final UserEventMessage message)
    {
        createGroupInConfig(message.getGroup());

        final ITextComponent messageToSend = new TextComponentString(ReplacementUtils.getFormat(message));

        final GroupConfig groupsConfig = ChatChainMC.instance.getGroupsConfig().getGroupStorage().get(message.getGroup().getId());

        for (final EntityPlayer player : groupsConfig.getPlayersListening())
        {
            player.sendMessage(messageToSend);
        }

        ChatChainMC.instance.getLogger().info("New User Event Message " + messageToSend.getFormattedText());
    }

    public static void ReceiveGroups(final GetGroupsMessage message)
    {
        final GroupsConfig groupsConfig = ChatChainMC.instance.getGroupsConfig();

        for (final Group group : message.getGroups())
        {
            if (!groupsConfig.getGroupStorage().containsKey(group.getId()))
            {
                GroupConfig config = new GroupConfig();
                config.setGroup(group);
                groupsConfig.getGroupStorage().put(group.getId(), config);
            }
        }
        groupsConfig.save();
    }

    public static void ReceiveClient(final GetClientMessage message)
    {
        ChatChainMC.instance.setClient(message.getClient());
    }

}
