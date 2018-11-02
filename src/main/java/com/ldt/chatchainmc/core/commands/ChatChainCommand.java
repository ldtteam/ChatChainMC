package com.ldt.chatchainmc.core.commands;

import com.google.common.collect.ImmutableMap;
import com.ldt.chatchainmc.core.commands.general.*;

import java.util.Map;

public class ChatChainCommand extends AbstractSplitCommand
{
    public static final String DESC = "chatchainmc";

    private final ImmutableMap<String, ISubCommand> subCommands =
      new ImmutableMap.Builder<String, ISubCommand>()
        //.put(StaffCommand.DESC, new StaffCommand(DESC))
        .put(ReloadCommand.DESC, new ReloadCommand(DESC))
        .put(ReconnectCommand.DESC, new ReconnectCommand(DESC))
        .put(ListenCommand.DESC, new ListenCommand(DESC))
        .put(MuteCommand.DESC, new MuteCommand(DESC))
        .put(ChatCommand.DESC, new ChatCommand(DESC))
        .build();

    public ChatChainCommand()
    {
        super(DESC);
    }

    @Override
    public Map<String, ISubCommand> getSubCommands()
    {
        return subCommands;
    }
}
