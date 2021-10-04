package co.chatchain.mc.forge.capabilities;

import co.chatchain.commons.core.entities.Group;
import co.chatchain.mc.forge.ChatChainMC;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraftforge.common.capabilities.Capability;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.Callable;

public interface IGroupSettings
{

    void addIgnoredGroup(final Group group);

    void removeIgnoredGroup(final Group group);

    List<Group> getIgnoredGroups();

    void setTalkingGroup(final Group group);

    void setTalkingGroup(final String group);

    Group getTalkingGroup();

    class Storage
    {
        private static final String NBT_TALKING_GROUP = "talking-group";

        public static Tag writeNBT(final Capability<IGroupSettings> capability, final IGroupSettings instance, final Direction side)
        {
            final CompoundTag nbt = new CompoundTag();

            nbt.putString(NBT_TALKING_GROUP, instance.getTalkingGroup().getId());

            return nbt;
        }

        public static void readNBT(final Capability<IGroupSettings> capability, final IGroupSettings instance, final Direction side, final Tag nbt)
        {
            if (nbt instanceof CompoundTag)
            {
                final CompoundTag compound = (CompoundTag) nbt;
                if (compound.contains(NBT_TALKING_GROUP))
                {
                    instance.setTalkingGroup(compound.getString(NBT_TALKING_GROUP));
                }
                else
                {
                    instance.setTalkingGroup(ChatChainMC.INSTANCE.getGroupsConfig().getDefaultGroup());
                }
            }
        }
    }

    class Factory implements Callable<IGroupSettings>
    {
        @Nullable
        @Override
        public IGroupSettings call()
        {
            return new GroupSettings();
        }
    }
}
