package co.chatchain.mc.commands;

import net.minecraft.command.ICommandSender;
import net.minecraftforge.server.command.CommandTreeBase;

public class BaseCommand extends CommandTreeBase
{

    public BaseCommand()
    {
        addSubcommand(new TalkInGroupCommand());
        addSubcommand(new MuteGroupCommand());
        addSubcommand(new ReloadCommand());
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
        return "chatchain <talk | mute>";
    }
}