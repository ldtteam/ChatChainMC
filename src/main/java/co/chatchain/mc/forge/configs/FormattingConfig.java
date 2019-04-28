package co.chatchain.mc.forge.configs;

import co.chatchain.commons.messages.objects.Client;
import co.chatchain.commons.messages.objects.Group;
import co.chatchain.commons.messages.objects.messages.ClientEventMessage;
import co.chatchain.commons.messages.objects.messages.GenericMessage;
import co.chatchain.commons.messages.objects.messages.UserEventMessage;
import co.chatchain.mc.forge.ChatChainMC;
import co.chatchain.mc.forge.Constants;
import lombok.Getter;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.event.ClickEvent;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@ConfigSerializable
public class FormattingConfig extends AbstractConfig
{
    public static String escapeMetaCharacters(String inputString){
        final String[] metaCharacters = {"\\","$","{","}","[","]","(",")",".","*","+","?","|","<",">","-","&","%"};

        for (String metaCharacter : metaCharacters)
        {
            if (inputString.contains(metaCharacter))
            {
                inputString = inputString.replace(metaCharacter, "\\" + metaCharacter);
            }
        }
        return inputString;
    }

    private String getDefaultOrOverride(final String groupId, final String defaultString, final Map<String, String> overrideStrings)
    {
        if (overrideStrings.containsKey(groupId))
        {
            return overrideStrings.get(groupId);
        }
        return defaultString;
    }

    private String getReplacements(final Group group, Client client, final String messageToReplace)
    {
        if (client == null)
        {
            client = ChatChainMC.instance.getClient();
        }

        return messageToReplace
                .replaceAll("(\\{group-name})", group.getGroupName())
                .replaceAll("(\\{group-id})", group.getGroupId())
                .replaceAll("(\\{sending-client-name})", client.getClientName())
                .replaceAll("(\\{sending-client-guid})", client.getClientGuid());
    }

    private ITextComponent getTextComponent(final String message, final Group group)
    {

        final GroupConfig groupConfig = ChatChainMC.instance.getGroupsConfig().getGroupStorage().get(group.getGroupId());

        final ITextComponent finalMessage = new TextComponentString("");

        String formattingForNext = "";

        for (final String part : message.split("((?<=\\{clickable-group-name})|(?=\\{clickable-group-name})|(?<=\\{clickable-group-id})|(?=\\{clickable-group-id}))"))
        {
            if (part.contains("{clickable-group-name}") || part.contains("{clickable-group-id}"))
            {
                final String replacement;
                final ClickEvent clickEvent = new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/chatchain " + ChatChainMC.instance.getMainConfig().getClickableGroupMuteOrTalk() + " " + groupConfig.getCommandName());
                if (part.contains("{clickable-group-name}"))
                {
                    replacement = formattingForNext + group.getGroupName();
                }
                else
                {
                    replacement = formattingForNext + group.getGroupId();
                }
                ITextComponent component = new TextComponentString(replacement).setStyle(new Style().setClickEvent(clickEvent));
                finalMessage.appendSibling(component);
            }
            else
            {
                finalMessage.appendSibling(new TextComponentString(formattingForNext + part));
            }

            final Pattern formattingAtEndPattern = Pattern.compile("(§.)*$");
            final Matcher formattingAtEndMatcher = formattingAtEndPattern.matcher(part);

            StringBuilder formatting = new StringBuilder();

            while (formattingAtEndMatcher.find())
            {
                formatting.append(formattingAtEndMatcher.group());
            }

            formattingForNext = formatting.toString();
        }

        return finalMessage;
    }

    @Setting("generic-messages-formats_comment")
    private String genericMessageComment = "Template options: " +
            Constants.GROUP_NAME + " - The messages's group's name " +
            Constants.CLICKABLE_GROUP_NAME + " - Same as above (name), however you can click it to change to change talking to this group" +
            Constants.GROUP_ID + " - The messages's group's ID " +
            Constants.CLICKABLE_GROUP_ID + " - Same as above (id), however you can click it to change to change talking to this group" +
            Constants.USER_NAME + " - Name of the user who sent the messages " +
            Constants.SENDING_CLIENT_NAME + " - Name of the Client who sent the messages " +
            Constants.SENDING_CLIENT_GUID + " - The GUID of the client who sent the messages " +
            Constants.MESSAGE + " - The messages that was sent";

    @Getter
    @Setting("generic-messages-formats")
    private Map<String, String> genericMessageFormats = new HashMap<>();

    @Setting("default-generic-messages-format")
    @Getter
    private String defaultGenericMessageFormat = "§f[§c{group-name}§f] §f[§6{sending-client-name}§f] §f<§e{user-name}§f>: {messages}";

    public ITextComponent getGenericMessage(final GenericMessage message)
    {
        if (ChatChainMC.instance.getGroupsConfig().getGroupStorage().containsKey(message.getGroup().getGroupId()))
        {
            final Group group = ChatChainMC.instance.getGroupsConfig().getGroupStorage().get(message.getGroup().getGroupId()).getGroup();

            final String defaultOrOverride = getDefaultOrOverride(group.getGroupId(), defaultGenericMessageFormat, genericMessageFormats);

            final String stringMessage = getReplacements(group, message.getSendingClient(), defaultOrOverride).replaceAll("(\\{user-name})", message.getUser().getName())
                    .replaceAll("(\\{messages})", escapeMetaCharacters(message.getMessage()));
            return getTextComponent(stringMessage, group);
        }
        return null;
    }

    @Setting("client-event-formats_comment")
    private String clientEventComment = "Template options: " +
            Constants.GROUP_NAME + " - The messages's group's name " +
            Constants.CLICKABLE_GROUP_NAME + " - Same as above (name), however you can click it to change to change talking to this group" +
            Constants.GROUP_ID + " - The messages's group's ID " +
            Constants.CLICKABLE_GROUP_ID + " - Same as above (id), however you can click it to change to change talking to this group" +
            Constants.SENDING_CLIENT_NAME + " - Name of the Client who sent the messages " +
            Constants.SENDING_CLIENT_GUID + " - The GUID of the client who sent the messages ";

    @Getter
    @Setting("client-start-event-formats")
    private Map<String, String> clientStartEventFormats = new HashMap<>();

    @Getter
    @Setting("default-client-start-event-format")
    private String defaultClientStartEventFormats = "§f[§c{group-name}§f] §6{sending-client-name}§a has §aconnected";

    @Getter
    @Setting("client-stop-event-formats")
    private Map<String, String> clientStopEventFormats = new HashMap<>();

    @Getter
    @Setting("default-client-stop-event-format")
    private String defaultClientStopEventFormats = "§f[§c{group-name}§f] §6{sending-client-name}§c has §cdisconnected";

    public ITextComponent getClientEventMessage(final ClientEventMessage message)
    {
        final String defaultOrOverride;
        if (message.getEvent().equalsIgnoreCase("START"))
        {
            defaultOrOverride = getDefaultOrOverride(message.getGroup().getGroupId(), defaultClientStartEventFormats, clientStartEventFormats);
        }
        else if (message.getEvent().equalsIgnoreCase("STOP"))
        {
            defaultOrOverride = getDefaultOrOverride(message.getGroup().getGroupId(), defaultClientStopEventFormats, clientStopEventFormats);
        }
        else
        {
            return null;
        }

        final String stringMessage = getReplacements(message.getGroup(), message.getSendingClient(), defaultOrOverride);
        return getTextComponent(stringMessage, message.getGroup());
    }

    @Setting("user-event-formats_comment")
    private String userEventComment = "Template options: " +
            Constants.GROUP_NAME + " - The messages's group's name " +
            Constants.CLICKABLE_GROUP_NAME + " - Same as above (name), however you can click it to change to change talking to this group" +
            Constants.GROUP_ID + " - The messages's group's ID " +
            Constants.CLICKABLE_GROUP_ID + " - Same as above (id), however you can click it to change to change talking to this group" +
            Constants.USER_NAME + " - Name of the user who sent the messages " +
            Constants.SENDING_CLIENT_NAME + " - Name of the Client who sent the messages " +
            Constants.SENDING_CLIENT_GUID + " - The GUID of the client who sent the messages ";

    @Getter
    @Setting("user-login-event-formats")
    private Map<String, String> userLoginEventFormats = new HashMap<>();

    @Getter
    @Setting("default-user-login-event-format")
    private String defaultUserLoginEventFormats = "§f[§c{group-name}§f] [§6{sending-client-name}§f] §e{user-name} has §alogged in§f";

    @Getter
    @Setting("user-logout-event-formats")
    private Map<String, String> userLogoutEventFormats = new HashMap<>();

    @Getter
    @Setting("default-user-logout-event-format")
    private String defaultUserLogoutEventFormats = "§f[§c{group-name}§f] [§6{sending-client-name}§f] §e{user-name} has §clogged out§f";

    @Getter
    @Setting("user-death-event-formats")
    private Map<String, String> userDeathEventFormats = new HashMap<>();

    @Getter
    @Setting("default-user-death-event-format")
    private String defaultUserDeathEventFormats = "§f[§c{group-name}§f] [§6{sending-client-name}§f] §e{user-name} has §8died§f";

    /*@Getter
    @Setting("user-achievement-event-formats")
    private Map<String, String> userAchievementEventFormats = new HashMap<>();

    @Getter
    @Setting("default-user-achievement-event-format")
    private String defaultUserAchievementEventFormats = "§f[§c{group-name}§f] [§6{sending-client-name}§f] §e{user-name} has gained §2achievement: {achievement-name}";*/

    public ITextComponent getUserEventMessage(final UserEventMessage message)
    {
        final String defaultOrOverride;
        if (message.getEvent().equalsIgnoreCase("LOGIN"))
        {
            defaultOrOverride = getDefaultOrOverride(message.getGroup().getGroupId(), defaultUserLoginEventFormats, userLoginEventFormats);
        }
        else if (message.getEvent().equalsIgnoreCase("LOGOUT"))
        {
            defaultOrOverride = getDefaultOrOverride(message.getGroup().getGroupId(), defaultUserLogoutEventFormats, userLogoutEventFormats);
        }
        else if (message.getEvent().equalsIgnoreCase("DEATH"))
        {
            defaultOrOverride = getDefaultOrOverride(message.getGroup().getGroupId(), defaultUserDeathEventFormats, userDeathEventFormats);
        }
        /*else if (messages.getEvent().equalsIgnoreCase("ACHIEVEMENT"))
        {
            defaultOrOverride = getDefaultOrOverride(group.getGroupId(), defaultUserAchievementEventFormats, userAchievementEventFormats);
        }*/
        else
        {
            return null;
        }

        String stringMessage = getReplacements(message.getGroup(), message.getSendingClient(), defaultOrOverride).replaceAll("(\\{user-name})", message.getUser().getName());

        /*if (messages.getEvent().equalsIgnoreCase("ACHIEVEMENT"))
        {
            for (final String key : messages.getExtraEventData().keySet())
            {
                ChatChainMC.instance.getLogger().info("here: " + key);
                if (key.equalsIgnoreCase("achievement-name"))
                {
                    stringMessage = stringMessage.replaceAll("(\\{achievement-name})", messages.getExtraEventData().get(key));
                }

            }
        }*/

        return getTextComponent(stringMessage, message.getGroup());
    }

}
