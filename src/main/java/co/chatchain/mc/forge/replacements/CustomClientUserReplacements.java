package co.chatchain.mc.forge.replacements;

import co.chatchain.commons.core.entities.ClientUser;
import co.chatchain.commons.infrastructure.interfaces.replacements.IClientUserReplacements;
import co.chatchain.mc.forge.util.ColourUtils;
import org.jetbrains.annotations.Nullable;

public enum CustomClientUserReplacements
{
    NAME("client-user-name", ClientUser::getName),
    UID("client-user-uid", ClientUser::getUniqueId),
    NICKNAME("client-user-nickname", ClientUser::getNickName),
    COLOUR("client-user-colour", user -> user.getColour() == null ? null : "§" + ColourUtils.getColourFromHexColour(user.getColour()).getColourCode());

    private final String replacement;
    private final UserReplacementAction action;

    CustomClientUserReplacements(final String replacement, final UserReplacementAction action)
    {
        this.replacement = replacement;
        this.action = action;
    }

    @Nullable
    private String GetReplacementObject(final ClientUser user)
    {
        if (user == null)
            return null;

        return this.action.invoke(user);
    }

    private String getReplacement()
    {
        return replacement;
    }

    public UserReplacementAction getAction()
    {
        return action;
    }

    @Nullable
    private static CustomClientUserReplacements GetFromReplacement(final String replacementString)
    {
        for (final CustomClientUserReplacements userReplacement : values())
        {
            if (userReplacement.getReplacement().equalsIgnoreCase(replacementString))
            {
                return userReplacement;
            }
        }
        return null;
    }

    public static class ClientUserReplacementsInstance implements IClientUserReplacements
    {
        @Nullable
        @Override
        public String getReplacementObject(final ClientUser user, final String replacementString)
        {
            final CustomClientUserReplacements userReplacement = GetFromReplacement(replacementString);

            return userReplacement == null ? null : userReplacement.GetReplacementObject(user);
        }
    }

    public interface UserReplacementAction
    {
        String invoke(final ClientUser user);
    }
}
