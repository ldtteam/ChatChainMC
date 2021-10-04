package co.chatchain.mc.forge.configs;

import co.chatchain.commons.core.entities.Group;
import co.chatchain.mc.forge.ChatChainMC;
import co.chatchain.mc.forge.capabilities.GroupProvider;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Delegate;
import net.minecraft.server.level.ServerPlayer;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@SuppressWarnings("CanBeFinal")
@ConfigSerializable
public class GroupConfig
{
    @Getter
    @Setter
    @Setting("group")
    private Group group;

    @Getter
    @Setting("can-allowed-chat")
    private boolean canAllowedChat = true;

    @Getter
    @Setting("cancel-chat-event")
    private boolean cancelChatEvent = false;

    @Setting("command-name")
    private String commandName = "";

    public String getCommandName()
    {
        if ((commandName == null || commandName.isEmpty()) && group.getName() != null)
        {
            return group.getName().replace(" ", "");
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

    public List<ServerPlayer> getPlayersForGroup()
    {
        if (allowAllPlayers)
        {
            return ChatChainMC.MINECRAFT_SERVER.getPlayerList().getPlayers();
        }
        else
        {
            final List<ServerPlayer> returnList = new ArrayList<>();
            for (final UUID uuid : allowedPlayers)
            {
                final ServerPlayer player = ChatChainMC.MINECRAFT_SERVER.getPlayerList().getPlayer(uuid);
                if (ChatChainMC.MINECRAFT_SERVER.getPlayerList().getPlayers().contains(player))
                {
                    returnList.add(player);
                }
            }

            return returnList;
        }
    }

    public List<ServerPlayer> getPlayersCanTalk()
    {
        if (!canAllowedChat)
        {
            return new ArrayList<>();
        }

        return getPlayersForGroup();
    }

    public List<ServerPlayer> getPlayersListening()
    {
        final List<ServerPlayer> returnList = new ArrayList<>();
        for (final ServerPlayer player : getPlayersForGroup())
        {
            player.getCapability(GroupProvider.GROUP_SETTINGS_CAP).ifPresent(groupSettings ->
            {
                if (!groupSettings.getIgnoredGroups().contains(group))
                {
                    returnList.add(player);
                }
            });
        }
        return returnList;
    }

    @Getter
    @Setter
    @Setting("is-group-mutable")
    private boolean isGroupIgnorable = true;
}
