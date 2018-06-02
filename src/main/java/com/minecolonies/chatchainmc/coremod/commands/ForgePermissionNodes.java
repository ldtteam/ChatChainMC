package com.minecolonies.chatchainmc.coremod.commands;

import net.minecraftforge.server.permission.DefaultPermissionLevel;
import org.jetbrains.annotations.NotNull;

/**
 * Available forge Permission nodes that can be checked for access.
 *
 * See this url until real documentation becomes available.
 * https://github.com/MinecraftForge/MinecraftForge/pull/3155
 *
 */
public enum ForgePermissionNodes
{
    TEST("com.chatchainmc.test", DefaultPermissionLevel.OP, "Can test...."),
    RELOAD("com.chatchainmc.reload", DefaultPermissionLevel.OP, "Reloads Configs...."),
    RECONNECT("com.chatchainmc.reconnect", DefaultPermissionLevel.OP, "Reconnect to API....")
    ;

    @NotNull
    private final String                 nodeName;
    @NotNull
    private final DefaultPermissionLevel defaultPermissionLevel;
    @NotNull
    private final String                 description;

    ForgePermissionNodes(@NotNull final String nodeName, @NotNull final DefaultPermissionLevel defaultPermissionLevel, @NotNull final String description)
    {
        this.nodeName = nodeName;
        this.defaultPermissionLevel = defaultPermissionLevel;
        this.description = description;
    }

    public String getNodeName()
    {
        return nodeName;
    }

    public DefaultPermissionLevel getDefaultPermissionLevel()
    {
        return defaultPermissionLevel;
    }

    public String getDescription()
    {
        return description;
    }
}
