package com.minecolonies.discordianmc.config;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class TemplatesConfig extends BaseConfig
{

    @Setting(value = "discord-minecraft-join-placeholder", comment = "\nPlaceholder for minecraft player join message. (this message is going to discord)")
    public String discordPlayerJoin = "**{player-name} joined server {server-name}**";

    @Setting(value = "discord-minecraft-leave-placeholder", comment = "\nPlaceholder for minecraft player leave message. (this message is going to discord)")
    public String discordPlayerLeave = "**{player-name} left server {server-name}**";

    @Setting(value = "discord-minecraft-start-placeholder", comment = "\nPlaceholder for minecraft start message. (this message is going to discord)")
    public String discordServerStart = "**{server-name} Started up!**";

    @Setting(value = "discord-minecraft-stop-placeholder", comment = "\nPlaceholder for minecraft stop message. (this message is going to discord)")
    public String discordServerStop = "**{server-name} Shut down!**";

    @Setting(value = "discord-minecraft-message-placeholder", comment = "\nPlaceholder for a minecaft chat message. (this message is going to discord)")
    public String discordChatMessage = "[{server-name}] **{player-name}**: {player-message}";

    @Setting(value = "any-minecraft-join-placeholder", comment = "\nPlaceholder for minecraft player join message. (this message is coming from another minecraft server)")
    public String anyPlayerJoin = "[{server-name}] {player-name} joined the game!";

    @Setting(value = "any-minecraft-leave-placeholder", comment = "\nPlaceholder for minecraft player leave message. (this message is coming from another minecraft server)")
    public String anyPlayerLeave = "[{server-name}] {player-name} left the game!";

    @Setting(value = "any-minecraft-start-placeholder", comment = "\nPlaceholder for minecraft start message. (this message is coming from another minecraft server)")
    public String anyServerStart = "[{server-name}] Started up!";

    @Setting(value = "any-minecraft-stop-placeholder", comment = "\nPlaceholder for minecraft stop message. (this message is coming from another minecraft server)")
    public String anyServerStop = "[{server-name}] Shut down!";

    @Setting(value = "any-minecraft-message-placeholder", comment = "\nPlaceholder for a minecaft chat message. (this message is coming from another minecraft server)")
    public String anyChatMessage = "[{server-name}] {player-name}: {player-message}";

    @Setting(value = "from-discord-message-placeholder", comment = "\nPlaceholder for a minecraft chat message.")
    public String discordMessage = "[getDiscordPlayerLeave] {player-name}: {player-message}";
}
