package com.minecolonies.chatchainmc.core.commands;

public interface IMenu
{

    String getMenuItemName();

    IMenuType getMenuType();

    void setMenuType(IMenuType menuType);
}
