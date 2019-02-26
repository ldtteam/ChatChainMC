package co.chatchain.mc.message.handling;

import co.chatchain.mc.ChatChainMC;
import co.chatchain.mc.capabilities.GroupProvider;
import co.chatchain.mc.capabilities.IGroupSettings;
import co.chatchain.mc.configs.GroupsConfig;
import co.chatchain.mc.message.objects.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

import static co.chatchain.mc.Constants.*;

public class APIMessages
{

    public static void ReceiveGenericMessage(final GenericMessage message)
    {
        if (!ChatChainMC.instance.getGroupsConfig().getGroupStorage().containsKey(message.getGroup().getGroupId()))
        {
            ChatChainMC.instance.getGroupsConfig().getGroupStorage().put(message.getGroup().getGroupId(), message.getGroup());
            ChatChainMC.instance.getGroupsConfig().save();
        }

        final ITextComponent messageToSend = ChatChainMC.instance.getFormattingConfig().getGenericMessage(message);

        if (messageToSend == null)
        {
            return;
        }

        /*if (ChatChainMC.instance.getFormattingConfig().getGenericMessageFormats().containsKey(message.getGroup().getGroupId()))
        {
            messageToSend = new TextComponentString(ChatChainMC.instance.getFormattingConfig().getGenericMessageFormats().get(message.getGroup().getGroupId())
                    .replace(GROUP_NAME, message.getGroup().getGroupName())
                    .replace(GROUP_ID, message.getGroup().getGroupId())
                    .replace(USER_NAME, message.getUser().getName())
                    .replace(SENDING_CLIENT_NAME, message.getSendingClient().getClientName())
                    .replace(SENDING_CLIENT_GUID, message.getSendingClient().getClientGuid())
                    .replace(MESSAGE, message.getMessage()));
        }
        else
        {
            messageToSend = new TextComponentString(ChatChainMC.instance.getFormattingConfig().getDefaultGenericMessageFormat()
                    .replace(GROUP_NAME, message.getGroup().getGroupName())
                    .replace(GROUP_ID, message.getGroup().getGroupId())
                    .replace(USER_NAME, message.getUser().getName())
                    .replace(SENDING_CLIENT_NAME, message.getSendingClient().getClientName())
                    .replace(SENDING_CLIENT_GUID, message.getSendingClient().getClientGuid())
                    .replace(MESSAGE, message.getMessage()));
        }*/

        final Group configGroup = ChatChainMC.instance.getGroupsConfig().getGroupStorage().get(message.getGroup().getGroupId());

        for (final EntityPlayer player : ChatChainMC.instance.getGroupsConfig().getPlayersForGroup(configGroup))
        {
            final IGroupSettings groupSettings = player.getCapability(GroupProvider.GROUP_SETTINGS_CAP, null);

            if (groupSettings != null && !groupSettings.getMutedGroups().contains(configGroup))
            {
                player.sendMessage(messageToSend);
            }
        }

        ChatChainMC.instance.getLogger().info("New Message in Channel: " + message.getGroup().getGroupName() + " Client: " + message.getSendingClient() + " User: " + message.getUser().getName() + " Message: " + message.getMessage());
    }

    public static void ReceiveClientEvent(final ClientEventMessage message)
    {
        for (final String groupId : ChatChainMC.instance.getGroupsConfig().getClientEventGroups())
        {
            if (ChatChainMC.instance.getGroupsConfig().getGroupStorage().containsKey(groupId))
            {
                final Group group = ChatChainMC.instance.getGroupsConfig().getGroupStorage().get(groupId);
                final ITextComponent messageToSend = ChatChainMC.instance.getFormattingConfig().getClientEventMessage(message, group);

                if (messageToSend == null)
                {
                    return;
                }

                /*if (message.getEvent().equals("START"))
                {
                    if (ChatChainMC.instance.getFormattingConfig().getClientStartEventFormats().containsKey(group.getGroupId()))
                    {
                        messageToSend = new TextComponentString(ChatChainMC.instance.getFormattingConfig().getClientStartEventFormats().get(group.getGroupId())
                                .replace(GROUP_NAME, group.getGroupName())
                                .replace(GROUP_ID, group.getGroupId())
                                .replace(SENDING_CLIENT_NAME, message.getSendingClient().getClientName())
                                .replace(SENDING_CLIENT_GUID, message.getSendingClient().getClientGuid()));
                    }
                    else
                    {
                        messageToSend = new TextComponentString(ChatChainMC.instance.getFormattingConfig().getDefaultClientStartEventFormats()
                                .replace(GROUP_NAME, group.getGroupName())
                                .replace(GROUP_ID, group.getGroupId())
                                .replace(SENDING_CLIENT_NAME, message.getSendingClient().getClientName())
                                .replace(SENDING_CLIENT_GUID, message.getSendingClient().getClientGuid()));
                    }
                }
                else if (message.getEvent().equals("STOP"))
                {
                    if (ChatChainMC.instance.getFormattingConfig().getClientStopEventFormats().containsKey(group.getGroupId()))
                    {
                        messageToSend = new TextComponentString(ChatChainMC.instance.getFormattingConfig().getClientStopEventFormats().get(group.getGroupId())
                                .replace(GROUP_NAME, group.getGroupName())
                                .replace(GROUP_ID, group.getGroupId())
                                .replace(SENDING_CLIENT_NAME, message.getSendingClient().getClientName())
                                .replace(SENDING_CLIENT_GUID, message.getSendingClient().getClientGuid()));
                    }
                    else
                    {
                        messageToSend = new TextComponentString(ChatChainMC.instance.getFormattingConfig().getDefaultClientStopEventFormats()
                                .replace(GROUP_NAME, group.getGroupName())
                                .replace(GROUP_ID, group.getGroupId())
                                .replace(SENDING_CLIENT_NAME, message.getSendingClient().getClientName())
                                .replace(SENDING_CLIENT_GUID, message.getSendingClient().getClientGuid()));
                    }
                }
                else
                {
                    return;
                }*/

                for (final EntityPlayer player : ChatChainMC.instance.getGroupsConfig().getPlayersForGroup(group))
                {
                    final IGroupSettings groupSettings = player.getCapability(GroupProvider.GROUP_SETTINGS_CAP, null);

                    if (groupSettings != null && !groupSettings.getMutedGroups().contains(group))
                    {
                        player.sendMessage(messageToSend);
                    }
                }

            }
        }
    }

    public static void ReceiveGroups(final ReceiveGroupsMessage message)
    {
        final GroupsConfig groupsConfig = ChatChainMC.instance.getGroupsConfig();

        for (final Group group : message.getGroups())
        {
            if (!groupsConfig.getGroupStorage().containsKey(group.getGroupId()))
            {
                groupsConfig.getGroupStorage().put(group.getGroupId(), group);
            }
        }

        groupsConfig.save();
    }

    public static void ReceiveClient(final ReceiveClientMessage message)
    {
        ChatChainMC.instance.setClient(message.getClient());
    }

}
