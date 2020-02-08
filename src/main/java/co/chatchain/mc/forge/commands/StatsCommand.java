package co.chatchain.mc.forge.commands;

import co.chatchain.commons.core.entities.messages.stats.StatsRequestMessage;
import co.chatchain.commons.core.entities.requests.stats.StatsRequestRequest;
import co.chatchain.mc.forge.ChatChainMC;
import co.chatchain.mc.forge.configs.GroupConfig;
import co.chatchain.mc.forge.util.CommandUtils;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.reactivex.Single;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;

import java.util.Arrays;

public class StatsCommand extends AbstractCommand
{

    private final static String NAME = "stats";

    private static final String GROUP_NAME = "groupName";

    private static final String STATS_SECTION = "statsSection";

    private static int onExecuteWithStatsSection(final CommandContext<CommandSource> context) throws CommandSyntaxException
    {
        ServerPlayerEntity player = context.getSource().asPlayer();
        final String groupName = context.getArgument(GROUP_NAME, String.class);
        final String statsSection = context.getArgument(STATS_SECTION, String.class);

        return onExecute(context, player, groupName, statsSection);
    }

    private static int onExecute(final CommandContext<CommandSource> context) throws CommandSyntaxException
    {
        ServerPlayerEntity player = context.getSource().asPlayer();
        final String groupName = context.getArgument(GROUP_NAME, String.class);

        return onExecute(context, player, groupName, null);
    }

    private static int onExecute(final CommandContext<CommandSource> context, final ServerPlayerEntity player, final String groupName, final String statsSection)
    {
        GroupConfig groupConfig = null;
        String groupId = null;

        for (final String id : ChatChainMC.INSTANCE.getGroupsConfig().getGroupStorage().keySet())
        {
            final GroupConfig fGroupConfig = ChatChainMC.INSTANCE.getGroupsConfig().getGroupStorage().get(id);

            if (fGroupConfig.getCommandName().equalsIgnoreCase(groupName))
            {
                groupConfig = fGroupConfig;
                groupId = id;
                break;
            }
        }

        if (groupConfig == null || groupId == null || !groupConfig.getPlayersForGroup().contains(player))
        {
            context.getSource().sendErrorMessage(new StringTextComponent("This group is invalid!"));
            return 0;
        }

        Single<StatsRequestMessage> response = ChatChainMC.INSTANCE.getConnection().sendStatsRequestMessage(new StatsRequestRequest(null, groupId, statsSection));

        response.doOnError(throwable ->
        {
            context.getSource().sendErrorMessage(new StringTextComponent("Failed to get Stats Request Response, check logs for stacktrace"));
            throwable.printStackTrace();
        }).doOnSuccess(message ->
        {
            for (String requestId : message.getRequestIds())
            {
                ChatChainMC.INSTANCE.getConnection().addStatsRequest(requestId, player.getUniqueID().toString());
            }
        }).subscribe();

        return 1;
    }

    protected static LiteralArgumentBuilder<CommandSource> build()
    {
        return newLiteral(NAME)
                .then(newArgument(GROUP_NAME, StringArgumentType.word()).suggests(CommandUtils::getIgnorableGroupSuggestions)
                        .executes(StatsCommand::onExecute)
                        .then(newArgument(STATS_SECTION, StringArgumentType.word()).suggests(CommandUtils::getStatsSections)
                                .executes(StatsCommand::onExecuteWithStatsSection)));
    }
}
