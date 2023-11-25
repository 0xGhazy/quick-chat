package org.example.ui;

import java.util.HashMap;

public class ColoredTerminal {

    private static HashMap<COLOR, String> colors;

    public ColoredTerminal()
    {
        // load colors
        colors = new HashMap<>();
        colors.put(COLOR.BLACK, "\u001B[30m");
        colors.put(COLOR.RED, "\u001B[31m");
        colors.put(COLOR.GREEN, "\u001B[32m");
        colors.put(COLOR.YELLOW, "\u001B[33m");
        colors.put(COLOR.BLUE, "\u001B[34m");
        colors.put(COLOR.PURPLE, "\u001B[35m");
        colors.put(COLOR.CYAN, "\u001B[36m");
        colors.put(COLOR.WHITE, "\u001B[37m");
        colors.put(COLOR.BLACK_BG, "\u001B[40m");
        colors.put(COLOR.RED_BG, "\u001B[41m");
        colors.put(COLOR.GREEN_BG, "\u001B[42m");
        colors.put(COLOR.YELLOW_BG, "\u001B[43m");
        colors.put(COLOR.BLUE_BG, "\u001B[44m");
        colors.put(COLOR.PURPLE_BG, "\u001B[45m");
        colors.put(COLOR.CYAN_BG, "\u001B[46m");
        colors.put(COLOR.WHITE_BG, "\u001B[47m");
    }

    public String colored(String message, COLOR color)
    {
        String usedColor = colors.get(color);
        final String rest = "\u001B[0m";
        return usedColor + message + rest;
    }

    public void print(String message, COLOR color, Boolean hasNewLine)
    {
        String usedColor = colors.get(color);
        final String rest = "\u001B[0m";
        if(hasNewLine)
            System.out.println(usedColor + message + rest);
        System.out.print(message + rest);
    }
}
