package co.chatchain.mc.forge.util;

import co.chatchain.mc.forge.ChatChainMC;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Log
{

    private static Logger logger = null;

    private Log()
    {

    }

    public static Logger getLogger()
    {
        if (logger == null)
        {
            Log.logger = LogManager.getLogger(ChatChainMC.MOD_ID);
        }
        return logger;
    }

}
