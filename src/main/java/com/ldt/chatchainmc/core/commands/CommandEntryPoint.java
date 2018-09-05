package com.ldt.chatchainmc.core.commands;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public class CommandEntryPoint extends CommandBase
{

    @NotNull
    private final ChatChainCommand root;

    public CommandEntryPoint()
    {
        super();
        root = new ChatChainCommand();
    }

    @Override
    public String getName()
    {
        return "chatchainmc";
    }

    @Override
    public String getUsage(final ICommandSender sender)
    {
        return root.getCommandUsage(sender);
    }

    @Override
    public void execute(final MinecraftServer server, final ICommandSender sender, final String[] args) throws CommandException
    {
        root.execute(server, sender, args);
    }

    @Override
    public List<String> getAliases()
    {
        return Arrays.asList("ccmc", "cc", "chatchain");
    }

    @Override
    public List<String> getTabCompletions(final MinecraftServer server, final ICommandSender sender, final String[] args, @Nullable final BlockPos targetPos)
    {
        return root.getTabCompletionOptions(server, sender, args, targetPos);
    }

    @Override
    public boolean isUsernameIndex(final String[] args, final int index)
    {
        return super.isUsernameIndex(args, index);
    }

    @Override
    public boolean checkPermission(final MinecraftServer server, final ICommandSender sender)
    {
        return true;
    }
}
