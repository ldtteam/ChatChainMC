package co.chatchain.mc.forge.message.handling;

import co.chatchain.commons.messages.objects.Group;
import co.chatchain.commons.messages.objects.messages.*;
import co.chatchain.mc.forge.ChatChainMC;
import co.chatchain.mc.forge.configs.GroupConfig;
import co.chatchain.mc.forge.configs.GroupsConfig;
import co.chatchain.mc.forge.configs.formatting.ReplacementUtils;
import co.chatchain.mc.forge.util.Log;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class APIMessages
{

    private static void createGroupInConfig(final Group group)
    {
        if (!ChatChainMC.INSTANCE.getGroupsConfig().getGroupStorage().containsKey(group.getGroupId()))
        {
            GroupConfig config = new GroupConfig();
            config.setGroup(group);

            ChatChainMC.INSTANCE.getGroupsConfig().getGroupStorage().put(group.getGroupId(), config);
            ChatChainMC.INSTANCE.getGroupsConfig().save();
        }
    }

    public static void ReceiveGenericMessage(final GenericMessage message)
    {
        createGroupInConfig(message.getGroup());

        final ITextComponent messageToSend = new StringTextComponent(ReplacementUtils.getFormat(message));

        final GroupConfig groupConfig = ChatChainMC.INSTANCE.getGroupsConfig().getGroupStorage().get(message.getGroup().getGroupId());

        for (final ServerPlayerEntity player : groupConfig.getPlayersListening())
        {
            player.sendMessage(messageToSend);
        }

        Log.getLogger().info("New Generic Message " + messageToSend.getFormattedText());
    }

    public static void ReceiveClientEvent(final ClientEventMessage message)
    {
        createGroupInConfig(message.getGroup());

        final ITextComponent messageToSend = new StringTextComponent(ReplacementUtils.getFormat(message));

        final GroupConfig groupsConfig = ChatChainMC.INSTANCE.getGroupsConfig().getGroupStorage().get(message.getGroup().getGroupId());

        for (final ServerPlayerEntity player : groupsConfig.getPlayersListening())
        {
            player.sendMessage(messageToSend);
        }

        Log.getLogger().info("New Client Event Message " + messageToSend.getFormattedText());
    }

    public static void ReceiveUserEvent(final UserEventMessage message)
    {
        createGroupInConfig(message.getGroup());

        final ITextComponent messageToSend = new StringTextComponent(ReplacementUtils.getFormat(message));

        final GroupConfig groupsConfig = ChatChainMC.INSTANCE.getGroupsConfig().getGroupStorage().get(message.getGroup().getGroupId());

        for (final ServerPlayerEntity player : groupsConfig.getPlayersListening())
        {
            player.sendMessage(messageToSend);
        }

        Log.getLogger().info("New User Event Message " + messageToSend.getFormattedText());
    }

    public static void ReceiveGroups(final GetGroupsResponse message)
    {
        final GroupsConfig groupsConfig = ChatChainMC.INSTANCE.getGroupsConfig();

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
        ChatChainMC.INSTANCE.setClient(message.getClient());
    }

}
