package co.chatchain.mc.forge.replacements;

import co.chatchain.commons.core.entities.ClientRank;
import co.chatchain.commons.infrastructure.interfaces.replacements.IClientRankReplacements;
import co.chatchain.mc.forge.util.ColourUtils;
import org.jetbrains.annotations.Nullable;

public enum CustomClientRankReplacements
{
    NAME("client-rank-name", ClientRank::getName),
    UID("client-rank-uid", ClientRank::getUniqueId),
    PRIORITY("client-rank-priority", rank -> String.valueOf(rank.getPriority())),
    DISPLAY("client-rank-display", ClientRank::getDisplay),
    COLOUR("client-rank-colour", rank -> rank.getColour() == null ? null : "§" + ColourUtils.getColourFromHexColour(rank.getColour()).getColourCode());

    private final String replacement;
    private final ClientRankReplacementAction action;

    CustomClientRankReplacements(final String replacement, final ClientRankReplacementAction action)
    {
        this.replacement = replacement;
        this.action = action;
    }

    @Nullable
    private String GetReplacementObject(final ClientRank rank)
    {
        if (rank == null)
            return null;

        return this.action.invoke(rank);
    }

    private String getReplacement()
    {
        return replacement;
    }

    public ClientRankReplacementAction getAction()
    {
        return action;
    }

    @Nullable
    private static CustomClientRankReplacements GetFromReplacement(final String replacementString)
    {
        for (final CustomClientRankReplacements rankReplacement : values())
        {
            if (rankReplacement.getReplacement().equalsIgnoreCase(replacementString))
            {
                return rankReplacement;
            }
        }
        return null;
    }

    public static class ClientRankReplacementsInstance implements IClientRankReplacements
    {
        @Nullable
        @Override
        public String getReplacementObject(final ClientRank rank, final String replacementString)
        {
            final CustomClientRankReplacements rankReplacement = GetFromReplacement(replacementString);

            return rankReplacement == null ? null : rankReplacement.GetReplacementObject(rank);
        }
    }

    public interface ClientRankReplacementAction
    {
        String invoke(final ClientRank rank);
    }
}
