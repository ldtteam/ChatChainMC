package com.minecolonies.chatchainmc.api.events.connection;

import com.minecolonies.chatchainconnect.api.connection.event.IChatChainConnectEventHandlerBuilder;
import net.minecraftforge.fml.common.eventhandler.Event;

public class ConnectionSetEventHandler extends Event
{

    private final IChatChainConnectEventHandlerBuilder builder;

    public ConnectionSetEventHandler(final IChatChainConnectEventHandlerBuilder builder)
    {
        this.builder = builder;
    }

    public IChatChainConnectEventHandlerBuilder getBuilder()
    {
        return this.builder;
    }

}
