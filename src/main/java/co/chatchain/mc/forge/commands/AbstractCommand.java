package co.chatchain.mc.forge.commands;

import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandExceptionType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;

/**
 * Interface for all commands
 */
public abstract class AbstractCommand
{
    /**
     * Builds command's tree.
     *
     * @return new built command
     */
    @SuppressWarnings("unused")
    protected static LiteralArgumentBuilder<CommandSource> build()
    {
        throw new RuntimeException("Missing command builder!");
    }

    /**
     * Creates new sub command, used for sub commands and type picking.
     *
     * @param name subcommand name
     * @return new node builder
     */
    protected static LiteralArgumentBuilder<CommandSource> newLiteral(final String name)
    {
        return LiteralArgumentBuilder.literal(name);
    }

    /**
     * Creates new command argument, used for collection selector, number picker etc.
     *
     * @param <T>  argument class type
     * @param name argument name, aka description/alias, but it's also id key to get argument value from command during execution
     * @param type argument type, see net.minecraft.command.arguments
     * @return new node builder
     */
    protected static <T> RequiredArgumentBuilder<CommandSource, T> newArgument(final String name, final ArgumentType<T> type)
    {
        return RequiredArgumentBuilder.argument(name, type);
    }

    /**
     * Throws command syntax exception.
     *
     * @param key language key to translate
     */
    public static void throwSyntaxException(final String key) throws CommandSyntaxException
    {
        throw new CommandSyntaxException(new ChatChainMCCommandExceptionType(), new LiteralMessage("message"));
    }

    /**
     * Throws command syntax exception.
     *
     * @param key    language key to translate
     * @param format String.format() attributes
     */
    public static void throwSyntaxException(final String key, final Object... format) throws CommandSyntaxException
    {
        throw new CommandSyntaxException(new ChatChainMCCommandExceptionType(), new LiteralMessage("message"));
    }

    /**
     * Our dummy exception type
     */
    @SuppressWarnings("unused")
    protected static class ChatChainMCCommandExceptionType implements CommandExceptionType
    {
    }

    /**
     * Class for building command trees effectively
     */
    protected static class CommandTree
    {
        /**
         * Tree root node
         */
        private final LiteralArgumentBuilder<CommandSource> rootNode;

        /**
         * Creates new command tree.
         *
         * @param commandName root vertex name
         */
        protected CommandTree(final String commandName)
        {
            rootNode = newLiteral(commandName);
        }

        /**
         * Adds new command as leaf into this tree.
         *
         * @param command new command to add
         * @return this
         */
        protected CommandTree addNode(final LiteralArgumentBuilder<CommandSource> command)
        {
            rootNode.then(command.build());
            return this;
        }

        /**
         * Builds whole tree for dispatcher.
         *
         * @return tree as command node
         */
        protected LiteralArgumentBuilder<CommandSource> build()
        {
            return rootNode;
        }
    }
}
