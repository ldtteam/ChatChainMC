package co.chatchain.mc.forge.cases;

import co.chatchain.commons.core.entities.Group;
import co.chatchain.commons.core.entities.messages.GetGroupsMessage;
import co.chatchain.commons.core.interfaces.cases.IReceiveGroupsCase;
import co.chatchain.mc.forge.ChatChainMC;
import co.chatchain.mc.forge.configs.GroupConfig;
import co.chatchain.mc.forge.configs.GroupsConfig;

public class ReceiveGroupsCase implements IReceiveGroupsCase
{
    @Override
    public boolean handle(final GetGroupsMessage message)
    {
        final GroupsConfig groupsConfig = ChatChainMC.INSTANCE.getGroupsConfig();

        for (final Group group : message.getGroups())
        {
            if (!groupsConfig.getGroupStorage().containsKey(group.getId()))
            {
                GroupConfig config = new GroupConfig();
                config.setGroup(group);
                groupsConfig.getGroupStorage().put(group.getId(), config);
            }
        }
        groupsConfig.save();
        return true;
    }
}
