package co.chatchain.mc.forge;

import co.chatchain.commons.core.interfaces.IMessageSender;
import co.chatchain.commons.core.interfaces.cases.IReceiveGroupsCase;
import co.chatchain.commons.core.interfaces.formatters.IClientEventFormatter;
import co.chatchain.commons.core.interfaces.formatters.IGenericMessageFormatter;
import co.chatchain.commons.core.interfaces.formatters.IUserEventFormatter;
import co.chatchain.commons.infrastructure.formatters.ClientEventFormatter;
import co.chatchain.commons.infrastructure.formatters.GenericMessageFormatter;
import co.chatchain.commons.infrastructure.formatters.UserEventFormatter;
import co.chatchain.commons.infrastructure.interfaces.replacements.*;
import co.chatchain.commons.infrastructure.replacements.*;
import co.chatchain.commons.interfaces.IConnectionConfig;
import co.chatchain.commons.interfaces.ILogger;
import co.chatchain.mc.forge.cases.ReceiveGroupsCase;
import co.chatchain.mc.forge.message.handling.MessageSender;
import co.chatchain.mc.forge.replacements.CustomClientRankReplacements;
import co.chatchain.mc.forge.replacements.CustomClientUserReplacements;
import co.chatchain.mc.forge.util.Log;
import com.google.inject.AbstractModule;

public class ChatChainMCModule extends AbstractModule
{
    @Override
    protected void configure()
    {
        bind(IMessageSender.class).to(MessageSender.class);
        bind(IReceiveGroupsCase.class).to(ReceiveGroupsCase.class);
        bind(IConnectionConfig.class).toInstance(ChatChainMC.INSTANCE.getMainConfig());
        bind(ILogger.class).to(Logger.class);

        //Formatters
        bind(IClientEventFormatter.class).to(ClientEventFormatter.class);
        bind(IGenericMessageFormatter.class).to(GenericMessageFormatter.class);
        bind(IUserEventFormatter.class).to(UserEventFormatter.class);

        //Replacements
        bind(IClientRankReplacements.class).to(CustomClientRankReplacements.ClientRankReplacementsInstance.class);
        bind(IClientReplacements.class).to(ClientReplacements.ClientReplacementsInstance.class);
        bind(IClientUserReplacements.class).to(CustomClientUserReplacements.ClientUserReplacementsInstance.class);
        bind(IGenericMessageReplacements.class).to(GenericMessageReplacements.GenericMessageReplacementsInstance.class);
        bind(IGroupReplacements.class).to(GroupReplacements.GroupReplacementsInstance.class);
    }

    private static class Logger implements ILogger
    {
        @Override
        public void error(final String error, final Exception exception)
        {
            Log.getLogger().error(error, exception);
        }
    }
}
