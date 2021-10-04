package co.chatchain.mc.forge.cases.stats;

import co.chatchain.commons.core.entities.ClientUser;
import co.chatchain.commons.core.entities.StatsObject;
import co.chatchain.commons.core.entities.messages.stats.StatsRequestMessage;
import co.chatchain.commons.core.entities.requests.stats.StatsResponseRequest;
import co.chatchain.commons.core.interfaces.cases.stats.IReceiveStatsRequestCase;
import co.chatchain.commons.interfaces.IChatChainHubConnection;
import co.chatchain.mc.forge.ChatChainMC;
import co.chatchain.mc.forge.util.UserUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;

import javax.inject.Inject;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class ReceiveStatsRequestCase implements IReceiveStatsRequestCase
{
    private static final DecimalFormat           TIME_FORMATTER = new DecimalFormat("########0.000");
    private final        IChatChainHubConnection chatChainHubConnection;

    @Inject
    public ReceiveStatsRequestCase(final IChatChainHubConnection chatChainHubConnection)
    {
        this.chatChainHubConnection = chatChainHubConnection;
    }

    @Override
    public boolean handle(final StatsRequestMessage message)
    {
        final StatsObject statsObject = new StatsObject();

        final List<ClientUser> clientUsers = new ArrayList<>();

        if (message.getStatsSection() == null || message.getStatsSection().equals("online-users"))
        {
            ChatChainMC.MINECRAFT_SERVER.getPlayerList().getPlayers().forEach(player -> clientUsers.add(UserUtils.getClientUserFromPlayer(player)));
            statsObject.setOnlineUsers(clientUsers);
        }

        if (message.getStatsSection() == null || message.getStatsSection().equals("performance"))
        {
            long[] times = null;
            for (ServerLevel dim : ChatChainMC.MINECRAFT_SERVER.getAllLevels())
            {
                if (dim.dimension().location().equals(new ResourceLocation("minecraft:overworld")))
                    times = ChatChainMC.MINECRAFT_SERVER.getTickTime(dim.dimension());
            }

            if (times != null)
            {
                double worldTickTime = mean(times) * 1.0E-6D;
                double worldTPS = Math.min(1000.0 / worldTickTime, 20);

                statsObject.setPerformance(TIME_FORMATTER.format(worldTPS));
            }
            else
            {
                statsObject.setPerformance("null");
            }
            statsObject.setPerformanceName("TPS");
        }

        chatChainHubConnection.sendStatsResponseMessage(new StatsResponseRequest(message.getRequestId(), statsObject));

        return true;
    }

    private static long mean(long[] values)
    {
        long sum = 0L;
        for (long v : values)
            sum += v;
        return sum / values.length;
    }
}
