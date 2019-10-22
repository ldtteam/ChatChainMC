package co.chatchain.mc.forge.commands;

import co.chatchain.mc.forge.ChatChainMC;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;

public class ReconnectCommand extends AbstractCommand
{
    private final static String NAME = "reconnect";

    private static int onExecute()
    {
        ChatChainMC.INSTANCE.getConnection().reconnect();
        return 1;
    }

    protected static LiteralArgumentBuilder<CommandSource> build()
    {
        return newLiteral(NAME)
                .executes(context -> ReconnectCommand.onExecute());
    }
}
