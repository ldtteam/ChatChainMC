package co.chatchain.mc.configs;

import co.chatchain.mc.message.objects.Group;
import lombok.Getter;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.*;

@ConfigSerializable
public class GroupsConfig extends AbstractConfig
{

    @Setting(value = "group-storage")
    @Getter
    private Map<String, Group> groupStorage = new HashMap<>();

}
