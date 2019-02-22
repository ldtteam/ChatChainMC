package co.chatchain.mc.configs;

import co.chatchain.mc.message.objects.Group;
import lombok.Getter;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.HashMap;
import java.util.Map;

@ConfigSerializable
public class GroupsConfig extends AbstractConfig
{

    @Setting(value = "default-group")
    private String defaultGroup = "";

    @Setting(value = "group-storage")
    @Getter
    private Map<String, Group> groupStorage = new HashMap<>();

    public Group getDefaultGroup()
    {
        Group returnGroup = null;

        if (defaultGroup == null || defaultGroup.isEmpty())
        {
            for (final String key : groupStorage.keySet())
            {
                if (groupStorage.get(key).isAllowAllPlayers())
                {
                    returnGroup = groupStorage.get(key);
                    break;
                }
            }

            returnGroup = groupStorage.entrySet().stream().findFirst().get().getValue();
        }

        if (groupStorage.containsKey(defaultGroup))
        {
            returnGroup = groupStorage.get(defaultGroup);
        }

        return returnGroup;
    }
}
