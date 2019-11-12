package co.chatchain.mc.forge.capabilities;

import co.chatchain.commons.core.entities.Group;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;
import java.util.List;
import java.util.concurrent.Callable;

public interface IGroupSettings
{

    void addIgnoredGroup(final Group group);

    void removeIgnoredGroup(final Group group);

    List<Group> getIgnoredGroups();

    void setTalkingGroup(final Group group);

    Group getTalkingGroup();

    class Storage implements Capability.IStorage<IGroupSettings>
    {
        @Nullable
        @Override
        public INBT writeNBT(final Capability<IGroupSettings> capability, final IGroupSettings instance, final Direction side)
        {
            return null;
        }

        @Override
        public void readNBT(final Capability<IGroupSettings> capability, final IGroupSettings instance, final Direction side, final INBT nbt)
        {

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
