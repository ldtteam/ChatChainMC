package co.chatchain.mc.forge.capabilities;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class GroupProvider implements ICapabilityProvider
{
    @CapabilityInject(IGroupSettings.class)
    public static Capability<IGroupSettings> GROUP_SETTINGS_CAP;

    private IGroupSettings instance = GROUP_SETTINGS_CAP.getDefaultInstance();

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing)
    {
        return capability == GROUP_SETTINGS_CAP;
    }

    @Nullable
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing)
    {
        return capability == GROUP_SETTINGS_CAP ? GROUP_SETTINGS_CAP.cast(this.instance) : null;
    }
}
