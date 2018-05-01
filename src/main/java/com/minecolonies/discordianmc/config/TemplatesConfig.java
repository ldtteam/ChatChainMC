package com.minecolonies.discordianmc.config;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class TemplatesConfig extends BaseConfig
{
    @Setting(value = "generic-connection-placeholder", comment = "Placeholder for when a new client connects.")
    public String genericConnection = "{client-name} of {client-type} connected!";

    @Setting(value = "generic-disconnection-placeholder", comment = "Placeholder for when a new client disconnects.")
    public String genericDisconnection = "{client-name} of {client-type} disconnected!";

    @Setting(value = "generic-message-placeholder", comment = "Placeholder for generic messages sent over connection.")
    public String genericMessage = "[{client-name}] {user-name}: {user-message}";

    @Setting(value = "generic-join-placeholder", comment = "Placeholder for a generic user join.")
    public String genericJoin = "[{client-name}] {user-name} joined!";

    @Setting(value = "generic-leave-placeholder", comment = "Placeholder for a generic user leave.")
    public String genericLeave = "[{client-name}] {user-name} left!";
}
