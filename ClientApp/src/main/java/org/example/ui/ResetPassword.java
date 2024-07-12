package org.example.ui;

import org.example.Settings;
import org.example.model.Credentials;
import org.example.model.User;
import org.example.network.ClientConnectionHandler;
import org.example.service.HashingService;
import org.example.service.UserService;
import org.example.utils.Validator;

import java.io.Console;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class ResetPassword {


    private Console console = System.console();
    private ColoredTerminal terminal = new ColoredTerminal();
    private UserService userService = new UserService();
    private final String ERROR = terminal.colored(" ERROR ", COLOR.RED_BG);
    private final String SYSTEM = terminal.colored(" SYSTEM ", COLOR.BLUE_BG);
    private User user = null;


    private  Boolean isUserExists(String username, ClientConnectionHandler client) throws IOException, InterruptedException {
        client.sendMessage("[LOAD]", username);
        String result = client.getServerResponse();
        if (result.startsWith("[VALID]"))
        {
            String userJson = result.split(Settings.DELIMITER)[1];
            user = userService.deSerialize(userJson);
            return true;
        }
        return false;
    }

    public Credentials resetPassword(String exitCommand, ClientConnectionHandler client) throws IOException, InterruptedException, NoSuchAlgorithmException {

        String username, tempAnswer, newPassword = null;
        Credentials credentials = new Credentials();
        System.out.println("\n");
        while (true) {
            username = console.readLine("[+] Username: ");
            while (username.equals("") || username.replaceAll(" ", "").equals("")
                    || Validator.haveWhitespaces(username)) {
                print(ERROR, "Username can't be empty or have whitespaces");
                username = console.readLine("[+] Username: ");
            }
            if (username.equals(exitCommand))
                return credentials;
            if(isUserExists(username, client))
            {
                break;
            }
            else {
                print(ERROR, "The passed username is invalid");
            }
        }

        if(user.getSecurityQuestionId() != null)
        {
            print(SYSTEM, user.getSecurityQuestionId());
            tempAnswer = console.readLine("Enter Answer: ");
            while(tempAnswer.equals(""))
            {
                print(ERROR, "answer can't be empty -_-");
                tempAnswer = console.readLine("Enter Answer: ");
            }
            if(tempAnswer.equals(exitCommand))
                return credentials;

            // check for answer correctness
            if(HashingService.getSha256(tempAnswer).equals(user.getSecurityAnswer()))
            {
                // get new password here then
                newPassword = String.valueOf(console.readPassword("Enter new password: "));
                while (newPassword.equals("") || newPassword.replaceAll(" ", "").equals("")) {
                    print(ERROR, "Password can't be empty or all whitespaces");
                    newPassword = String.valueOf(console.readPassword("Enter new password: "));
                }
                if(newPassword.equals(exitCommand))
                    return credentials;
            }
            else
            {
                print(ERROR, "Invalid answer!");
                return credentials;
            }
        }
        credentials.setUsername(username);
        credentials.setPassword(newPassword);
        return credentials;
    }

    public static void print(String source, String message) {
        System.out.println(String.format("[%s] %s", source, message));
    }



}
