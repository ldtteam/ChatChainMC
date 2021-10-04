package co.chatchain.mc.forge.message.handling;

import co.chatchain.commons.core.entities.Group;
import co.chatchain.commons.core.interfaces.IMessageSender;
import co.chatchain.mc.forge.ChatChainMC;
import co.chatchain.mc.forge.configs.GroupConfig;
import co.chatchain.mc.forge.util.Log;
import net.minecraft.Util;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;

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

        final TextComponent messageToSend = new TextComponent(message);

        final GroupConfig groupConfig = ChatChainMC.INSTANCE.getGroupsConfig().getGroupStorage().get(group.getId());

        for (final ServerPlayer player : groupConfig.getPlayersListening())
        {
            player.sendMessage(messageToSend, Util.NIL_UUID);
        }

        Log.getLogger().info("New Message: " + messageToSend.getContents());
        return true;
    }

    @Override
    public boolean sendStatsMessage(String message, String responseLocation)
    {
        final TextComponent messageToSend = new TextComponent(message);

        ServerPlayer playerEntity = ChatChainMC.MINECRAFT_SERVER.getPlayerList().getPlayer(UUID.fromString(responseLocation));

        if (playerEntity != null)
        {
            playerEntity.sendMessage(messageToSend, Util.NIL_UUID);
        }

        return true;
    }
}
