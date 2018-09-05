package com.ldt.chatchainmc.api.capabilities;

import com.ldt.chatchainmc.api.StaticAPIChannels;

import java.util.ArrayList;
import java.util.List;

public class ChannelStorage implements IChannelStorage
{
    private List<String> channels = new ArrayList<>();
    private List<String> listeningChannels = new ArrayList<>();

    private String talkingChannel = StaticAPIChannels.MAIN;

    @Override
    public void addLChannel(final String channel)
    {
        if (!this.listeningChannels.contains(channel))
        {
            this.listeningChannels.add(channel);
        }
    }

    @Override
    public void removeLChannel(final String channel)
    {
        if (this.listeningChannels.contains(channel))
        {
            this.listeningChannels.remove(channel);
        }
    }

    @Override
    public void setTalkingChannel(final String channel)
    {
        this.talkingChannel = channel;
    }

    @Override
    public void addChannel(final String channel)
    {
        if (!this.channels.contains(channel))
        {
            this.channels.add(channel);
            this.addLChannel(channel);
        }
    }

    @Override
    public void removeChannel(final String channel)
    {
        if (this.channels.contains(channel))
        {
            this.channels.remove(channel);
            this.removeLChannel(channel);
        }
    }

    @Override
    public List<String> getChannels()
    {
        return new ArrayList<>(channels);
    }

    @Override
    public List<String> getListeningChannels()
    {
        return new ArrayList<>(listeningChannels);
    }

    @Override
    public String getTalkingChannel()
    {
        return talkingChannel;
    }
}
