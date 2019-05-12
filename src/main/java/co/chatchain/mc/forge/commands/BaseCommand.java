package co.chatchain.mc.forge.commands;

import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.server.command.CommandTreeBase;

public class BaseCommand extends CommandTreeBase
{

    public BaseCommand()
    {
        addSubcommand(new TalkInGroupCommand());
        addSubcommand(new IgnoreGroupCommand());
        addSubcommand(new ReloadCommand());
        addSubcommand(new UUIDCommand());
        //addSubcommand(new AddPlayerCommand());
    }

    @Override
    public String getName()
    {
        return "chatchain";
    }

    @Override
    public String getUsage(ICommandSender sender)
    {
        if (sender.canUseCommand(2, ""))
            return "chatchain <talk | ignore | reload | uuid>";
        return "chatchain <talk | ignore>";
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender)
    {
        return true;
    }
}
