package com.ldt.chatchainmc.core.config;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.ArrayList;
import java.util.List;

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

    @Setting(value = "only-op-create-channels", comment = "Whether Only ops can create new channels or not")
    public Boolean onlyOPCreateChannels = true;

    @Setting(value = "only-op-add-and-remove-users-from-channels", comment = "Whether Only ops can add users to channels or not")
    public Boolean onlyOPAddUsers = true;

    @Setting(value = "created-channels", comment = "List of channels that have been created")
    public List<String> createdChannels = new ArrayList<>();
}
