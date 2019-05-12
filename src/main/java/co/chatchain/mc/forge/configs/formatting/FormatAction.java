package co.chatchain.mc.forge.configs.formatting;

import co.chatchain.mc.forge.configs.formatting.formats.MessageFormats;

import java.util.List;

public interface FormatAction
{
    List<String> invoke(final MessageFormats formats);
}
