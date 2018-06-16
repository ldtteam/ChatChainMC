package com.minecolonies.chatchainmc.core.commands;

import com.minecolonies.chatchainmc.core.commands.general.ReconnectCommand;
import com.minecolonies.chatchainmc.core.commands.general.ReloadCommand;
import org.jetbrains.annotations.NotNull;

//PMD.AvoidDuplicateLiterals: We want to have literals used instead of constants as we are defining commands
//and do not necessarily want one command's syntax dependent on another command
//PMD.ExcessiveImports: This class DOES have a high degree of coupling by design.
@SuppressWarnings({"PMD.AvoidDuplicateLiterals", "PMD.ExcessiveImports"})
public enum ActionMenuType implements IMenuType
{
    RELOAD(new ActionMenu(
      "Reload Command",
      "reload",
      ForgePermissionNodes.RELOAD,
      ReloadCommand.class
    )),
    RECONNECT(new ActionMenu(
      "Reconnect Command",
      "reconnect",
      ForgePermissionNodes.RECONNECT,
      ReconnectCommand.class
    ));

    @NotNull
    private final ActionMenu menu;

    ActionMenuType(@NotNull final ActionMenu menu)
    {
        this.menu = menu;
        this.menu.setMenuType(this);
    }

    public boolean isNavigationMenu()
    {
        return false;
    }

    public ActionMenu getMenu()
    {
        return menu;
    }
}
