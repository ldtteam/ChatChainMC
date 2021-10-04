package co.chatchain.mc.forge.util;

import co.chatchain.mc.forge.ChatChainMC;
import co.chatchain.mc.forge.configs.GroupConfig;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class CommandUtils
{
    public static CompletableFuture<Suggestions> getAllGroupSuggestions(final CommandContext<CommandSourceStack> context, final SuggestionsBuilder builder)
    {
        final List<String> suggestions = new ArrayList<>();

        for (final String groupId : ChatChainMC.INSTANCE.getGroupsConfig().getGroupStorage().keySet())
        {
            final GroupConfig groupConfig = ChatChainMC.INSTANCE.getGroupsConfig().getGroupStorage().get(groupId);
            suggestions.add(groupConfig.getCommandName());
        }
        suggestions.sort(null);
        return SharedSuggestionProvider.suggest(suggestions, builder);
    }

    public static CompletableFuture<Suggestions> getTalkingGroupSuggestions(final CommandContext<CommandSourceStack> context, final SuggestionsBuilder builder)
    {
        final List<String> suggestions = new ArrayList<>();
        final CommandSourceStack source = context.getSource();

        for (final String groupId : ChatChainMC.INSTANCE.getGroupsConfig().getGroupStorage().keySet())
        {
            final GroupConfig groupConfig = ChatChainMC.INSTANCE.getGroupsConfig().getGroupStorage().get(groupId);
            try
            {
                if (groupConfig.isCanAllowedChat() && groupConfig.getPlayersForGroup().contains(source.getPlayerOrException()))
                {
                    suggestions.add(groupConfig.getCommandName());
                }
            }
            catch (CommandSyntaxException e)
            {
                return Suggestions.empty();
            }
        }
        suggestions.sort(null);
        return SharedSuggestionProvider.suggest(suggestions, builder);
    }

    public static CompletableFuture<Suggestions> getIgnorableGroupSuggestions(final CommandContext<CommandSourceStack> context, final SuggestionsBuilder builder)
    {
        final List<String> suggestions = new ArrayList<>();
        final CommandSourceStack source = context.getSource();

        for (final String groupId : ChatChainMC.INSTANCE.getGroupsConfig().getGroupStorage().keySet())
        {
            final GroupConfig groupConfig = ChatChainMC.INSTANCE.getGroupsConfig().getGroupStorage().get(groupId);
            try
            {
                if (groupConfig.getPlayersForGroup().contains(source.getPlayerOrException()))
                {
                    suggestions.add(groupConfig.getCommandName());
                }
            }
            catch (CommandSyntaxException e)
            {
                return Suggestions.empty();
            }
        }
        suggestions.sort(null);
        return SharedSuggestionProvider.suggest(suggestions, builder);
    }

    public static CompletableFuture<Suggestions> getStatsSections(final CommandContext<CommandSourceStack> context, final SuggestionsBuilder builder)
    {
        return SharedSuggestionProvider.suggest(Arrays.asList("online-users", "performance"), builder);
    }
}
