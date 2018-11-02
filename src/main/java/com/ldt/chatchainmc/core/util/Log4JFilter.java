package com.ldt.chatchainmc.core.util;

import com.minecolonies.chatchainconnect.api.objects.User;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.filter.AbstractFilter;

public class Log4JFilter extends AbstractFilter
{
    @Override
    public Result filter(final LogEvent event)
    {
        if (!event.getLoggerName().equalsIgnoreCase("FML"))
        {
            new Thread(() -> sendLogMessage(event.getMessage().getFormattedMessage())).start();
        }
        return super.filter(event);
    }

    private void sendLogMessage(final String message)
    {
        final User user = new User();
        user.setName("Server");
        user.setAvatarURL("https://cdn.discordapp.com/channel-icons/354208766285185027/6fd2f2d04ef1c4a79a970ae96af42e75");

        APIMesssages.chatMessage("log", user, message);
    }
}
