package org.example;

import org.example.model.Conversation;
import org.example.model.Credentials;
import org.example.model.User;
import org.example.network.ClientConnectionHandler;
import org.example.service.*;
import org.example.ui.*;
import org.example.utils.DateTimeHandler;

import java.io.Console;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class Main {

    private static ReportService reportService = new ReportService();
    private static UserService userService = new UserService();
    private static ConversationService conversationService = new ConversationService();
    private static ColoredTerminal terminal = new ColoredTerminal();
    private static final String SYSTEM = terminal.colored(" SYSTEM ", COLOR.BLUE_BG);
    private static final String SERVER = terminal.colored(" SERVER ", COLOR.GREEN_BG);
    private static final String ERROR = terminal.colored(" ERROR ", COLOR.RED_BG);

    public static void main(String[] args) throws IOException, InterruptedException {

        // define all needed local variables
        String command, activeUsername, serverResponse;
        Socket socket;
        User currentUser;
        ClientConnectionHandler client;
        ColoredTerminal terminal = new ColoredTerminal();
        Console console = System.console();
        ChatService chatService = new ChatService();
        CredentialsService credentialsService = new CredentialsService();

        // display application banner
        // Banner.printMainBanner();

        print(SYSTEM, "Try to connect to the server @ " + Settings.HOST + ":" + Settings.PORT);

        while (true) {
            try {
                socket = new Socket(Settings.HOST, Settings.PORT);
                client = new ClientConnectionHandler(socket);
                if (socket.isConnected())
                    print(SYSTEM, "Connection established successfully");

                while (socket.isConnected()) {
                    // start the lobby server mode
                    // client who is connected and not logged in
                    command = console.readLine("lubby@Mini-Discord>> ");

                    // handling the help command
                    if (command.equals("/help")) {
                        Menu.printHelpMenu();
                    }

                    // handling login process
                    else if (command.equals("/login")) {
                        HashMap<String, Long> chatWords = new HashMap<>();
                        Login loginUI = new Login();
                        Credentials credentials = loginUI.takeUserCredentials("/back");
                        if (credentials.getUsername() != null && credentials.getPassword() != null) {
                            // send auth flag to server with the gathered credentials
                            client.sendMessage("[AUTH]", credentialsService.serialize(credentials));
                            // get the server response and parse it
                            serverResponse = client.getServerResponse();
                            // response[0], response[1] = flag, response body if exist
                            String[] response = serverResponse.split(">");
                            String userJson = response[1];
                            String errorMessage = userJson;
                            if (serverResponse.startsWith("[AUTHORIZED]")) {
                                print(SYSTEM, "Authenticated Successfully.");
                                // load user account to be available to dump or adding new messages and words
                                // frequencies
                                currentUser = userService.deSerialize(userJson);
                                // currentUser
                                // get current username for terminal name.
                                activeUsername = currentUser.getUsername();
                                print(SYSTEM, "User profile is loaded successfully");

                                // start the active user mode
                                while (true) {
                                    // personalize the terminal message prompt.
                                    command = console.readLine(String.format("%s@Mini-Discord>> ", activeUsername));

                                    if (command.equals("/help")) {
                                        Menu.printActiveUserHelpMenu();
                                    }

                                    // dump user profile as json
                                    else if (command.equals("/dump")) {
                                        String dumpsPath = String.format("D:/mini_discord/dumps/%s.json",
                                                activeUsername);
                                        reportService.dumpUser(currentUser, dumpsPath);
                                    }

                                    else if (command.equals("/logout")) {
                                        // remove user profile
                                        currentUser = new User();
                                        activeUsername = null;
                                        break;
                                    }

                                    // handle joining chat room
                                    else if (command.equals("/join")) {
                                        // inform the server to broadcast all members of our joining.
                                        client.sendMessage("[JOIN]", activeUsername);

                                        if (client.getServerResponse().equals("[ACCEPTED]")) {
                                            // start message listener to get message and print it.
                                            client.startMessageListener(activeUsername).start();
                                            print(SYSTEM, "You can have fun with friends now :)");
                                            String message;
                                            while (true) {
                                                // read messages from user
                                                message = console.readLine();

                                                // validate the empty messages
                                                while (message.equals("") || message.replaceAll(" ", "").equals("")) {
                                                    print(ERROR, "Empty messages are not allowed -_-");
                                                    message = console.readLine();
                                                }

                                                // print entered message in formatted way.
                                                printUserMessage(message);

                                                // handle the leaving and snapshotting.
                                                if (message.equals("/leave")
                                                        || message.toLowerCase().equals("bye bye")) {
                                                    client.sendMessage("[LEAVE]", activeUsername);
                                                    client.sendMessage("[SNAPSHOT]", activeUsername);
                                                    print(SYSTEM, "Conversation snapshot requested.");

                                                    String conversationJson = client.getServerResponse();
                                                    Conversation conversation = conversationService
                                                            .deSerialize(conversationJson);

                                                    // create folder for each user and dump into it.
                                                    String dumpsPath = String.format("D:/mini_discord/dumps/%s",
                                                            activeUsername);
                                                    new File(dumpsPath).mkdirs();
                                                    String dumpTempWordsPath = dumpsPath + "/words-temp.txt";
                                                    String dumpTempConversationsPath = dumpsPath
                                                            + "/conversation-temp.txt";
                                                    String dumpTotWordsPath = dumpsPath + "/words-total.txt";
                                                    String dumpTotConversationsPath = dumpsPath
                                                            + "/conversation-total.txt";

                                                    // dump the current chat and words into file.
                                                    reportService.dumpUserChat(conversation.getMessages(),
                                                            dumpTempConversationsPath);
                                                    reportService.dumpWords(chatWords, dumpTempWordsPath);

                                                    // update the current user
                                                    currentUser.setWords(
                                                            userService.accumulateUserWords(chatWords, currentUser));
                                                    ArrayList<String> res = userService.accumulateUserConversation(
                                                            conversation.getMessages(), currentUser);
                                                    currentUser.setConversations(res);

                                                    // dump the total chat and words info file
                                                    reportService.dumpUserChat(currentUser.getConversations(),
                                                            dumpTotConversationsPath);
                                                    reportService.dumpWords(currentUser.getWords(), dumpTotWordsPath);

                                                    // tell the server that we need to update the user account with new
                                                    // data.
                                                    client.sendMessage("[UPDATE]", userService.serialize(currentUser));
                                                    break;
                                                }

                                                // broadcast message to all users.
                                                else {
                                                    // count words in message before sending it
                                                    chatWords = chatService.messageCounter(chatWords, message);
                                                    // send message to server.
                                                    client.sendMessage("[BROADCAST]", activeUsername + "~" + message);
                                                }

                                            }
                                        } else {
                                            print(ERROR, "An error occurred, please try again");
                                        }

                                    }

                                    else if (command.equals("/visual")) {
                                        reportService.printColoredVisualisedReport(currentUser.getWords(),
                                                activeUsername);
                                    }

                                    else {
                                        print(ERROR, "Not supported command");
                                    }
                                }
                            } else {
                                print(SERVER, errorMessage);
                            }
                        }
                    }

                    else if (command.equals("/exit")) {
                        System.exit(0);
                    }

                    else if (command.equals("/reset")) {
                        ResetPassword resetPasswordUI = new ResetPassword();
                        Credentials newCredentials = resetPasswordUI.resetPassword("/back", client);
                        if (newCredentials.getUsername() != null && newCredentials.getPassword() != null) {
                            // perform the update action here
                            // the server will take care of password hashing at its end
                            String credentialsJson = credentialsService.serialize(newCredentials);
                            client.sendMessage("[RESET]", credentialsJson);
                            print(SERVER, client.getServerResponse());
                        }
                    }

                    // handle signup command
                    else if (command.equals("/signup")) {
                        Signup signupUI = new Signup();
                        // start signup screen and specify /end to exit from the signup process
                        User user = signupUI.takeUserData("/end", client);

                        // if the user enter /end it will return new empty user object
                        // so here I check for it.
                        if (user.getUsername() != null && user.getPassword() != null) {
                            // we have valid user object
                            // serialize user object to json string to be sent to server.
                            String userJson = userService.serialize(user);
                            client.sendMessage("[SIGNUP]", userJson);
                            // check for server response
                            serverResponse = client.getServerResponse();
                            print(SERVER, serverResponse);
                        }
                    }

                    // handle not supported commands
                    else {
                        print(ERROR, "Not supported command");
                    }
                }
            } catch (SocketException socketException) {
                print(ERROR,
                        terminal.colored(String.format("CONNECTION REFUSED: Server is down right now"), COLOR.PURPLE));
                print(ERROR,
                        terminal.colored(String.format("Your client will be terminated in 3 seconds"), COLOR.PURPLE));
                TimeUnit.SECONDS.sleep(3);
                System.exit(0);
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void print(String source, String message) {
        System.out.println(String.format("[%s] %s", source, message));
    }

    public static void printUserMessage(String message) {
        {
            // get user input with cursor movement.
            // take input then remove what entered then print them formatted as I want.
            System.out.print("\033[1A"); // Move cursor up one line
            System.out.print("\033[K"); // Clear the line
            message = terminal.colored(String.format("[%s] You: %s", DateTimeHandler.timeNow(), message), COLOR.BLUE);
            System.out.println(message);
        }
    }

}