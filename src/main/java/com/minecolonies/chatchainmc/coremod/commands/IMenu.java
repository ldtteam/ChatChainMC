package com.minecolonies.chatchainmc.coremod.commands;

public interface IMenu
{

    String getMenuItemName();
    IMenuType getMenuType();
    void setMenuType(IMenuType menuType);

}
