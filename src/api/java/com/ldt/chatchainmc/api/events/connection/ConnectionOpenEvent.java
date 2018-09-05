package com.ldt.chatchainmc.api.events.connection;

import com.minecolonies.chatchainconnect.api.connection.IChatChainConnectConnectionBuilder;
import lombok.Getter;
import net.minecraftforge.fml.common.eventhandler.Event;

public class ConnectionOpenEvent extends Event
{
    @Getter
    private final IChatChainConnectConnectionBuilder builder;

    public ConnectionOpenEvent(final IChatChainConnectConnectionBuilder builder)
    {
        this.builder = builder;
    }
}
