package com.ldt.chatchainmc.core.commands;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractSingleCommand implements ISubCommand
{

    public static final String NOT_PERMITTED = "You are not allowed to do that!";
    private final String[] parents;

    /**
     * Initialize this SubCommand with it's parents.
     *
     * @param parents an array of all the parents.
     */
    public AbstractSingleCommand(@NotNull final String... parents)
    {
        this.parents = parents.clone();
    }

    /**
     * Get the ith argument (An Integer).
     *
     * @param i    the argument from the list you want.
     * @param args the list of arguments.
     * @param def  the default value.
     * @return the argument.
     */
    public static int getIthArgument(final String[] args, final int i, final int def)
    {
        if (args.length <= i)
        {
            return def;
        }

        try
        {
            return Integer.parseInt(args[i]);
        }
        catch (final NumberFormatException e)
        {
            return def;
        }
    }

    @NotNull
    @Override
    public String getCommandUsage(@NotNull final ICommandSender sender)
    {
        final StringBuilder sb = new StringBuilder().append('/');
        for (final String parent : parents)
        {
            sb.append(parent).append(' ');
        }
        return sb.toString();
    }

    /**
     * Will check to see if play is Opped for the given command name.
     *
     * @param sender to check the player using the command.
     * @return boolean
     */
    @NotNull
    public static boolean isPlayerOpped(@NotNull final ICommandSender sender)
    {
        if (sender instanceof EntityPlayer)
        {
            return FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList()
                     .canSendCommands(((EntityPlayer) sender).getGameProfile());
        }
        return true;
    }

    public enum Commands
    {
        CITIZENINFO,
        COLONYTP,
        RTP,
        DELETECOLONY,
        KILLCITIZENS,
        LISTCITIZENS,
        RESPAWNCITIZENS,
        SHOWCOLONYINFO,
        ADDOFFICER,
        CHANGE_COLONY_OWNER,
        REFRESH_COLONY,
        HOMETP,
        MC_BACKUP,
        RSRESET
    }
}
