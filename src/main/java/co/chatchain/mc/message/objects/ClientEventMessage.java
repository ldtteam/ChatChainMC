package co.chatchain.mc.message.objects;

import lombok.Getter;
import lombok.Setter;

public class ClientEventMessage
{
    @Getter
    @Setter
    private String event;

    @Getter
    private Client sendingClient;

    @Getter
    @Setter
    private boolean sendToSelf;

    private ClientEventMessage(final String event, final boolean sendToSelf)
    {
        this.event = event;
        this.sendToSelf = sendToSelf;
    }

    public ClientEventMessage(final String event)
    {
        this(event, false);
    }
}
