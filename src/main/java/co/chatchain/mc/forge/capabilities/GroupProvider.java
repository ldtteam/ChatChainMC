package co.chatchain.mc.forge.capabilities;

import net.minecraft.core.Direction;
import net.minecraft.nbt.Tag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GroupProvider implements ICapabilitySerializable<Tag>
{
    @CapabilityInject(IGroupSettings.class)
    public static Capability<IGroupSettings> GROUP_SETTINGS_CAP = null;

    private final LazyOptional<IGroupSettings> groupSettingsOptional;
    private final IGroupSettings groupSettings;

    public GroupProvider()
    {
        this.groupSettings = new GroupSettings();
        this.groupSettingsOptional = LazyOptional.of(() -> groupSettings);
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull final Capability<T> capability, @Nullable final Direction side)
    {
        return capability == GROUP_SETTINGS_CAP ? groupSettingsOptional.cast() : LazyOptional.empty();
    }

    @Override
    public Tag serializeNBT()
    {
        return IGroupSettings.Storage.writeNBT(GROUP_SETTINGS_CAP, groupSettings, null);
    }

    @Override
    public void deserializeNBT(final Tag nbt)
    {
        IGroupSettings.Storage.readNBT(GROUP_SETTINGS_CAP, groupSettings, null, nbt);
    }
}
