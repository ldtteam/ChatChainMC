package com.minecolonies.discordianmc.config;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class MainConfig extends BaseConfig
{

    @Setting(value = "server-name", comment = "The display name for this server, try to keep it unique.")
    public String serverName = "This Servers Name";

    @Setting(value = "api-url", comment = "URL for connecting to DiscordianNetwork")
    public String apiUrl = "http://localhost:5000";

    @Setting(value = "api-hub", comment = "DO NOT TOUCH UNLESS YOU KNOW WHAT YOU'RE DOING \n "
                                            + "API hub url. please leave proceeding \"/\"")
    public String apiHub = "/hubs/discordian";

    @Setting(value = "main-channel", comment = "Put the discord channel ID of where you want the main chat output to go.")
    public String mainChannel = "channel id here";
}
