package co.chatchain.mc.forge.compatibility.sponge;

import co.chatchain.mc.forge.ChatChainMC;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.service.permission.SubjectReference;

import java.util.Optional;

@Plugin(
        id = ChatChainMC.MOD_ID,
        name = ChatChainMC.MOD_NAME,
        version = ChatChainMC.VERSION,
        description = "Sponge Companion plugin for ChatChainMC Mod",
        url = "https://github.com/ldtteam/ChatChainMC")
public class ChatChainSpongePlugin
{

    public ChatChainSpongePlugin()
    {
        ChatChainMC.instance.setSpongeIsPresent(true);
    }

    public static String getPlayerRank(final EntityPlayer player)
    {
        final Player spongePlayer = (Player) player;
        final Optional<SubjectReference> optionalRank = spongePlayer.getParents().stream().findFirst();
        return optionalRank.map(SubjectReference::getSubjectIdentifier).orElse("");
    }

}
