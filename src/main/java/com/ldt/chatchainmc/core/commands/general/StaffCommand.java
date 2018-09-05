package com.ldt.chatchainmc.core.commands.general;

import com.ldt.chatchainmc.api.StaticAPIChannels;
import com.ldt.chatchainmc.core.commands.AbstractSingleCommand;
import com.ldt.chatchainmc.core.ChatChainMC;
import com.ldt.chatchainmc.core.util.APIMesssages;
import com.minecolonies.chatchainconnect.api.connection.ConnectionState;
import com.minecolonies.chatchainconnect.api.objects.User;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class StaffCommand extends AbstractSingleCommand
{
    /**
     * Command description.
     */
    public static final String DESC = "staff";


    public StaffCommand(@NotNull final String... parents)
    {
        super(parents);
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

            APIMesssages.chatMessage(StaticAPIChannels.STAFF, user, message.toString());
        }
    }

    @Override
    public @NotNull List<String> getTabCompletionOptions(
      final @NotNull MinecraftServer server, final @NotNull ICommandSender sender, @NotNull final String[] args, @Nullable final BlockPos pos)
    {
        return Collections.emptyList();
    }

    @Override
    public boolean isUsernameIndex(@NotNull final String[] args, final int index)
    {
        return false;
    }
}
