package co.chatchain.mc.forge.capabilities;

import co.chatchain.commons.core.entities.Group;
import co.chatchain.mc.forge.ChatChainMC;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GroupSettings implements IGroupSettings
{
    private final List<String> ignoredGroups = new ArrayList<>();
    private String talkingGroup = ChatChainMC.INSTANCE.getGroupsConfig().getDefaultGroup();

    @Override
    public void addIgnoredGroup(Group group)
    {
        this.ignoredGroups.add(group.getId());
    }

    @Override
    public void removeIgnoredGroup(Group group)
    {
        this.ignoredGroups.remove(group.getId());
    }

    @Override
    public List<Group> getIgnoredGroups()
    {
        final List<Group> groups = new ArrayList<>();

        for (final String groupId : ignoredGroups)
        {
            if (ChatChainMC.INSTANCE.getGroupsConfig().getGroupStorage().containsKey(groupId))
            {
                groups.add(ChatChainMC.INSTANCE.getGroupsConfig().getGroupStorage().get(groupId).getGroup());
            }
        }

        return groups;
    }

    @Override
    public void setTalkingGroup(Group group)
    {
        this.talkingGroup = group.getId();
    }

    @Override
    public void setTalkingGroup(String group)
    {
        this.talkingGroup = group;
    }

    @Override
    public Group getTalkingGroup()
    {
        if (ChatChainMC.INSTANCE.getGroupsConfig().getGroupStorage().containsKey(talkingGroup))
        {
            return ChatChainMC.INSTANCE.getGroupsConfig().getGroupStorage().get(talkingGroup).getGroup();
        }
        return null;
    }
}
