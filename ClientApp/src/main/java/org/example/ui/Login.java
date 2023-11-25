package org.example.ui;

import org.example.model.Credentials;

import java.io.Console;

public class Login {

    private Credentials credentials = new Credentials();
    private Console console = System.console();
    private ColoredTerminal terminal = new ColoredTerminal();
    private final String ERROR = terminal.colored(" ERROR ", COLOR.RED_BG);


    public Credentials takeUserCredentials(String exitCommand)
    {
        String username, password;
        username = console.readLine("\t[+] Username: ");
        while(username.equals("") || username.replaceAll(" ", "").equals(""))
        {
            System.out.println(String.format("[%s] %s", ERROR, terminal.colored("Username can't be empty", COLOR.PURPLE)));
            username = console.readLine("\t[+] Username: ");
        }
        if(username.equals(exitCommand))
            return credentials;

        password = String.valueOf(console.readPassword("\t[+] Password: "));
        while(password.equals("") || password.replaceAll(" ", "").equals(""))
        {
            System.out.println(String.format("[%s] %s", ERROR, terminal.colored("Password can't be empty", COLOR.PURPLE)));
            password = String.valueOf(console.readPassword("\t[+] Password: "));
        }
        if (password.equals(exitCommand))
            return credentials;

        // return the valid credentials
        credentials.setUsername(username);
        credentials.setPassword(password);
        return credentials;
    }
}
