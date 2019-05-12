package co.chatchain.mc.forge.commands;

import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.FMLCommonHandler;
import org.jetbrains.annotations.NotNull;

public class UUIDCommand extends CommandBase
{
    @NotNull
    @Override
    public String getName()
    {
        return "uuid";
    }

    @NotNull
    @Override
    public String getUsage(@NotNull ICommandSender sender)
    {
        return "/chatchain uuid <username>";
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender)
    {
        return sender.canUseCommand(2, "");
    }

    @Override
    public void execute(@NotNull MinecraftServer server, @NotNull ICommandSender sender, @NotNull String[] args)
    {
        if (args.length != 1)
        {
            sender.sendMessage(new TextComponentString("Invalid Arguments"));
            return;
        }

        final EntityPlayerMP player = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUsername(args[0]);
        if (player != null)
        {
            sender.sendMessage(new TextComponentString("Player UUID: " + player.getUniqueID()));
        }

    }
}
