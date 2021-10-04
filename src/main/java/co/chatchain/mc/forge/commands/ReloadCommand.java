package co.chatchain.mc.forge.commands;

import co.chatchain.mc.forge.ChatChainMC;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;

@SuppressWarnings("SameReturnValue")
public class ReloadCommand extends AbstractCommand
{
    private final static String NAME = "reload";

    private static int onExecute()
    {
        ChatChainMC.INSTANCE.reloadConfigs();
        return 1;
    }

    protected static LiteralArgumentBuilder<CommandSourceStack> build()
    {
        return newLiteral(NAME)
                .executes(context -> ReloadCommand.onExecute());
    }
}
