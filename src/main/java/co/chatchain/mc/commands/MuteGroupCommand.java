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

            String groupId = null;

            for (final String id : ChatChainMC.instance.getGroupsConfig().getGroupStorage().keySet())
            {
                final Group group = ChatChainMC.instance.getGroupsConfig().getGroupStorage().get(id);

                if (group.getGroupName().replace(" ", "").equalsIgnoreCase(args[0]))
                {
                    groupId = id;
                }
            }

            final EntityPlayerMP player = (EntityPlayerMP) sender;

            if (!ChatChainMC.instance.getGroupsConfig().getGroupStorage().containsKey(groupId) ||
                    (!ChatChainMC.instance.getGroupsConfig().getGroupStorage().get(groupId).getAllowedPlayers().contains(player.getUniqueID()) && !ChatChainMC.instance.getGroupsConfig().getGroupStorage().get(groupId).isAllowAllPlayers()))
            {
                sender.sendMessage(new TextComponentString("This group is invalid!"));
                return;
            }

            if (!ChatChainMC.instance.getGroupsConfig().getGroupStorage().get(groupId).isGroupMutable())
            {
                sender.sendMessage(new TextComponentString("This group is unmutable!"));
                return;
            }

            final IGroupSettings groupSettings = player.getCapability(GroupProvider.GROUP_SETTINGS_CAP, null);

            if (groupSettings != null)
            {
                if (groupSettings.getMutedGroups().contains(groupId))
                {
                    groupSettings.removeMutedGroup(groupId);
                    sender.sendMessage(new TextComponentString("Group unmuted"));
                }
                else
                {
                    groupSettings.addMutedGroup(groupId);
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
                groupNames.add(group.getGroupName().replace(" ", ""));
            }
        }

        groupNames.sort(null);
        return getListOfStringsMatchingLastWord(args, groupNames);
    }
}
