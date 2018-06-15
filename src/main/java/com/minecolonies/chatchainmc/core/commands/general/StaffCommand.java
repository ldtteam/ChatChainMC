package com.minecolonies.chatchainmc.core.commands.general;

import com.minecolonies.chatchainconnect.api.connection.ConnectionState;
import com.minecolonies.chatchainconnect.api.objects.User;
import com.minecolonies.chatchainmc.core.APIChannels;
import com.minecolonies.chatchainmc.core.ChatChainMC;
import com.minecolonies.chatchainmc.core.util.APIMesssages;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;

public class StaffCommand extends CommandBase
{

    @Override
    public String getName()
    {
        return "staff";
    }

    @Override
    public String getUsage(final ICommandSender sender)
    {
        return "staff [message...]";
    }

    @Override
    public void execute(final MinecraftServer server, final ICommandSender sender, final String[] args) throws CommandException
    {
        if (ChatChainMC.instance.getConnection() != null
              && ChatChainMC.instance.getConnection().getConnectionState().equals(ConnectionState.OPEN))
        {
            final User user = new User();
            user.setName(sender.getName());

            if (sender instanceof EntityPlayer)
            {
                user.setAvatarURL("https://crafatar.com/avatars/" + ((EntityPlayer) sender).getUniqueID());
            }
            else
            {
                user.setAvatarURL("https://cdn.discordapp.com/channel-icons/354208766285185027/6fd2f2d04ef1c4a79a970ae96af42e75");
            }

            final StringBuilder message = new StringBuilder();

            for (final String string : args)
            {
                message.append(string);
                message.append(" ");
            }

            APIMesssages.chatMessage(APIChannels.STAFF, user, message.toString());
        }
    }
}
