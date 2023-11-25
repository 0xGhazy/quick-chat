package org.example.ui;

import org.example.model.Credentials;
import org.example.model.User;
import org.example.network.ClientConnectionHandler;
import org.example.utils.Validator;

import java.io.Console;
import java.io.IOException;

public class Signup {

    private Console console = System.console();
    private ColoredTerminal terminal = new ColoredTerminal();
    private User user = new User();
    private final String ERROR = terminal.colored(" ERROR ", COLOR.RED_BG);
    private String[] securityQuestions = { "What is Your favorite movie name?",
            "What is your mom middle name?",
            "Place holder question 3",
            "Place holder question 4" };

    public User takeUserData(String exitCommand, ClientConnectionHandler client)
            throws IOException, InterruptedException {
        String username, password, secQ = null, secA;
        Integer userChoice;

        // take and validate username
        while (true) {
            username = console.readLine("[+] Username: ");
            while (username.equals("") || username.replaceAll(" ", "").equals("")
                    || Validator.haveWhitespaces(username)) {
                System.out.println(String.format("[%s] %s", ERROR,
                        terminal.colored("Username can't be empty or have whitespaces", COLOR.PURPLE)));
                username = console.readLine("[+] Username: ");
            }
            if (username.equals(exitCommand))
                return user;
            client.sendMessage("[VALIDATE]", username);
            String result = client.getServerResponse();
            if (result.startsWith("[VALID]"))
                break;
            else
                System.out.println(String.format("[%s] %s", ERROR,
                        terminal.colored(String.format("[-] {%s} is already exist.", username), COLOR.PURPLE)));
        }

        password = String.valueOf(console.readPassword("[+] Password: "));
        while (password.equals("") || password.replaceAll(" ", "").equals("")) {
            System.out.println(
                    String.format("[%s] %s", ERROR, terminal.colored("Password can't be empty", COLOR.PURPLE)));
            password = String.valueOf(console.readPassword("[+] Password: "));
        }
        if (password.equals(exitCommand))
            return user;

        try {
            printQuestions();
            secQ = console.readLine("[+] Choose a security question number: ");
            if (secQ.equals(exitCommand))
                return user;
            userChoice = Integer.parseInt(secQ);
            while (userChoice < 1 || userChoice > securityQuestions.length - 1
                    || securityQuestions[userChoice - 1] == null) {
                System.out.println(
                        String.format("[%s] %s", ERROR, terminal.colored("Invalid question number", COLOR.PURPLE)));
                secQ = console.readLine("[+] Choose a security question number: ");
                if (secQ.equals(exitCommand))
                    return user;
                userChoice = Integer.parseInt(secQ);
            }
            secQ = securityQuestions[userChoice - 1];
            securityQuestions[userChoice - 1] = null;
        } catch (Exception e) {
            System.out.println(
                    String.format("[%s] %s", ERROR, terminal.colored("Invalid question number", COLOR.PURPLE)));
        }

        secA = console.readLine("[+] Answer: ");
        while (secA.length() == 0) {
            System.out
                    .println(String.format("[%s] %s", ERROR, terminal.colored("Answer can't be empty", COLOR.PURPLE)));
            secA = console.readLine("[+] Answer: ");
        }

        // return valid user object
        user.setUsername(username);
        user.setPassword(password);
        user.setSecQ(secQ);
        user.setSecA(secA);
        return user;
    }

    private void printQuestions() {
        for (int i = 0; i < securityQuestions.length; i++) {
            if (securityQuestions[i] == null)
                continue;
            else
                System.out.println(String.format("\t[%s] %s", i + 1, securityQuestions[i]));
        }
    }
}
