package com.minecolonies.chatchainmc.core.config;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class MainConfig extends BaseConfig
{

    @Setting(value = "client-name", comment = "The display name for this server, try to keep it unique.")
    public String clientName = "This Servers Name";

    @Setting(value = "api-url", comment = "URL for connecting to ChatChainNetwork")
    public String apiUrl = "http://localhost:5000";

    @Setting(value = "api-hub", comment = "DO NOT TOUCH UNLESS YOU KNOW WHAT YOU'RE DOING \n "
                      + "API hub url. please leave proceeding \"/\"")
    public String apiHub = "/hubs/discordian";

    @Setting(value = "api-token", comment = "Put your ChatChainServer token in here.")
    public String apiToken = "api token here";

}
