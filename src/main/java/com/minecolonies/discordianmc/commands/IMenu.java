package com.minecolonies.discordianmc.commands;

public interface IMenu
{

    String getMenuItemName();
    IMenuType getMenuType();
    void setMenuType(IMenuType menuType);

}
