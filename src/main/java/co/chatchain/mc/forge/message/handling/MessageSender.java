package co.chatchain.mc.forge.message.handling;

import co.chatchain.commons.core.entities.Group;
import co.chatchain.commons.core.interfaces.IMessageSender;
import co.chatchain.mc.forge.ChatChainMC;
import co.chatchain.mc.forge.configs.GroupConfig;
import co.chatchain.mc.forge.util.Log;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import java.util.UUID;

public class MessageSender implements IMessageSender
{
    private static void createGroupInConfig(final Group group)
    {
        if (!ChatChainMC.INSTANCE.getGroupsConfig().getGroupStorage().containsKey(group.getId()))
        {
            GroupConfig config = new GroupConfig();
            config.setGroup(group);

            ChatChainMC.INSTANCE.getGroupsConfig().getGroupStorage().put(group.getId(), config);
            ChatChainMC.INSTANCE.getGroupsConfig().save();
        }
    }

    @Override
    public boolean sendMessage(final String message, final Group group)
    {
        createGroupInConfig(group);

        final ITextComponent messageToSend = new StringTextComponent(message);

        final GroupConfig groupConfig = ChatChainMC.INSTANCE.getGroupsConfig().getGroupStorage().get(group.getId());

        for (final ServerPlayerEntity player : groupConfig.getPlayersListening())
        {
            player.sendMessage(messageToSend, Util.DUMMY_UUID);
        }

        Log.getLogger().info("New Message: " + messageToSend.getUnformattedComponentText());
        return true;
    }

    @Override
    public boolean sendStatsMessage(String message, String responseLocation)
    {
        final ITextComponent messageToSend = new StringTextComponent(message);

        ServerPlayerEntity playerEntity = ChatChainMC.MINECRAFT_SERVER.getPlayerList().getPlayerByUUID(UUID.fromString(responseLocation));

        if (playerEntity != null)
        {
            playerEntity.sendMessage(messageToSend, Util.DUMMY_UUID);
        }

        return true;
    }
}
