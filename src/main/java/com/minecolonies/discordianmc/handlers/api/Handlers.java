package com.minecolonies.discordianmc.handlers.api;

import com.minecolonies.discordianconnect.api.message.IDiscordianConnectMessage;
import com.minecolonies.discordianmc.DiscordianMC;
import com.minecolonies.discordianmc.util.TemplateMessages;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

public class Handlers
{

    private Handlers() {/* Private constructor to hide implicit */}

    public static void discordMessage(IDiscordianConnectMessage message)
    {
        final String channelID = message.getArguments()[0].getAsString();

        if (DiscordianMC.instance.getMainConfig().mainChannel.equalsIgnoreCase(channelID))
        {
            final ITextComponent text =
              new TextComponentString(TemplateMessages.getGenericAnyDiscordChatMessage(channelID,
                message.getArguments()[1].getAsString(),
                message.getArguments()[2].getAsString()));

            DiscordianMC.instance.getServer().sendMessage(text);
            DiscordianMC.instance.getServer().getPlayerList().getPlayers().forEach(player -> player.sendMessage(text));
        }
    }

    public static void anyMinecraftChatMessage(IDiscordianConnectMessage message)
    {
        final String serverName = message.getArguments()[0].getAsString();
        if (!serverName.equalsIgnoreCase(DiscordianMC.instance.getMainConfig().serverName))
        {
            final ITextComponent text =
              new TextComponentString(TemplateMessages.getAnyChatMessage(message.getArguments()[0].getAsString(),
                message.getArguments()[1].getAsString(),
                message.getArguments()[2].getAsString()));

            DiscordianMC.instance.getLogger().info("anyMinecraftChatMessage: {}", text);
            DiscordianMC.instance.getServer().getPlayerList().getPlayers().forEach(player -> player.sendMessage(text));
        }
    }

    public static void anyMinecraftPlayerJoin(IDiscordianConnectMessage message)
    {
        final String serverName = message.getArguments()[0].getAsString();
        if (!serverName.equalsIgnoreCase(DiscordianMC.instance.getMainConfig().serverName))
        {
            final ITextComponent text =
              new TextComponentString(TemplateMessages.getAnyPlayerJoin(message.getArguments()[0].getAsString(), message.getArguments()[1].getAsString()));

            DiscordianMC.instance.getLogger().info("anyMinecraftPlayerJoin: {}", text);
            DiscordianMC.instance.getServer().getPlayerList().getPlayers().forEach(player -> player.sendMessage(text));
        }
    }

    public static void anyMinecraftPlayerLeave(IDiscordianConnectMessage message)
    {
        final String serverName = message.getArguments()[0].getAsString();
        if (!serverName.equalsIgnoreCase(DiscordianMC.instance.getMainConfig().serverName))
        {
            final ITextComponent text =
              new TextComponentString(TemplateMessages.getAnyPlayerLeave(message.getArguments()[0].getAsString(), message.getArguments()[1].getAsString()));

            DiscordianMC.instance.getLogger().info("anyMinecraftPlayerLeave: {}", text);
            DiscordianMC.instance.getServer().getPlayerList().getPlayers().forEach(player -> player.sendMessage(text));
        }
    }

    public static void anyMinecraftServerStart(IDiscordianConnectMessage message)
    {
        final String serverName = message.getArguments()[0].getAsString();
        if (!serverName.equalsIgnoreCase(DiscordianMC.instance.getMainConfig().serverName))
        {
            final ITextComponent text =
              new TextComponentString(TemplateMessages.getAnyServerStart(message.getArguments()[0].getAsString()));

            DiscordianMC.instance.getLogger().info("anyMinecraftServerStart: {}", text);
            DiscordianMC.instance.getServer().getPlayerList().getPlayers().forEach(player -> player.sendMessage(text));
        }
    }

    public static void anyMinecraftServerStop(IDiscordianConnectMessage message)
    {
        final String serverName = message.getArguments()[0].getAsString();
        if (!serverName.equalsIgnoreCase(DiscordianMC.instance.getMainConfig().serverName))
        {
            final ITextComponent text =
              new TextComponentString(TemplateMessages.getAnyServerStop(message.getArguments()[0].getAsString()));

            DiscordianMC.instance.getLogger().info("anyMinecraftServerStop: {}", text);
            DiscordianMC.instance.getServer().getPlayerList().getPlayers().forEach(player -> player.sendMessage(text));
        }
    }
}
