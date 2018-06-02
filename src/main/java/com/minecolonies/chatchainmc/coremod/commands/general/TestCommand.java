package com.minecolonies.chatchainmc.coremod.commands.general;

import com.minecolonies.chatchainmc.coremod.ChatChainMC;
import com.minecolonies.chatchainmc.coremod.commands.ActionMenuState;
import com.minecolonies.chatchainmc.coremod.commands.IActionCommand;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.NotNull;

public class TestCommand implements IActionCommand
{
    public static final String DESC = "test";

    /**
     * no-args constructor called by new CommandEntryPoint executer.
     */
    public TestCommand()
    {
        super();
    }

    @Override
    public void execute(
      @NotNull final MinecraftServer server, @NotNull final ICommandSender sender, @NotNull final ActionMenuState actionMenuState) throws CommandException
    {
        executeShared(server, sender);
    }

    private void executeShared(@NotNull final MinecraftServer server, @NotNull final ICommandSender sender) throws CommandException
    {
        for (final String client : ChatChainMC.instance.getClientConfigs().clientConfigs.keySet())
        {
            if (ChatChainMC.instance.getClientConfigs().clientConfigs.get(client).display)
            {
                ChatChainMC.instance.getConnection().send("RequestJoined", ChatChainMC.CLIENT_TYPE, ChatChainMC.instance.getMainConfig().clientName, client);
            }
        }
    }
}
