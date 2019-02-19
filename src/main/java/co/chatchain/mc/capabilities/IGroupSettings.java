package co.chatchain.mc.capabilities;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;
import java.util.List;
import java.util.concurrent.Callable;

public interface IGroupSettings
{

    void addMutedGroup(final String group);

    void removeMutedGroup(final String group);

    List<String> getMutedGroups();

    void setTalkingGroup(final String group);

    String getTalkingGroup();

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
