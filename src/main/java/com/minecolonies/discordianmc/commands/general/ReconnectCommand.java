package com.minecolonies.discordianmc.commands.general;

import com.minecolonies.discordianmc.DiscordianMC;
import com.minecolonies.discordianmc.commands.ActionMenuState;
import com.minecolonies.discordianmc.commands.IActionCommand;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.NotNull;

public class ReconnectCommand implements IActionCommand
{
    public static final String DESC = "reconnect";

    /**
     * no-args constructor called by new CommandEntryPoint executer.
     */
    public ReconnectCommand()
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
        DiscordianMC.instance.connectToAPI();
    }

}
