package co.chatchain.mc.forge.compatibility.sponge;

import co.chatchain.commons.objects.ClientRank;
import co.chatchain.mc.forge.ChatChainMC;
import co.chatchain.mc.forge.util.ColourUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.service.permission.SubjectReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Plugin(
        id = ChatChainMC.MOD_ID + "plugin",
        name = ChatChainMC.MOD_NAME + "Plugin",
        version = ChatChainMC.VERSION,
        description = "Sponge Companion plugin for ChatChainMC Mod",
        url = "https://github.com/ldtteam/ChatChainMC")
public class ChatChainSpongePlugin
{

    private static final Pattern COLOUR_CODE_PATTERN = Pattern.compile("§.");

    public ChatChainSpongePlugin()
    {
        ChatChainMC.instance.setSpongeIsPresent(true);
    }

    public static List<ClientRank> getPlayerRanks(final EntityPlayer player)
    {
        final List<ClientRank> returnList = new ArrayList<>();

        final Player spongePlayer = (Player) player;

        int priority = 0;

        String lastDisplay = "";

        for (SubjectReference parent : spongePlayer.getParents())
        {
            parent.getSubjectIdentifier();

            String prefix = spongePlayer.getOption("prefix").orElse(null);
            prefix = prefix == null ? null : prefix.replaceAll("&", "§");

            String hexColour = null;

            if (prefix != null)
            {
                final Matcher matcher = COLOUR_CODE_PATTERN.matcher(prefix);

                if (matcher.find())
                    hexColour = ColourUtils.Colour.getFromColourCode(matcher.group().split("")[1]).getHexCode();
            }

            prefix = prefix == null ? null : prefix.replaceAll("§.", "");

            if (prefix == null || prefix.equals(lastDisplay))
                returnList.add(new ClientRank(parent.getSubjectIdentifier(), parent.getSubjectIdentifier(), priority, null, null));
            else
                returnList.add(new ClientRank(parent.getSubjectIdentifier(), parent.getSubjectIdentifier(), priority, prefix, hexColour));

            lastDisplay = prefix;

            priority++;
        }

        return returnList;
    }

    public static String getPlayerColour(final EntityPlayer player)
    {
        final Player spongePlayer = (Player) player;

        String prefix = spongePlayer.getOption("prefix").orElse(null);
        prefix = prefix == null ? null : prefix.replaceAll("&", "§");

        String hexColour = null;

        if (prefix != null)
        {
            final Matcher matcher = COLOUR_CODE_PATTERN.matcher(prefix);

            if (matcher.find())
                hexColour = ColourUtils.Colour.getFromColourCode(matcher.group().split("")[1]).getHexCode();
        }

        return hexColour;
    }


}
