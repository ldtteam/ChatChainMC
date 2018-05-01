package com.minecolonies.discordianmc.commands;

import org.jetbrains.annotations.NotNull;

public enum NavigationMenuType implements IMenuType
{
    TEST_COMMANDS(new NavigationMenu("tests",
            ActionMenuType.TEST2
    )),

    MINECOLONIES(new NavigationMenu("discordianMC",
            NavigationMenuType.TEST_COMMANDS,
            ActionMenuType.TEST
    ))

    ;

    @NotNull
    private final NavigationMenu menu;

    NavigationMenuType(@NotNull final NavigationMenu menu)
    {
        this.menu = menu;
        this.menu.setMenuType(this);
    }

    public boolean isNavigationMenu()
    {
        return true;
    }

    public NavigationMenu getMenu()
    {
        return menu;
    }
}
