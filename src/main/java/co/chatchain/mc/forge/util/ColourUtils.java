package co.chatchain.mc.forge.util;

import lombok.Getter;
import net.minecraft.util.text.TextFormatting;


public class ColourUtils
{

    public enum Colour
    {
        DARK_RED(new RGBColour(170, 0, 0), "#AA0000", "4", TextFormatting.DARK_RED),
        RED(new RGBColour(255, 85, 85), "#FF5555", "c", TextFormatting.RED),
        GOLD(new RGBColour(255, 170, 0), "#FFAA00", "6", TextFormatting.GOLD),
        YELLOW(new RGBColour(255, 255, 85), "#FFFF55", "e", TextFormatting.YELLOW),
        DARK_GREEN(new RGBColour(0, 170, 0), "#00AA00", "2", TextFormatting.DARK_GREEN),
        GREEN(new RGBColour(85, 255, 85), "#55FF55", "a", TextFormatting.GREEN),
        AQUA(new RGBColour(85, 255, 255), "#55FFFF", "b", TextFormatting.AQUA),
        DARK_AQUA(new RGBColour(0, 170, 170), "#00AAAA", "3", TextFormatting.DARK_AQUA),
        DARK_BLUE(new RGBColour(0, 0, 170), "#0000AA", "1", TextFormatting.DARK_BLUE),
        BLUE(new RGBColour(85, 85, 255), "#5555FF", "9", TextFormatting.BLUE),
        LIGHT_PURPLE(new RGBColour(255, 85, 255), "#FF55FF", "d", TextFormatting.LIGHT_PURPLE),
        DARK_PURPLE(new RGBColour(170, 0, 170), "#AA00AA", "5", TextFormatting.DARK_PURPLE),
        WHITE(new RGBColour(255, 255, 255), "#FFFFFF", "f", TextFormatting.WHITE),
        GRAY(new RGBColour(170, 170, 170), "#AAAAAA", "7", TextFormatting.GRAY),
        DARK_GRAY(new RGBColour(85, 85, 85), "#555555", "8", TextFormatting.DARK_GRAY),
        BLACK(new RGBColour(0, 0, 0), "#000000", "0", TextFormatting.BLACK);

        @Getter
        private final RGBColour colour;

        @Getter
        private final String hexCode;

        @Getter
        private final String colourCode;

        @Getter
        private final TextFormatting textFormatting;

        Colour(final RGBColour colour, final String hexCode, final String colourCode, final TextFormatting textFormatting)
        {
            this.colour = colour;
            this.hexCode = hexCode;
            this.colourCode = colourCode;
            this.textFormatting = textFormatting;
        }

        public static Colour getFromColourCode(final String colourCode)
        {
            for (final Colour colour : values())
            {
                if (colour.getColourCode().equalsIgnoreCase(colourCode))
                    return colour;
            }

            return Colour.WHITE;
        }
    }

    private static RGBColour convertHexToColour(final String hexColour)
    {
        return new RGBColour(
                Integer.valueOf(hexColour.substring(1, 3), 16),
                Integer.valueOf(hexColour.substring(3, 5), 16),
                Integer.valueOf(hexColour.substring(5, 7), 16)
        );
    }

    private static double colourDistance(final RGBColour colour1, final RGBColour colour2)
    {
        final double redSqrt = Math.pow(colour1.getRed() -colour2.getRed(), 2);
        final double greenSqrt = Math.pow(colour1.getGreen() - colour2.getGreen(), 2);
        final double blueSqrt = Math.pow(colour1.getBlue() - colour2.getBlue(), 2);
        return Math.sqrt(redSqrt + greenSqrt + blueSqrt);
    }

    public static Colour getColourFromHexColour(final String hexColour)
    {
        final RGBColour colour = convertHexToColour(hexColour);

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

        return currentClosestColour;
    }

    public static class RGBColour
    {
        private final int red;
        private final int green;
        private final int blue;

        public RGBColour(final int red, final int green, final int blue)
        {
            this.red = red;
            this.green = green;
            this.blue = blue;
        }

        public int getRed()
        {
            return red;
        }

        public int getGreen()
        {
            return green;
        }

        public int getBlue()
        {
            return blue;
        }
    }

}
