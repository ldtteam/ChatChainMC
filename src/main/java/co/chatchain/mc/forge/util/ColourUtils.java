package co.chatchain.mc.forge.util;

import lombok.Getter;
import net.minecraft.util.text.TextFormatting;

import java.awt.*;


public class ColourUtils
{

    private enum Colour
    {
        DARK_RED(new Color(170, 0, 0), TextFormatting.DARK_RED),
        RED(new Color(255, 85, 85), TextFormatting.RED),
        GOLD(new Color(255, 170, 0), TextFormatting.GOLD),
        YELLOW(new Color(255, 255, 85), TextFormatting.YELLOW),
        DARK_GREEN(new Color(0, 170, 0), TextFormatting.DARK_GREEN),
        GREEN(new Color(85, 255, 85), TextFormatting.GREEN),
        AQUA(new Color(85, 255, 255), TextFormatting.AQUA),
        DARK_AQUA(new Color(0, 170, 170), TextFormatting.DARK_AQUA),
        DARK_BLUE(new Color(0, 0, 170), TextFormatting.DARK_BLUE),
        BLUE(new Color(85, 85, 255), TextFormatting.BLUE),
        LIGHT_PURPLE(new Color(255, 85, 255), TextFormatting.LIGHT_PURPLE),
        DARK_PURPLE(new Color(170, 0, 170), TextFormatting.DARK_PURPLE),
        WHITE(new Color(255, 255, 255), TextFormatting.WHITE),
        GRAY(new Color(170, 170, 170), TextFormatting.GRAY),
        DARK_GRAY(new Color(85, 85, 85), TextFormatting.DARK_GRAY),
        BLACK(new Color(0, 0, 0), TextFormatting.BLACK);

        @Getter
        private final Color colour;

        @Getter
        private final TextFormatting textFormatting;

        private Colour(final Color colour, final TextFormatting textFormatting)
        {
            this.colour = colour;
            this.textFormatting = textFormatting;
        }
    }

    private static Color convertHexToColour(final String hexColour)
    {
        return new Color(
                Integer.valueOf(hexColour.substring(1, 3), 16),
                Integer.valueOf(hexColour.substring(3, 5), 16),
                Integer.valueOf(hexColour.substring(5, 7), 16)
        );
    }

    private static double colourDistance(final Color colour1, final Color colour2)
    {
        final double redSqrd = Math.pow(colour1.getRed() -colour2.getRed(), 2);
        final double greenSqrd = Math.pow(colour1.getGreen() - colour2.getGreen(), 2);
        final double blueSqrd = Math.pow(colour1.getBlue() - colour2.getBlue(), 2);
        return Math.sqrt(redSqrd + greenSqrd + blueSqrd);
    }

    public static TextFormatting getTextFormatFromHexColour(final String hexColour)
    {
        final Color colour = convertHexToColour(hexColour);

        Colour currentClosestColour = Colour.DARK_RED;
        double currentClosestDouble = -1D;

        for (final Colour currentColour : Colour.values())
        {
            final double currentDouble = colourDistance(colour, currentColour.getColour());

            if (currentClosestDouble == -1D || currentDouble < currentClosestDouble)
            {
                currentClosestDouble = currentDouble;
                currentClosestColour = currentColour;
            }
        }

        return currentClosestColour.getTextFormatting();
    }

}
