package co.chatchain.mc.forge.capabilities;

import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GroupProvider implements ICapabilityProvider
{
    @CapabilityInject(IGroupSettings.class)
    public static Capability<IGroupSettings> GROUP_SETTINGS_CAP = null;

    private final LazyOptional<IGroupSettings> instance = LazyOptional.of(GroupSettings::new);

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull final Capability<T> capability, @Nullable final Direction side)
    {
        return GROUP_SETTINGS_CAP.orEmpty(capability, instance);
    }
}
