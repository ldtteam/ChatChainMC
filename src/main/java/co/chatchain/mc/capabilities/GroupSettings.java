package co.chatchain.mc.capabilities;

import co.chatchain.commons.messages.objects.Group;
import co.chatchain.mc.ChatChainMC;

import java.util.ArrayList;
import java.util.List;

public class GroupSettings implements IGroupSettings
{

    private List<String> mutedGroups = new ArrayList<>();
    private String talkingGroup = ChatChainMC.instance.getGroupsConfig().getDefaultGroup();

    @Override
    public void addMutedGroup(Group group)
    {
        this.mutedGroups.add(group.getGroupId());
    }

    @Override
    public void removeMutedGroup(Group group)
    {
        this.mutedGroups.remove(group.getGroupId());
    }

    @Override
    public List<Group> getMutedGroups()
    {
        final List<Group> groups = new ArrayList<>();

        for (final String groupId : mutedGroups)
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
        this.talkingGroup = group.getGroupId();
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
