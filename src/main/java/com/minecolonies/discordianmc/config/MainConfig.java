package com.minecolonies.discordianmc.config;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.ArrayList;
import java.util.List;

@ConfigSerializable
public class MainConfig extends BaseConfig
{

    @Setting(value = "server-name", comment = "\nThe display name for this server, try to keep it unique.")
    public String serverName = "This Servers Name";

    @Setting(value = "api-url", comment = "\nURL for connecting to DiscordianNetwork")
    public String apiUrl = "http://localhost:5000";

    @Setting(value = "api-hub", comment = "\nDO NOT TOUCH UNLESS YOU KNOW WHAT YOU'RE DOING \n "
                                            + "API hub url. please leave proceeding \"/\"")
    public String apiHub = "/hubs/discordian";

    @Setting(value = "main-channel", comment = "\nPut the discord channel ID of where you want the main chat output to go.")
    public String mainChannel = "channel id here";

    @Setting(value = "api-token", comment = "\nPut your DiscordianServer token in here.")
    public String apiToken = "api token here";

    @Setting(value = "servers-to-display", comment = "Put all the Minecraft server-names (config value) to display messages from here.")
    public List<String> displayServers = new ArrayList<>();
}
