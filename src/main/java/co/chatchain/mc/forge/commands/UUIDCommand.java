package co.chatchain.mc.forge.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;

public class UUIDCommand extends AbstractCommand
{

    private final static String NAME = "uuid";

    private final static String PLAYER = "player";

    private static int onExecute(final CommandContext<CommandSource> context) throws CommandSyntaxException
    {
        final ServerPlayerEntity player = EntityArgument.getPlayer(context, PLAYER);
        context.getSource().sendFeedback(new StringTextComponent("Player UUID: " + player.getUniqueID()), true);
        return 1;
    }

    protected static LiteralArgumentBuilder<CommandSource> build()
    {
        return newLiteral(NAME)
                .then(newArgument(PLAYER, EntityArgument.player())
                        .executes(UUIDCommand::onExecute));
    }
}
