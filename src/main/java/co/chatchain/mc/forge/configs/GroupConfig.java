package co.chatchain.mc.forge.configs;

import co.chatchain.commons.messages.objects.Group;
import co.chatchain.mc.forge.capabilities.GroupProvider;
import co.chatchain.mc.forge.capabilities.IGroupSettings;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Delegate;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@ConfigSerializable
public class GroupConfig
{
    @Getter
    @Setter
    @Setting("group")
    private Group group;

    @Getter
    @Setting("cancel-chat-event")
    private boolean cancelChatEvent = false;

    @Setting("command-name")
    private String commandName = "";

    public String getCommandName()
    {
        if (commandName == null || commandName.isEmpty())
        {
            return group.getGroupName().replace(" ", "");
        }
        return commandName;
    }

    @Getter
    @Setter
    @Setting("allow-all-players")
    private boolean allowAllPlayers = false;

    @Delegate
    @Setting("allowed-players")
    private List<UUID> allowedPlayers = new ArrayList<>();

    public List<EntityPlayer> getPlayersForGroup()
    {
        final List<EntityPlayer> returnList = new ArrayList<>();
        if (allowAllPlayers)
        {
            returnList.addAll(FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayers());
        }
        else
        {
            for (final UUID uuid : allowedPlayers)
            {
                final EntityPlayer player = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUUID(uuid);
                if (FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayers().contains(player))
                {
                    returnList.add(player);
                }
            }
        }

        return returnList;
    }

    public List<EntityPlayer> getPlayersForGroupUnignored()
    {
        final List<EntityPlayer> returnList = new ArrayList<>();
        for (final EntityPlayer player : getPlayersForGroup())
        {
            final IGroupSettings groupSettings = player.getCapability(GroupProvider.GROUP_SETTINGS_CAP, null);

            if (groupSettings != null && !groupSettings.getIgnoredGroups().contains(group))
            {
                returnList.add(player);
            }
        }
        return returnList;
    }

    @Getter
    @Setter
    @Setting("is-group-mutable")
    private boolean isGroupIgnorable = true;
}
