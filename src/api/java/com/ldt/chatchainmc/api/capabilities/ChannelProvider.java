package com.ldt.chatchainmc.api.capabilities;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ChannelProvider implements ICapabilitySerializable<NBTBase>
{
    @CapabilityInject(IChannelStorage.class)
    public static Capability<IChannelStorage> CHANNEL_STORAGE_CAP;

    private IChannelStorage instance = CHANNEL_STORAGE_CAP.getDefaultInstance();

    @Override
    public boolean hasCapability(
      @Nonnull final Capability<?> capability, @Nullable final EnumFacing facing)
    {
        return capability == CHANNEL_STORAGE_CAP;
    }

    @Nullable
    @Override
    public <T> T getCapability(@Nonnull final Capability<T> capability, @Nullable final EnumFacing facing)
    {
        return capability == CHANNEL_STORAGE_CAP ? CHANNEL_STORAGE_CAP.cast(this.instance) : null;
    }

    @Override
    public NBTBase serializeNBT()
    {
        return CHANNEL_STORAGE_CAP.getStorage().writeNBT(CHANNEL_STORAGE_CAP, this.instance, null);
    }

    @Override
    public void deserializeNBT(final NBTBase nbt)
    {
        CHANNEL_STORAGE_CAP.getStorage().readNBT(CHANNEL_STORAGE_CAP, this.instance, null, nbt);
    }
}
