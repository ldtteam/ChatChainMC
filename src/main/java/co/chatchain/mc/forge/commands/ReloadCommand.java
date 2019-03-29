package co.chatchain.mc.forge.commands;

import co.chatchain.mc.forge.ChatChainMC;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.NotNull;

public class ReloadCommand extends CommandBase
{

    @NotNull
    @Override
    public String getName()
    {
        return "reload";
    }

    @NotNull
    @Override
    public String getUsage(@NotNull ICommandSender sender)
    {
        return "/chatchain reload";
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender)
    {
        return sender.canUseCommand(2, "");
    }

    @Override
    public void execute(@NotNull MinecraftServer server, @NotNull ICommandSender sender, @NotNull String[] args)
    {
        ChatChainMC.instance.reloadConfigs();
    }
}
