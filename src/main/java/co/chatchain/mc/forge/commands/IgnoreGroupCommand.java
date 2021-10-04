package co.chatchain.mc.forge.commands;

import co.chatchain.mc.forge.ChatChainMC;
import co.chatchain.mc.forge.capabilities.GroupProvider;
import co.chatchain.mc.forge.configs.GroupConfig;
import co.chatchain.mc.forge.util.CommandUtils;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;

public class IgnoreGroupCommand extends AbstractCommand
{

    private final static String NAME = "ignore";

    private static final String GROUP_NAME = "groupName";

    private static int onExecute(final CommandContext<CommandSourceStack> context) throws CommandSyntaxException
    {
        ServerPlayer player = context.getSource().getPlayerOrException();
        final String groupName = context.getArgument(GROUP_NAME, String.class);

        GroupConfig groupConfig = null;

        for (final String id : ChatChainMC.INSTANCE.getGroupsConfig().getGroupStorage().keySet())
        {
            final GroupConfig fGroupConfig = ChatChainMC.INSTANCE.getGroupsConfig().getGroupStorage().get(id);

            if (fGroupConfig.getCommandName().equalsIgnoreCase(groupName))
            {
                groupConfig = fGroupConfig;
                break;
            }
        }

        if (groupConfig == null || !groupConfig.getPlayersForGroup().contains(player))
        {
            context.getSource().sendFailure(new TextComponent("This group is invalid!"));
            return 0;
        }

        if (!groupConfig.isGroupIgnorable())
        {
            context.getSource().sendFailure(new TextComponent("This group is not ignorable!"));
            return 0;
        }

        final GroupConfig finalGroupConfig = groupConfig;

        player.getCapability(GroupProvider.GROUP_SETTINGS_CAP).ifPresent(settings -> {
            if (settings.getIgnoredGroups().contains(finalGroupConfig.getGroup()))
            {
                settings.removeIgnoredGroup(finalGroupConfig.getGroup());
                context.getSource().sendSuccess(new TextComponent("Group un-ignored"), true);
            }
            else
            {
                settings.addIgnoredGroup(finalGroupConfig.getGroup());
                context.getSource().sendSuccess(new TextComponent("Group ignored"), true);
            }
        });

        return 1;
    }

    protected static LiteralArgumentBuilder<CommandSourceStack> build()
    {
        return newLiteral(NAME)
                .then(newArgument(GROUP_NAME, StringArgumentType.word()).suggests(CommandUtils::getIgnorableGroupSuggestions)
                    .executes(IgnoreGroupCommand::onExecute));
    }
}
