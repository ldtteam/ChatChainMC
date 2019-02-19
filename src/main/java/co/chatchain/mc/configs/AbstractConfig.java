package co.chatchain.mc.configs;

import co.chatchain.mc.ChatChainMC;
import com.google.common.reflect.TypeToken;
import lombok.Getter;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;

import java.io.IOException;

public abstract class AbstractConfig<T extends ConfigurationLoader<K>, K extends ConfigurationNode>
{

    private T loader;
    @Getter
    private K node;
    private TypeToken<AbstractConfig> token;

    public void init(T loader, K node, TypeToken<AbstractConfig> token)
    {
        this.loader = loader;
        this.node = node;
        this.token = token;
    }

    public void save()
    {
        try
        {
            this.node.setValue(this.token, this);
            this.loader.save(this.node);
        }
        catch (IOException | ObjectMappingException e)
        {
            ChatChainMC.instance.getLogger().error("Error saving config", e);
        }
    }

    public void load()
    {
        try
        {
            this.node = this.loader.load();
        }
        catch (IOException e)
        {
            ChatChainMC.instance.getLogger().error("Error loading config", e);
        }
    }


}
