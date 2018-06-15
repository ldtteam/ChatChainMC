package com.minecolonies.chatchainmc.core.config;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.ArrayList;

@ConfigSerializable
public class MainConfig extends BaseConfig
{

    @Setting(value = "client-name", comment = "The display name for this server, try to keep it unique.")
    public String clientName = "This Servers Name";

    @Setting(value = "api-url", comment = "URL for connecting to ChatChainNetwork")
    public String apiUrl = "http://localhost:5000";

    @Setting(value = "api-hub", comment = "DO NOT TOUCH UNLESS YOU KNOW WHAT YOU'RE DOING \n "
                                            + "API hub url. please have proceeding \"/\"")
    public String apiHub = "/hubs/chatchain";

    @Setting(value = "api-token", comment = "Put your ChatChainServer token in here.")
    public String apiToken = "api token here";

    @Setting(value = "main-channel-name")
    public String mainChannel = "main";

    @Setting(value = "staff-channel-name")
    public String staffChannel = "staff";

    @Setting(value = "channels-list")
    public ArrayList<String> channels = new ArrayList<>();
}
