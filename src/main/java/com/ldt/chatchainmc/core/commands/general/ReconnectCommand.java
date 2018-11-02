package com.ldt.chatchainmc.core.commands.general;

import com.ldt.chatchainmc.core.ChatChainMC;
import com.ldt.chatchainmc.core.commands.AbstractSingleCommand;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class ReconnectCommand extends AbstractSingleCommand
{
    public static final String DESC = "reconnect";

    public ReconnectCommand(@NotNull final String... parents)
    {
        super(parents);
    }

    @Override
    public void execute(final MinecraftServer server, final ICommandSender sender, final String[] args) throws CommandException
    {
        ChatChainMC.instance.connectToAPI();
        sender.sendMessage(new TextComponentString("Reconnection has begun. Check server log for more information."));
    }

    @Override
    public @NotNull List<String> getTabCompletionOptions(
      final @NotNull MinecraftServer server, final @NotNull ICommandSender sender, @NotNull final String[] args, @Nullable final BlockPos pos)
    {
        return Collections.emptyList();
    }

    @Override
    public boolean isUsernameIndex(@NotNull final String[] args, final int index)
    {
        return false;
    }
}
