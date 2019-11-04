package co.chatchain.mc.forge.capabilities;

import co.chatchain.commons.objects.Group;
import co.chatchain.mc.forge.ChatChainMC;

import java.util.ArrayList;
import java.util.List;

public class GroupSettings implements IGroupSettings
{

    private List<String> ignoredGroups = new ArrayList<>();
    private String talkingGroup = ChatChainMC.instance.getGroupsConfig().getDefaultGroup();

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
            if (ChatChainMC.instance.getGroupsConfig().getGroupStorage().containsKey(groupId))
            {
                groups.add(ChatChainMC.instance.getGroupsConfig().getGroupStorage().get(groupId).getGroup());
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
    public Group getTalkingGroup()
    {
        if (ChatChainMC.instance.getGroupsConfig().getGroupStorage().containsKey(talkingGroup))
        {
            return ChatChainMC.instance.getGroupsConfig().getGroupStorage().get(talkingGroup).getGroup();
        }
        return null;
    }
}
