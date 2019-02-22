package co.chatchain.mc.commands;

import co.chatchain.mc.ChatChainMC;
import co.chatchain.mc.capabilities.GroupProvider;
import co.chatchain.mc.capabilities.IGroupSettings;
import co.chatchain.mc.message.objects.Group;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MuteGroupCommand extends CommandBase
{

    @NotNull
    @Override
    public String getName()
    {
        return "mute";
    }

    @NotNull
    @Override
    public String getUsage(@NotNull ICommandSender sender)
    {
        return "/chatchain mute <group name>";
    }

    @Override
    public void execute(@NotNull MinecraftServer server, @NotNull ICommandSender sender, @NotNull String[] args)
    {
        if (args.length != 1)
        {
            sender.sendMessage(new TextComponentString("Invalid Arguments"));
            return;
        }

        if (sender instanceof EntityPlayerMP)
        {

            Group group = null;

            for (final String id : ChatChainMC.instance.getGroupsConfig().getGroupStorage().keySet())
            {
                final Group fgroup = ChatChainMC.instance.getGroupsConfig().getGroupStorage().get(id);

                if (fgroup.getCommandName().equalsIgnoreCase(args[0]))
                {
                    group = fgroup;
                }
            }

            final EntityPlayerMP player = (EntityPlayerMP) sender;

            if (group == null || (!group.getAllowedPlayers().contains(player.getUniqueID()) && !group.isAllowAllPlayers()))
            {
                sender.sendMessage(new TextComponentString("This group is invalid!"));
                return;
            }

            if (!group.isGroupMutable())
            {
                sender.sendMessage(new TextComponentString("This group is unmutable!"));
                return;
            }

            final IGroupSettings groupSettings = player.getCapability(GroupProvider.GROUP_SETTINGS_CAP, null);

            if (groupSettings != null)
            {
                if (groupSettings.getMutedGroups().contains(group))
                {
                    groupSettings.removeMutedGroup(group);
                    sender.sendMessage(new TextComponentString("Group unmuted"));
                } else
                {
                    groupSettings.addMutedGroup(group);
                    sender.sendMessage(new TextComponentString("Group muted"));
                }
            }
        }
    }

    @NotNull
    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos)
    {
        if (!(sender instanceof EntityPlayerMP))
        {
            return Collections.emptyList();
        }

        final EntityPlayer player = (EntityPlayer) sender;

        final ArrayList<String> groupNames = new ArrayList<>();

        for (final String groupId : ChatChainMC.instance.getGroupsConfig().getGroupStorage().keySet())
        {
            final Group group = ChatChainMC.instance.getGroupsConfig().getGroupStorage().get(groupId);
            if (group.getAllowedPlayers().contains(player.getUniqueID()))
            {
                groupNames.add(group.getCommandName());
            }
        }

        groupNames.sort(null);
        return getListOfStringsMatchingLastWord(args, groupNames);
    }
}
