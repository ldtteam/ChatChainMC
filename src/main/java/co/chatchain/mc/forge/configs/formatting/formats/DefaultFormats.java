package co.chatchain.mc.forge.configs.formatting.formats;

import lombok.Getter;
import ninja.leaping.configurate.objectmapping.Setting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/***
 * Exactly the same as {@link MessageFormats} except it has default values
 */
public class DefaultFormats extends MessageFormats
{
    @Getter
    @Setting("generic")
    private List<String> genericMessage = new ArrayList<String>()
    {
        {
            add("§f[§c{group-name}§f] ");
            add("§f[§6{client-name}§f] ");
            add("§f<§e{client-user-name}§f>: {message}");
        }
    };

    @Getter
    @Setting("client-event")
    private Map<String, List<String>> clientEventMessages = new HashMap<String, List<String>>()
    {
        {
            put("START", new ArrayList<String>()
            {
                {
                    add("§f[§c{group-name}§f] ");
                    add("§6{client-name}§a has §aconnected");
                }
            });

            put("STOP", new ArrayList<String>()
            {
                {
                    add("§f[§c{group-name}§f] ");
                    add("§6{client-name}§a has §cdisconnected");
                }
            });
        }
    };

    @Getter
    @Setting("user-event")
    private Map<String, List<String>> userEventMessages = new HashMap<String, List<String>>()
    {
        {
            put("LOGIN", new ArrayList<String>()
            {
                {
                    add("§f[§c{group-name}§f] ");
                    add("[§6{client-name}§f] ");
                    add("§e{client-user-name} has §clogged out§f");
                }
            });
            put("LOGOUT", new ArrayList<String>()
            {
                {
                    add("§f[§c{group-name}§f] ");
                    add("[§6{client-name}§f] ");
                    add("§e{client-user-name} has §alogged in§f");
                }
            });
            put("DEATH", new ArrayList<String>()
            {
                {
                    add("§f[§c{group-name}§f] ");
                    add("[§6{client-name}§f] ");
                    add("§e{client-user-name} has §8died§f");
                }
            });
        }
    };
}
