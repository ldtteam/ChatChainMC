package co.chatchain.mc.forge.util;

import co.chatchain.commons.core.entities.ClientUser;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;

public class UserUtils
{
    public static ClientUser getClientUserFromPlayer(final ServerPlayer playerEntity)
    {
        return new ClientUser(playerEntity.getDisplayName().getString(), playerEntity.getUUID().toString(), null, null, new ArrayList<>());
    }
}
