package co.chatchain.mc.forge.cases.stats;

import co.chatchain.commons.core.entities.ClientUser;
import co.chatchain.commons.core.entities.messages.stats.StatsResponseMessage;
import co.chatchain.commons.core.interfaces.cases.stats.IReceiveStatsResponseCase;
import co.chatchain.mc.forge.ChatChainMC;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Util;
import net.minecraft.util.text.StringTextComponent;

import java.util.Arrays;
import java.util.UUID;

public class ReceiveStatsResponseCase implements IReceiveStatsResponseCase
{

    @Override
    public boolean handle(final StatsResponseMessage message)
    {
        final UUID playerUUID = ChatChainMC.INSTANCE.getStatsRequestsCache().getIfPresent(message.getRequestId());

        if (playerUUID != null)
        {
            StringTextComponent textComponent = new StringTextComponent("§f[§cStats§f]§r\n§f[§cStats§f]§r §f[§6Client Name§f]:§r " + message.getSendingClient().getName());

            boolean allNull = true;

            if (message.getStatsObject().getOnlineUsers() != null)
            {
                textComponent.appendString("\n§f[§cStats§f]§r §f[§eOnline Users§f]:§r " + Arrays.toString(message.getStatsObject().getOnlineUsers().stream().map(ClientUser::getName).toArray()));
                allNull = false;
            }

            if (message.getStatsObject().getPerformance() != null)
            {
                textComponent.appendString("\n§f[§cStats§f]§r §f[§ePerformance§f]:§r " + message.getStatsObject().getPerformance() + " " + message.getStatsObject().getPerformanceName());
                allNull = false;
            }

            if (allNull)
                return true;

            ServerPlayerEntity playerEntity = ChatChainMC.MINECRAFT_SERVER.getPlayerList().getPlayerByUUID(playerUUID);

            if (playerEntity != null)
            {
                playerEntity.sendMessage(textComponent, Util.DUMMY_UUID);
            }
            return false;
        }

        return false;
    }
}
