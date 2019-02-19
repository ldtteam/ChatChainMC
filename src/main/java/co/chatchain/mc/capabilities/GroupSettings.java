package co.chatchain.mc.capabilities;

import java.util.ArrayList;
import java.util.List;

public class GroupSettings implements IGroupSettings
{

    private List<String> mutedGroups = new ArrayList<>();
    private String talkingGroup = "4e57261d-bcc7-47b8-8aa0-e0d7048faebc";

    @Override
    public void addMutedGroup(String group)
    {
        this.mutedGroups.add(group);
    }

    @Override
    public void removeMutedGroup(String group)
    {
        this.mutedGroups.remove(group);
    }

    @Override
    public List<String> getMutedGroups()
    {
        return new ArrayList<>(this.mutedGroups);
    }

    @Override
    public void setTalkingGroup(String group)
    {
        this.talkingGroup = group;
    }

    @Override
    public String getTalkingGroup()
    {
        return this.talkingGroup;
    }
}
