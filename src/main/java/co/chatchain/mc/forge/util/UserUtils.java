package co.chatchain.mc.forge.util;

import co.chatchain.commons.core.entities.ClientUser;
import net.minecraft.entity.player.ServerPlayerEntity;

import java.util.ArrayList;

public class UserUtils
{
    public static ClientUser getClientUserFromPlayer(final ServerPlayerEntity playerEntity)
    {
        return new ClientUser(playerEntity.getDisplayName().getString(), playerEntity.getUniqueID().toString(), null, null, new ArrayList<>());
    }
}
