package co.chatchain.mc.message.handling;

import co.chatchain.mc.ChatChainMC;
import co.chatchain.mc.capabilities.GroupProvider;
import co.chatchain.mc.capabilities.IGroupSettings;
import co.chatchain.mc.configs.GroupsConfig;
import co.chatchain.mc.message.objects.GenericMessage;
import co.chatchain.mc.message.objects.GetGroupsResponseMessage;
import co.chatchain.mc.message.objects.Group;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

import java.util.UUID;

import static co.chatchain.mc.Constants.*;

public class APIMessages
{

    public static void ReceiveGenericMessage(final GenericMessage message)
    {
        if (!ChatChainMC.instance.getGroupsConfig().getGroupStorage().containsKey(message.getGroup().getGroupId()))
        {
            //final Group configGroup = message.getGroup();
            //groupsConfig.groupStorage.put(message.getGroup().getGroupId(), message.getGroup());
            //TODO: REMOVE THIS
            /*message.getGroup().setAllowedPlayers(new ArrayList<>());
            for (final EntityPlayer player : ChatChainMC.instance.getServer().getPlayerList().getPlayers())
            {
                message.getGroup().getAllowedPlayers().add(player.getUniqueID());
            }*/

            ChatChainMC.instance.getGroupsConfig().getGroupStorage().put(message.getGroup().getGroupId(), message.getGroup());
            ChatChainMC.instance.getGroupsConfig().save();
        }

        final ITextComponent messageToSend;

        if (ChatChainMC.instance.getFormattingConfig().getGenericMessageFormats().containsKey(message.getGroup().getGroupId()))
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
        }

        if (ChatChainMC.instance.getGroupsConfig().getGroupStorage().get(message.getGroup().getGroupId()).isAllowAllPlayers())
        {
            for (final EntityPlayer player : ChatChainMC.instance.getServer().getPlayerList().getPlayers())
            {
                final IGroupSettings groupSettings = player.getCapability(GroupProvider.GROUP_SETTINGS_CAP, null);

                if (groupSettings != null && !groupSettings.getMutedGroups().contains(message.getGroup().getGroupId()))
                {
                    //player.sendMessage(new TextComponentString("[" + message.getGroup().getGroupName() + "] [" + message.getSendingClient().getClientName() + "] <" + message.getUser().getName() + "> " + message.getMessage()));
                    player.sendMessage(messageToSend);
                }
            }
        }
        else
        {
            for (final UUID playerUUID : ChatChainMC.instance.getGroupsConfig().getGroupStorage().get(message.getGroup().getGroupId()).getAllowedPlayers())
            {
                final EntityPlayer player = ChatChainMC.instance.getServer().getPlayerList().getPlayerByUUID(playerUUID);

                final IGroupSettings groupSettings = player.getCapability(GroupProvider.GROUP_SETTINGS_CAP, null);

                if (groupSettings != null && !groupSettings.getMutedGroups().contains(message.getGroup().getGroupId()))
                {
                    //player.sendMessage(new TextComponentString("[" + message.getGroup().getGroupName() + "] [" + message.getSendingClient().getClientName() + "] <" + message.getUser().getName() + "> " + message.getMessage()));
                    player.sendMessage(messageToSend);
                }
            }
        }

        ChatChainMC.instance.getLogger().info("New Message in Channel: " + message.getGroup().getGroupName() + " Client: " + message.getSendingClient() + " User: " + message.getUser().getName() + " Message: " + message.getMessage());
    }

    public static void GetGroupsResponse(final GetGroupsResponseMessage message)
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

}
