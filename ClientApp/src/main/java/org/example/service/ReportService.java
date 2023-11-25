package org.example.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.example.model.Conversation;
import org.example.model.User;
import org.example.ui.COLOR;
import org.example.ui.ColoredTerminal;
import org.example.utils.DateTimeHandler;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class ReportService {

    private Gson gson = new Gson();
    private ColoredTerminal terminal = new ColoredTerminal();
    private final String SYSTEM = terminal.colored(" SYSTEM ", COLOR.BLUE_BG);
    private final String ERROR = terminal.colored(" ERROR ", COLOR.RED_BG);

    public void dumpWords(HashMap<String, Long> userWords, String filePath) {
        String message = null;
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (Map.Entry<String, Long> map : userWords.entrySet()) {
                writer.write(String.format("%s | %s", map.getKey(), map.getValue()));
                writer.newLine();
            }
            message = terminal.colored(
                    String.format("Your words statistics have ben dumped successfully @ %s", filePath), COLOR.PURPLE);
            System.out.println(String.format("[%s] %s", SYSTEM, message));
        } catch (IOException exception) {
            System.out.println(String.format("[%s] %s", ERROR, exception.getMessage()));
        }
    }

    public void dumpUserChat(ArrayList<String> conversation, String filePath) {
        String message = null;
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (String line : conversation) {
                writer.write(line);
                writer.newLine();
            }
            message = terminal.colored(
                    String.format("Your conversations have ben dumped successfully @ %s", filePath),
                    COLOR.PURPLE);
            System.out.println(String.format("[%s] %s", SYSTEM, message));
        } catch (IOException e) {
            System.out.println(String.format("[%s] %s", ERROR, e.getMessage()));
        }
    }

    public void dumpUser(User user, String filePath) {
        String message;
        try (FileWriter writer = new FileWriter(filePath)) {
            // Create Gson object with pretty printing
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            // Convert object to JSON and write to file
            gson.toJson(user, writer);
            message = terminal.colored(
                    String.format("Your User data have ben dumped successfully @ %s", filePath),
                    COLOR.PURPLE);
            System.out.println(String.format("[%s] %s", SYSTEM, message));
        } catch (IOException e) {
            System.out.println(String.format("[%s] %s", ERROR, e.getMessage()));
        }
    }

    public void printColoredVisualisedReport(HashMap<String, Long> map, String username) {
        long maxValue = 0;
        for (Map.Entry<String, Long> mapEntry : map.entrySet()) {
            maxValue += mapEntry.getValue();
        }
        System.out.println("============================================================");
        System.out.println(String.format("[+] All time Report generated for %s", username));
        System.out.println("============================================================\n");
        System.out.println(
                "\tWord                           Capacity                                            Percentage");
        System.out.println(
                "\t----                           --------                                            ----------");
        for (Map.Entry<String, Long> entry : map.entrySet()) {
            String key = entry.getKey();
            long value = entry.getValue();
            double percentage = (value * 100.0) / maxValue;
            double remainingPercentage = 100 - percentage;
            String percentageToPrint = String.format("%.2f%%", percentage);
            String progress = String.join("", Collections.nCopies((int) (percentage / 2), " "));
            progress = terminal.colored(progress, COLOR.WHITE_BG);
            String empty = String.join("", Collections.nCopies((int) (remainingPercentage / 2), "#"));
            empty = terminal.colored(empty, COLOR.GREEN);
            System.out.println(String.format("\t%-30s [%s%s] (%-6s) [%s/%s]", key, progress, empty, percentageToPrint,
                    value, maxValue));
        }
        System.out.println(String.format("\tTotal Words No: %s Word\n", maxValue));
    }

}
