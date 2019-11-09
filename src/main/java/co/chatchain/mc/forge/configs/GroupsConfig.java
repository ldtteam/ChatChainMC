package co.chatchain.mc.forge.configs;

import co.chatchain.commons.objects.Group;
import lombok.Getter;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ConfigSerializable
public class GroupsConfig extends AbstractConfig
{

    @Setting(value = "default-group")
    private String defaultGroup = "";

    @Getter
    @Setting(value = "group-storage")
    private Map<String, GroupConfig> groupStorage = new HashMap<>();

    public String getDefaultGroup()
    {
        Group returnGroup = null;

        if (groupStorage.containsKey(defaultGroup))
        {
            returnGroup = groupStorage.get(defaultGroup).getGroup();
        }
        else
        {
            for (final String key : groupStorage.keySet())
            {
                if (groupStorage.get(key).isAllowAllPlayers())
                {
                    returnGroup = groupStorage.get(key).getGroup();
                    break;
                }
            }
        }

        return returnGroup != null ? returnGroup.getId() : "";
    }
}
