package co.chatchain.mc.forge.message.handling;

import co.chatchain.commons.core.entities.Group;
import co.chatchain.commons.core.interfaces.IMessageSender;
import co.chatchain.mc.forge.ChatChainMC;
import co.chatchain.mc.forge.configs.GroupConfig;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

public class MessageSender implements IMessageSender
{
    private static void createGroupInConfig(final Group group)
    {
        if (!ChatChainMC.instance.getGroupsConfig().getGroupStorage().containsKey(group.getId()))
        {
            GroupConfig config = new GroupConfig();
            config.setGroup(group);

            ChatChainMC.instance.getGroupsConfig().getGroupStorage().put(group.getId(), config);
            ChatChainMC.instance.getGroupsConfig().save();
        }
    }

    @Override
    public boolean sendMessage(final String message, final Group group)
    {
        createGroupInConfig(group);

        final ITextComponent messageToSend = new TextComponentString(message);

        final GroupConfig groupConfig = ChatChainMC.instance.getGroupsConfig().getGroupStorage().get(group.getId());

        for (final EntityPlayer player : groupConfig.getPlayersListening())
        {
            player.sendMessage(messageToSend);
        }

        ChatChainMC.instance.getLogger().info("New Message: " + messageToSend.getFormattedText());
        return true;
    }
}
