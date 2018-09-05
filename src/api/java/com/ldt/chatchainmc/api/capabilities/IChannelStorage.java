package com.ldt.chatchainmc.api.capabilities;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.List;
import java.util.concurrent.Callable;

public interface IChannelStorage
{
    void addLChannel(final String channel);
    void removeLChannel(final String channel);

    void addChannel(final String channel);
    void removeChannel(final String channel);

    void setTalkingChannel(final String channel);

    List<String> getChannels();
    List<String> getListeningChannels();
    String getTalkingChannel();


    class Storage implements Capability.IStorage<IChannelStorage>
    {

        /// ---- NBT CONSTANTS ---- \\\

        private static final String NBT_TAG_CHANNELS = "channels";

        /// ---- NBT CONSTANTS ---- \\\

        @Nullable
        @Override
        public NBTBase writeNBT(final Capability<IChannelStorage> capability, final IChannelStorage instance, final EnumFacing side)
        {
            final NBTTagCompound compound = new NBTTagCompound();

            final NBTTagList channelsList = new NBTTagList();

            for (final String channel : instance.getChannels())
            {
                channelsList.appendTag(new NBTTagString(channel));
            }

            compound.setTag(NBT_TAG_CHANNELS, channelsList);

            return compound;
        }

        @Override
        public void readNBT(final Capability<IChannelStorage> capability, final IChannelStorage instance, final EnumFacing side, final NBTBase nbt)
        {
            final NBTTagList channelsList = ((NBTTagCompound) nbt).getTagList(NBT_TAG_CHANNELS, Constants.NBT.TAG_STRING);

            for (int i = 0; i < channelsList.tagCount(); ++i)
            {
                instance.addChannel(channelsList.getStringTagAt(i));
            }
        }
    }

    class Factory implements Callable<IChannelStorage>
    {

        @Nullable
        @Override
        public IChannelStorage call()
        {
            return new ChannelStorage();
        }
    }

}
