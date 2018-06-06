package com.minecolonies.chatchainmc.core.config;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ConfigSerializable
public class ClientConfigs extends BaseConfig
{

    @Setting(value = "client-types", comment = "Put overriding ignore on clientTypes. E.G. ChatChainMC=false will ignore all other MC messages")
    public Map<String, Boolean> clientTypesConfig = new HashMap<>();

    @Setting(value = "clients")
    public Map<String, ClientConfig> clientConfigs = new HashMap<>();

    @ConfigSerializable
    public static class ClientConfig
    {
        @Setting(value = "display", comment = "\nDo i display messages from this client?")
        public Boolean display = true;

        @Setting(value = "channels", comment = "\nlocalChannelName: [channelName]. E.G. \"main\": \"[435017246830755841, 435017246830755917]\" \n "
                                                 + "See client's wiki for list of their channelNames")
        public Map<String, List<String>> channels = new HashMap<>();
    }

}
