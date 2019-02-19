package co.chatchain.mc.message.objects;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Delegate;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@ConfigSerializable
public class Group
{
    @Getter
    @Setting("group-name")
    private String groupName;

    @Getter
    @Setting("group-ID")
    private String groupId;

    @Getter
    @Setter
    @Delegate
    @Setting("allowed-players")
    private List<UUID> allowedPlayers = new ArrayList<>();

    @Getter
    @Setter
    @Setting("allow-all-players")
    private boolean allowAllPlayers = false;

    @Getter
    @Setter
    @Setting("is-group-mutable")
    private boolean isGroupMutable = true;

    public Group() {}

    public Group(final String groupId)
    {
        this.groupId = groupId;
    }
}
