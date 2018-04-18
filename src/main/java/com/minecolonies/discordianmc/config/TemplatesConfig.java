package com.minecolonies.discordianmc.config;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class TemplatesConfig extends BaseConfig
{

    @Setting(value = "minecraft-join-placeholder", comment = "\nPlaceholder for minecraft player join message")
    public String playerJoin = "**{player-name} joined server {server-name}**";

    @Setting(value = "minecraft-leave-placeholder", comment = "\nPlaceholder for minecraft player leave message")
    public String playerLeave = "**{player-name} left server {server-name}**";

    @Setting(value = "minecraft-start-placeholder", comment = "\nPlaceholder for minecraft start message.")
    public String serverStart = "**{server-name} Started up!**";

    @Setting(value = "minecraft-stop-placeholder", comment = "\nPlaceholder for minecraft stop message.")
    public String serverStop = "**{server-name} Shut down!**";

    @Setting(value = "minecraft-message-placeholder", comment = "\nPlaceholder for a minecaft chat message.")
    public String chatMessage = "[{server-name}] **{player-name}**: {player-message}";

    @Setting(value = "discord-message-placeholder", comment = "\nPlaceholder for a minecraft chat message")
    public String discordMessage = "[Discord] {player-name}: {player-message}";
}
