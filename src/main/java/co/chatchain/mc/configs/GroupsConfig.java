package co.chatchain.mc.configs;

import co.chatchain.mc.ChatChainMC;
import co.chatchain.mc.message.objects.Group;
import lombok.Getter;
import net.minecraft.entity.player.EntityPlayer;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.*;

@ConfigSerializable
public class GroupsConfig extends AbstractConfig
{

    @Setting(value = "default-group")
    private String defaultGroup = "";

    @Getter
    @Setting(value = "client-events-groups")
    private List<String> clientEventGroups = new ArrayList<>();

    @Getter
    @Setting(value = "group-storage")
    private Map<String, Group> groupStorage = new HashMap<>();

    public List<EntityPlayer> getPlayersForGroup(final Group group)
    {
        final List<EntityPlayer> returnList = new ArrayList<>();
        if (group.isAllowAllPlayers())
        {
            returnList.addAll(ChatChainMC.instance.getServer().getPlayerList().getPlayers());
        }
        else
        {
            for (final UUID uuid : group.getAllowedPlayers())
            {
                final EntityPlayer player = ChatChainMC.instance.getServer().getPlayerList().getPlayerByUUID(uuid);
                if (ChatChainMC.instance.getServer().getPlayerList().getPlayers().contains(player))
                {
                    returnList.add(player);
                }
            }
        }

        return returnList;
    }

    public String getDefaultGroup()
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

            //returnGroup = groupStorage.entrySet().stream().findFirst().get().getValue();
        }

        if (groupStorage.containsKey(defaultGroup))
        {
            returnGroup = groupStorage.get(defaultGroup);
        }

        return returnGroup != null ? returnGroup.getGroupId() : "";
    }
}
