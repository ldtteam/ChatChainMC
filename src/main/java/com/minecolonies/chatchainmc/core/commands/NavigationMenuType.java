package com.minecolonies.chatchainmc.core.commands;

import org.jetbrains.annotations.NotNull;

public enum NavigationMenuType implements IMenuType
{
    TEST_COMMANDS(new NavigationMenu("tests",
            ActionMenuType.TEST2
    )),

    CHATCHAIN(new NavigationMenu("chatchain",
            NavigationMenuType.TEST_COMMANDS,
            ActionMenuType.TEST,
            ActionMenuType.RELOAD,
            ActionMenuType.RECONNECT
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
