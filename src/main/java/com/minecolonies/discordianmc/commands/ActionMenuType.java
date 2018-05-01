package com.minecolonies.discordianmc.commands;

import com.minecolonies.discordianmc.commands.general.TestCommand;
import org.jetbrains.annotations.NotNull;

//PMD.AvoidDuplicateLiterals: We want to have literals used instead of constants as we are defining commands
//and do not necessarily want one command's syntax dependent on another command
//PMD.ExcessiveImports: This class DOES have a high degree of coupling by design.
@SuppressWarnings({"PMD.AvoidDuplicateLiterals", "PMD.ExcessiveImports"})
public enum ActionMenuType implements IMenuType
{
    TEST2(new ActionMenu(
      "Test Command 2",
      "test2",
      ForgePermissionNodes.TEST,
      TestCommand.class,
      new ActionArgument("player", ActionArgumentType.ONLINE_PLAYER, ActionArgumentType.Is.OPTIONAL)
    )),
    TEST(new ActionMenu(
            "Test Command",
            "test",
            ForgePermissionNodes.TEST,
            TestCommand.class,
            new ActionArgument("player", ActionArgumentType.ONLINE_PLAYER, ActionArgumentType.Is.OPTIONAL)
            ))
    ;

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
