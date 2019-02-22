package co.chatchain.mc.capabilities;

import co.chatchain.mc.message.objects.Group;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;
import java.util.List;
import java.util.concurrent.Callable;

public interface IGroupSettings
{

    void addMutedGroup(final Group group);

    void removeMutedGroup(final Group group);

    List<Group> getMutedGroups();

    void setTalkingGroup(final Group group);

    Group getTalkingGroup();

    class Storage implements Capability.IStorage<IGroupSettings>
    {

        @Nullable
        @Override
        public NBTBase writeNBT(Capability<IGroupSettings> capability, IGroupSettings instance, EnumFacing side)
        {
            return null;
        }

        @Override
        public void readNBT(Capability<IGroupSettings> capability, IGroupSettings instance, EnumFacing side, NBTBase nbt)
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
