package co.chatchain.mc.forge.configs;

import co.chatchain.commons.interfaces.IConnectionConfig;
import co.chatchain.mc.forge.util.Log;
import lombok.Getter;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.net.MalformedURLException;
import java.net.URL;

@ConfigSerializable
public class MainConfig extends AbstractConfig implements IConnectionConfig
{

    @Getter
    @Setting(value = "client-name-if-offline")
    private String clientNameIfOffline = "MC";

    @Getter
    @Setting(value = "clickable-group-mute-or-talk")
    private String clickableGroupMuteOrTalk = "talk";

    //####### Client ID #######\\

    @Setting(value = "client-id", comment = "ChatChainIdentity User ID for this client")
    private String clientId = "ClientId";

    @Override
    public String getClientId()
    {
        return getSystemValueOrConfigValue("CHATCHAIN_CLIENT_ID", clientId);
    }

    //####### Client Secret #######\\

    @Setting(value = "client-secret", comment = "ChatChainIdentity User password for this client")
    private String clientSecret = "ClientSecret";

    @Override
    public String getClientSecret()
    {
        return getSystemValueOrConfigValue("CHATCHAIN_CLIENT_SECRET", clientSecret);
    }

    //####### Api URL #######\\

    @Setting(value = "api-url", comment = "API URL Your client is connecting to")
    private String apiUrl = "https://api.chatchain.co/hubs/chatchain";

    @Override
    public URL getHubUrl()
    {
        try
        {
            return new URL(getSystemValueOrConfigValue("CHATCHAIN_API_URL", apiUrl));
        }
        catch (MalformedURLException e)
        {
            Log.getLogger().error("ChatChain Hub URL is malformed!", e);
        }
        return null;
    }

    //####### Identity URL #######\\

    @Setting(value = "identity-url", comment = "Identity URL Your client is authenticating against")
    private String identityUrl = "https://identity.chatchain.co/connect/token";

    @Override
    public URL getIdentityUrl()
    {
        try
        {
            return new URL(getSystemValueOrConfigValue("CHATCHAIN_IDENTITY_URL", identityUrl));
        }
        catch (MalformedURLException e)
        {
            Log.getLogger().error("ChatChain Identity URL is malformed!", e);
        }
        return null;
    }

    //####### Use Advanced Formatting #######\\

    @Getter
    @Setting("advanced-formatting")
    private Boolean advancedFormatting = false;

    private String getSystemValueOrConfigValue(final String systemValue, String configValue)
    {
        final String value = System.getenv(systemValue);
        if (value == null || value.equals(""))
        {
            return configValue;
        }
        return value;
    }

}
