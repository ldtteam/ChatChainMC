package com.minecolonies.discordianmc.commands.general;

import com.minecolonies.discordianmc.DiscordianMC;
import com.minecolonies.discordianmc.commands.ActionMenuState;
import com.minecolonies.discordianmc.commands.IActionCommand;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import org.jetbrains.annotations.NotNull;

public class ReloadCommand implements IActionCommand
{
    public static final String DESC = "reload";

    /**
     * no-args constructor called by new CommandEntryPoint executer.
     */
    public ReloadCommand()
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
        DiscordianMC.instance.reloadConfigs();

        sender.sendMessage(new TextComponentString("Clients: " + DiscordianMC.instance.getClientConfigs().clientConfigs.keySet()));
    }
}
