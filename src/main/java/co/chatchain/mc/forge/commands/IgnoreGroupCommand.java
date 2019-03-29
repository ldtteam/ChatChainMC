package co.chatchain.mc.forge.commands;

import co.chatchain.mc.forge.ChatChainMC;
import co.chatchain.mc.forge.capabilities.GroupProvider;
import co.chatchain.mc.forge.capabilities.IGroupSettings;
import co.chatchain.mc.forge.configs.GroupConfig;
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

public class IgnoreGroupCommand extends CommandBase
{

    @NotNull
    @Override
    public String getName()
    {
        return "ignore";
    }

    @NotNull
    @Override
    public String getUsage(@NotNull ICommandSender sender)
    {
        return "/chatchain ignore <group name>";
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender)
    {
        return true;
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

            GroupConfig groupConfig = null;

            for (final String id : ChatChainMC.instance.getGroupsConfig().getGroupStorage().keySet())
            {
                final GroupConfig fGroupConfig = ChatChainMC.instance.getGroupsConfig().getGroupStorage().get(id);

                if (fGroupConfig.getCommandName().equalsIgnoreCase(args[0]))
                {
                    groupConfig = fGroupConfig;
                }
            }

            final EntityPlayerMP player = (EntityPlayerMP) sender;

            if (groupConfig == null || !groupConfig.getPlayersForGroup().contains(player))
            {
                sender.sendMessage(new TextComponentString("This group is invalid!"));
                return;
            }

            if (!groupConfig.isGroupIgnorable())
            {
                sender.sendMessage(new TextComponentString("This group is ignorable!"));
                return;
            }

            final IGroupSettings groupSettings = player.getCapability(GroupProvider.GROUP_SETTINGS_CAP, null);

            if (groupSettings != null)
            {
                if (groupSettings.getIgnoredGroups().contains(groupConfig.getGroup()))
                {
                    groupSettings.removeIgnoredGroup(groupConfig.getGroup());
                    sender.sendMessage(new TextComponentString("Group ignored"));
                }
                else
                {
                    groupSettings.addIgnoredGroup(groupConfig.getGroup());
                    sender.sendMessage(new TextComponentString("Group un-ignored"));
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
            final GroupConfig groupConfig = ChatChainMC.instance.getGroupsConfig().getGroupStorage().get(groupId);
            if (groupConfig.getPlayersForGroup().contains(player))
            {
                groupNames.add(groupConfig.getCommandName());
            }
        }

        groupNames.sort(null);
        return getListOfStringsMatchingLastWord(args, groupNames);
    }
}
