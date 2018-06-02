package com.minecolonies.chatchainmc.core.commands;

import com.google.common.primitives.Ints;
import com.mojang.authlib.GameProfile;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.FakePlayer;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public enum ActionArgumentType
{
    ONLINE_PLAYER("online-player-expression", 0),
    PLAYER("player-expression", 0),
    COORDINATE_X("x-coordinate", 0),
    COORDINATE_Y("y-coordinate", 0),
    COORDINATE_Z("z-coordinate", 0),
    BOOLEAN("boolean", 0),
    INTEGER("integer", 0),
    STRING("string", 0)
    ;

    private static final String ABANDONED_FAKE_PLAYER_NAME = "[abandoned]";

    public enum Is
    {
        REQUIRED,
        OPTIONAL
    }

    @Nonnull private final String usageValue;
    private final int allowedSpaceCount;

    ActionArgumentType(@Nonnull final String usageValue, final int allowedSpaceCount)
    {
        this.usageValue = usageValue;
        this.allowedSpaceCount = allowedSpaceCount;
    }

    public String getUsageValue()
    {
        return usageValue;
    }

    public int allowedSpaceCount()
    {
        return allowedSpaceCount;
    }

    @NotNull
    private static List<String> getOnlinePlayerNames(@NotNull final MinecraftServer server)
    {
        final String[] onlinePlayerNames = server.getOnlinePlayerNames();
        return Arrays.asList(onlinePlayerNames);
    }

    @NotNull
    private static List<String> getAllPlayerNames(@NotNull final MinecraftServer server)
    {
        final PlayerList playerList = server.getPlayerList();
        final List<EntityPlayerMP> allPlayersList = playerList.getPlayers();
        final List<String> playerNames = new ArrayList<>(allPlayersList.size());
        for (final EntityPlayerMP entityPlayerMP : allPlayersList)
        {
            final String playerName = entityPlayerMP.getName();
            if (!playerNames.contains(playerName))
            {
                playerNames.add(playerName);
            }
        }
        return playerNames;
    }

    @NotNull
    public List<String> getTabCompletions(@NotNull final MinecraftServer server,
            @Nullable final BlockPos pos,
            @NotNull final ActionMenuState actionMenuState, final String potentialArgumentValue)
    {
        switch (this)
        {
            case INTEGER:
            case STRING:
                return Collections.emptyList();
            case BOOLEAN:
                return Arrays.asList(new String[] {"true", "false"});
            case COORDINATE_X:
            case COORDINATE_Y:
            case COORDINATE_Z:
                return getCoordinateTabCompletions(pos, potentialArgumentValue);
            case ONLINE_PLAYER:
                final List<String> onlinePlayerNameStrings = getOnlinePlayerNames(server);
                return onlinePlayerNameStrings.stream().filter(k -> k.startsWith(potentialArgumentValue)).collect(Collectors.toList());
            case PLAYER:
                final List<String> allPlayerNameStrings = getAllPlayerNames(server);
                return allPlayerNameStrings.stream().filter(k -> k.startsWith(potentialArgumentValue)).collect(Collectors.toList());
            default:
                throw new IllegalStateException("Unimplemented ActionArgumentType tab completion");
        }
    }

    @NotNull
    private List<String> getCoordinateTabCompletions(@Nullable final BlockPos pos, final String potentialArgumentValue)
    {
        if (null == pos)
        {
            return Collections.emptyList();
        }
        if (potentialArgumentValue.isEmpty())
        {
            switch (this)
            {
                case COORDINATE_X:
                    return Collections.singletonList(String.valueOf(pos.getX()));
                case COORDINATE_Y:
                    return Collections.singletonList(String.valueOf(pos.getY()));
                case COORDINATE_Z:
                    return Collections.singletonList(String.valueOf(pos.getZ()));
                default:
                    // We will never reach here.
                    break;
            }
            return Collections.emptyList();
        }
        return Collections.emptyList();
    }

    @Nullable
    public Object parse(@NotNull final MinecraftServer server, @NotNull final ICommandSender sender, @Nullable final BlockPos pos,
            @NotNull final List<ActionMenuHolder> parsedHolders,
            final String potentialArgumentValue)
    {
        // TODO: selector support, such as used by CommandKill to find player
        // Entity entity = <net.minecraft.command.CommandBase>.getEntity(server, sender, args[0]);

        switch (this)
        {
            case INTEGER:
            case COORDINATE_X:
            case COORDINATE_Y:
            case COORDINATE_Z:
                return Ints.tryParse(potentialArgumentValue);
            case BOOLEAN:
                return parseBoolean(potentialArgumentValue);
            case ONLINE_PLAYER:
                return parseOnlinePlayerValue(server, potentialArgumentValue);
            case PLAYER:
                return parseAnyPlayerValue(server, potentialArgumentValue);
            case STRING:
                return potentialArgumentValue.isEmpty() ? null : potentialArgumentValue;
            default:
                throw new IllegalStateException("Unimplemented ActionArgumentType parsing");
        }
    }

    /*
     * Suppressing Sonar Rule squid:S2447
     * This rule complains about returning null for a Boolean method.
     * But in this case the rule does not apply because
     * We are returning null to indicate that no boolean value could be parsed.
     */
    @SuppressWarnings({"squid:S2447"})
    @Nullable
    private static Boolean parseBoolean(final String potentialArgumentValue)
    {
        if ("true".equalsIgnoreCase(potentialArgumentValue))
        {
            return Boolean.TRUE;
        }
        if ("t".equalsIgnoreCase(potentialArgumentValue))
        {
            return Boolean.TRUE;
        }
        if ("yes".equalsIgnoreCase(potentialArgumentValue))
        {
            return Boolean.TRUE;
        }
        if ("y".equalsIgnoreCase(potentialArgumentValue))
        {
            return Boolean.TRUE;
        }
        if ("1".equalsIgnoreCase(potentialArgumentValue))
        {
            return Boolean.TRUE;
        }
        if ("false".equalsIgnoreCase(potentialArgumentValue))
        {
            return Boolean.FALSE;
        }
        if ("f".equalsIgnoreCase(potentialArgumentValue))
        {
            return Boolean.FALSE;
        }
        if ("no".equalsIgnoreCase(potentialArgumentValue))
        {
            return Boolean.FALSE;
        }
        if ("n".equalsIgnoreCase(potentialArgumentValue))
        {
            return Boolean.FALSE;
        }
        if ("0".equalsIgnoreCase(potentialArgumentValue))
        {
            return Boolean.FALSE;
        }
        return null;
    }

    @Nullable
    private EntityPlayerMP parseOnlinePlayerValue(@NotNull final MinecraftServer server, final String potentialArgumentValue)
    {
        final List<String> playerNameStrings = getOnlinePlayerNames(server);
        if (playerNameStrings.contains(potentialArgumentValue))
        {
            return server.getPlayerList().getPlayerByUsername(potentialArgumentValue);
        }
        else
        {
            if (ABANDONED_FAKE_PLAYER_NAME.equals(potentialArgumentValue))
            {
                return new FakePlayer(server.getWorld(0), new GameProfile(UUID.randomUUID(), ABANDONED_FAKE_PLAYER_NAME));
            }
            return null;
        }
    }

    @Nullable
    private EntityPlayerMP parseAnyPlayerValue(@NotNull final MinecraftServer server, final String potentialArgumentValue)
    {
        final List<String> playerNameStrings = getAllPlayerNames(server);
        if (playerNameStrings.contains(potentialArgumentValue))
        {
            return server.getPlayerList().getPlayerByUsername(potentialArgumentValue);
        }
        else
        {
            if (ABANDONED_FAKE_PLAYER_NAME.equals(potentialArgumentValue))
            {
                return new FakePlayer(server.getWorld(0), new GameProfile(UUID.randomUUID(), ABANDONED_FAKE_PLAYER_NAME));
            }
            return null;
        }
    }
}

