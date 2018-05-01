package com.minecolonies.discordianmc.commands.general;

import com.minecolonies.discordianmc.DiscordianMC;
import com.minecolonies.discordianmc.commands.ActionMenuState;
import com.minecolonies.discordianmc.commands.IActionCommand;
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
        for (final String client : DiscordianMC.instance.getClientConfigs().clientConfigs.keySet())
        {
            if (DiscordianMC.instance.getClientConfigs().clientConfigs.get(client).display)
            {
                DiscordianMC.instance.getConnection().send("RequestJoined", DiscordianMC.CLIENT_TYPE, DiscordianMC.instance.getMainConfig().clientName, client);
            }
        }
    }
}
