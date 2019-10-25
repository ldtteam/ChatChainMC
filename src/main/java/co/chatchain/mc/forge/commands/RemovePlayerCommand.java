package co.chatchain.mc.forge.commands;

import co.chatchain.mc.forge.ChatChainMC;
import co.chatchain.mc.forge.configs.GroupConfig;
import co.chatchain.mc.forge.util.CommandUtils;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;

public class RemovePlayerCommand extends AbstractCommand
{

    private final static String NAME = "remove-player";

    private final static String GROUP_NAME = "groupName";

    private final static String PLAYER = "player";

    private static int onExecute(final CommandContext<CommandSource> context) throws CommandSyntaxException
    {
        if (!context.getSource().hasPermissionLevel(2))
        {
            context.getSource().sendErrorMessage(new StringTextComponent("You must be OP level 2"));
            return 0;
        }

        final ServerPlayerEntity player = EntityArgument.getPlayer(context, PLAYER);
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
            context.getSource().sendErrorMessage(new StringTextComponent("This group is invalid!"));
            return 0;
        }

        if (groupConfig.contains(player.getUniqueID()))
        {
            groupConfig.remove(player.getUniqueID());
        }

        ChatChainMC.INSTANCE.getGroupsConfig().save();

        return 1;
    }

    protected static LiteralArgumentBuilder<CommandSource> build()
    {
        return newLiteral(NAME)
                .requires(source -> source.hasPermissionLevel(2))
                .then(newArgument(GROUP_NAME, StringArgumentType.word()).suggests(CommandUtils::getAllGroupSuggestions)
                        .then(newArgument(PLAYER, EntityArgument.player())
                                .executes(RemovePlayerCommand::onExecute)));
    }
}
