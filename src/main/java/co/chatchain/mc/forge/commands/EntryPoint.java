package co.chatchain.mc.forge.commands;

import co.chatchain.mc.forge.ChatChainMC;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandSource;

public class EntryPoint extends AbstractCommand
{
    /**
     * Registers mod command tree to given dispatcher.
     *
     * @param dispatcher main server command dispatcher
     */
    public static void register(final CommandDispatcher<CommandSource> dispatcher)
    {
        final CommandTree chatChainRoot = new CommandTree(ChatChainMC.MOD_ID)
                .addNode(IgnoreGroupCommand.build())
                .addNode(ReloadCommand.build())
                .addNode(ReconnectCommand.build())
                .addNode(TalkInGroupCommand.build())
                .addNode(UUIDCommand.build())
                .addNode(AddPlayerCommand.build())
                .addNode(RemovePlayerCommand.build())
                .addNode(StatsCommand.build());

        final CommandTree chatChainRootAlias1 = new CommandTree("chatchain")
                .addNode(IgnoreGroupCommand.build())
                .addNode(ReloadCommand.build())
                .addNode(ReconnectCommand.build())
                .addNode(TalkInGroupCommand.build())
                .addNode(UUIDCommand.build())
                .addNode(AddPlayerCommand.build())
                .addNode(RemovePlayerCommand.build())
                .addNode(StatsCommand.build());

        final CommandTree chatChainRootAlias2 = new CommandTree("chat")
                .addNode(IgnoreGroupCommand.build())
                .addNode(ReloadCommand.build())
                .addNode(ReconnectCommand.build())
                .addNode(TalkInGroupCommand.build())
                .addNode(UUIDCommand.build())
                .addNode(AddPlayerCommand.build())
                .addNode(RemovePlayerCommand.build())
                .addNode(StatsCommand.build());

        final CommandTree chatChainRootAlias3 = new CommandTree("ccmc")
                .addNode(IgnoreGroupCommand.build())
                .addNode(ReloadCommand.build())
                .addNode(ReconnectCommand.build())
                .addNode(TalkInGroupCommand.build())
                .addNode(UUIDCommand.build())
                .addNode(AddPlayerCommand.build())
                .addNode(RemovePlayerCommand.build())
                .addNode(StatsCommand.build());

        dispatcher.register(chatChainRoot.build());
        dispatcher.register(chatChainRootAlias1.build());
        dispatcher.register(chatChainRootAlias2.build());
        dispatcher.register(chatChainRootAlias3.build());
    }
}
