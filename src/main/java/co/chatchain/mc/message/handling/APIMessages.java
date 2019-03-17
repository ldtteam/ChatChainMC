package co.chatchain.mc.message.handling;

import co.chatchain.commons.messages.objects.Group;
import co.chatchain.commons.messages.objects.message.ClientEventMessage;
import co.chatchain.commons.messages.objects.message.GenericMessage;
import co.chatchain.commons.messages.objects.message.GetClientResponse;
import co.chatchain.commons.messages.objects.message.GetGroupsResponse;
import co.chatchain.mc.ChatChainMC;
import co.chatchain.mc.capabilities.GroupProvider;
import co.chatchain.mc.capabilities.IGroupSettings;
import co.chatchain.mc.configs.GroupConfig;
import co.chatchain.mc.configs.GroupsConfig;
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

        for (final EntityPlayer player : groupConfig.getPlayersForGroup())
        {
            final IGroupSettings groupSettings = player.getCapability(GroupProvider.GROUP_SETTINGS_CAP, null);

            if (groupSettings != null && !groupSettings.getMutedGroups().contains(groupConfig.getGroup()))
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
                final GroupConfig groupConfig = ChatChainMC.instance.getGroupsConfig().getGroupStorage().get(groupId);
                final ITextComponent messageToSend = ChatChainMC.instance.getFormattingConfig().getClientEventMessage(message, groupConfig.getGroup());

                if (messageToSend == null)
                {
                    return;
                }

                for (final EntityPlayer player : groupConfig.getPlayersForGroup())
                {
                    final IGroupSettings groupSettings = player.getCapability(GroupProvider.GROUP_SETTINGS_CAP, null);

                    if (groupSettings != null && !groupSettings.getMutedGroups().contains(groupConfig.getGroup()))
                    {
                        player.sendMessage(messageToSend);
                    }
                }

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
