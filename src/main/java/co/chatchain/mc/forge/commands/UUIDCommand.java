package co.chatchain.mc.forge.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;

@SuppressWarnings({"SameReturnValue", "unused"})
public class UUIDCommand extends AbstractCommand
{
    private final static String NAME = "uuid";

    private final static String PLAYER = "player";

    private static int onExecute(final CommandContext<CommandSourceStack> context) throws CommandSyntaxException
    {
        final ServerPlayer player = EntityArgument.getPlayer(context, PLAYER);
        context.getSource().sendSuccess(new TextComponent("Player UUID: " + player.getUUID()), true);
        return 1;
    }

    protected static LiteralArgumentBuilder<CommandSourceStack> build()
    {
        return newLiteral(NAME)
                .requires(source -> source.hasPermission(2))
                .then(newArgument(PLAYER, EntityArgument.player())
                        .executes(UUIDCommand::onExecute));
    }
}
