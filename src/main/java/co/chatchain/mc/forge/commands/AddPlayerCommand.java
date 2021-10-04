package co.chatchain.mc.forge.commands;

import co.chatchain.mc.forge.ChatChainMC;
import co.chatchain.mc.forge.configs.GroupConfig;
import co.chatchain.mc.forge.util.CommandUtils;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;

public class AddPlayerCommand extends AbstractCommand
{

    private final static String NAME = "add-player";

    private final static String GROUP_NAME = "groupName";

    private final static String PLAYER = "player";

    private static int onExecute(final CommandContext<CommandSourceStack> context) throws CommandSyntaxException
    {
        if (!context.getSource().hasPermission(2))
        {
            context.getSource().sendFailure(new TextComponent("You must be OP level 2"));
            return 0;
        }

        final ServerPlayer player = EntityArgument.getPlayer(context, PLAYER);
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

        if (groupConfig == null)
        {
            context.getSource().sendFailure(new TextComponent("This group is invalid!"));
            return 0;
        }

        if (!groupConfig.contains(player.getUUID()))
        {
            groupConfig.add(player.getUUID());
        }

        ChatChainMC.INSTANCE.getGroupsConfig().save();

        return 1;
    }

    protected static LiteralArgumentBuilder<CommandSourceStack> build()
    {
        return newLiteral(NAME)
                .requires(source -> source.hasPermission(2))
                .then(newArgument(GROUP_NAME, StringArgumentType.word()).suggests(CommandUtils::getAllGroupSuggestions)
                        .then(newArgument(PLAYER, EntityArgument.player())
                                .executes(AddPlayerCommand::onExecute)));
    }
}
