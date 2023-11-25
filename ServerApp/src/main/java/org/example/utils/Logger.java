package org.example.utils;

import java.util.HashMap;


public class Logger {

    private HashMap<String, COLOR> events = new HashMap<>();
    private ColoredTerminal terminal = new ColoredTerminal();


    public Logger()
    {
        events.put("INFO", COLOR.GREEN);
        events.put("ERROR", COLOR.RED);
        events.put("BROADCAST", COLOR.CYAN);
        events.put("REQUEST", COLOR.BLUE);
        events.put("REPLAY", COLOR.PURPLE);
    }

    public void logThis(String type, String message)
    {
        type = type.toUpperCase();

        String logMessage = String.format("[%s]   [%-20s]   %s", // + (TIMESTAMP_WIDTH - 21) + "s",
                DateTimeHandler.timeNow(),
                terminal.colored(type, events.get(type)),
                message);
        System.out.println(logMessage);
    }
}
