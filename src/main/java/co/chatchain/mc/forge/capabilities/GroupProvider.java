package co.chatchain.mc.forge.capabilities;

import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class GroupProvider implements ICapabilityProvider
{
    @CapabilityInject(IGroupSettings.class)
    public static Capability<IGroupSettings> GROUP_SETTINGS_CAP;
    private static final LazyOptional<IGroupSettings> holder = LazyOptional.of(GroupSettings::new);

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull final Capability<T> capability, @Nullable final Direction side)
    {
        return capability == GROUP_SETTINGS_CAP ? holder.cast() : LazyOptional.empty();
    }
}
