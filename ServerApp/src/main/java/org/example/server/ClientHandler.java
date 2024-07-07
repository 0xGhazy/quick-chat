package org.example.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.Settings;
import org.example.database.SingletonDatabaseAPI;
import org.example.model.Conversation;
import org.example.model.Credentials;
import org.example.model.User;
import org.example.service.ConversationService;
import org.example.service.CredentialsService;
import org.example.service.HashingService;
import org.example.service.UserService;
import org.example.utils.DateTimeHandler;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.ArrayList;


class ClientHandler implements Runnable {

    private Socket socket;
    private String username;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private final UserService userService = new UserService();
    private final ConversationService conversationService = new ConversationService();
    private final CredentialsService credentialsService = new CredentialsService();
    private static Conversation conversation = new Conversation();
    private static ArrayList<ClientHandler> clientsList = new ArrayList<>();
    private final SingletonDatabaseAPI databaseAPI = SingletonDatabaseAPI.getInstance();
    private static final Logger logger = LogManager.getLogger(ClientHandler.class);

    public ClientHandler(Socket socket, String username) {
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.username = username;
            clientsList.add(this);
        } catch (IOException ioException) {
            logger.error("msg: I/O exception happened, exception:{}", ioException.toString());
        }
    }

    @Override
    public void run() {
        try {
            while (socket.isConnected()) {
                // reading commands from the client side.
                String command = bufferedReader.readLine();
                String[] commandArray = command.split(Settings.DELIMITER);
                String flag = commandArray[0];
                String payload = commandArray[1];

                logger.debug("flag: {}, payload: {}, msg: request captured", flag, payload);

                if (flag.equals("[SIGNUP]")) {
                    User user = userService.deSerialize(payload);
                    user.setPassword(HashingService.getSha256(user.getPassword()));
                    user.setSecA(HashingService.getSha256(user.getSecA()));
                    user.setConversations( new ArrayList<>());
                    String result = databaseAPI.insertUser(user);
                    sendMessage(this, result);
                }
                else if(flag.equals("[LOAD]")) {
                    User user = databaseAPI.loadUserByUsername(payload);
                    if (user.getUsername() != null) {
                        String userJson = userService.serialize(user);
                        sendMessage(this, "[VALID]" + Settings.DELIMITER + userJson);
//                        logger.logThis("replay", "Replay with valid username with the user object");
                    } else {
                        sendMessage(this, "[INVALID] Invalid username");
//                        logger.logThis("replay", "Replay with invalid username");
                    }
                }

                else if(flag.equals("[RESET]")) {
                    Credentials credentials = credentialsService.deSerialize(payload);
                    String username = credentials.getUsername();
                    String password = credentials.getPassword();
                    // hashing the new password
                    password = HashingService.getSha256(password);
                    String result = databaseAPI.updatePassword(username, password);
                    sendMessage(this, result);
                }

                else if(flag.equals("[VALIDATE]")) {
                    if(databaseAPI.validateUsername(payload))
                    {
                        sendMessage(this, "[VALID] username is valid.");
//                        logger.logThis("replay", "Replay with {" + payload +"} is valid username");
                    }
                    else{
                        sendMessage(this, "[INVALID] username is invalid.");
//                        logger.logThis("replay", "Replay with {" + payload +"} is invalid username");
                    }
                }

                else if(flag.equals("[UPDATE]")) {
                    User user = userService.deSerialize(payload);
                    String result = databaseAPI.updateUserConversationAndWords(user);
                    System.out.println(result);
                }

                else if(flag.equals("[SNAPSHOT]")) {
                    Conversation conversationCopy = new Conversation();
                    conversationCopy.setMessages(conversation.getMessages());
                    conversationCopy.setUsername(payload);
                    conversationCopy.setStartTime(conversation.getStartTime());
                    conversationCopy.setEndTime(DateTimeHandler.timeNow());
                    String conversationJson = conversationService.serialize(conversationCopy);
//                    logger.logThis("info", "Conversation messages serialized successfully");
                    sendMessage(this, conversationJson);
                    sendMessage(this, conversationJson);
//                    logger.logThis("replay", "Replay with the snapshot");
                }

                else if(flag.equals("[AUTH]")) {
                    // hash password then pass to database
                    Credentials credentials = credentialsService.deSerialize(payload);
                    String username = credentials.getUsername();
                    String password = HashingService.getSha256(credentials.getPassword());
                    User resultUser = databaseAPI.authenticateUser(username, password);
//                    logger.logThis("request", "Credentials authentication request is captured");
                    if(resultUser.getUsername() != null && resultUser.getPassword() != null)
                    {
                        this.username = resultUser.getUsername();
                        sendMessage(this, "[AUTHORIZED]>" + userService.serialize(resultUser));
//                        logger.logThis("replay", "Replay with AUTHORIZED");
                    } else {
                        sendMessage(this, "[NOT_AUTHORIZED]>Invalid credentials passed.");
//                        logger.logThis("replay", "Replay with UNAUTHORIZED");
                    }
                }

                else if(flag.equals("[JOIN]")) {
                    sendMessage(this, "[ACCEPTED]");
//                    logger.logThis("replay", "Replay with ACCEPTED");
                    this.username = payload;
                    broadcastMessage(String.format("[MINI DISCORD SERVER] %s has joined the chat", this.username));
//                    logger.logThis("broadcast", "inform all members if the newly joined member");
                }

                else if(flag.equals("[BROADCAST]")) {
                    String[] message = payload.split("~");
                    String messageBody = message[1];
                    String sender = message[0];
                    String finalMessage = String.format("[%s] %s: %s", DateTimeHandler.timeNow(), sender, messageBody);
                    broadcastMessage(finalMessage);
//                    logger.logThis("broadcast", "Message sent by user {" + sender + "} to all members");
                    conversation.pushMessage(finalMessage);
                }

                else if(flag.equals("[LEAVE]")) {
//                    logger.logThis("request", "Leaving chat room request is captured");
//                    broadcastMessage(String.format("["+  terminal.colored("SERVER", COLOR.RED_BG) +"] %s Has left the chat!", username));
//                    logger.logThis("broadcast", "Inform all members that " + username + " has left the chat room");
//                    logger.logThis("info", username + " has left the chat room");
                    leaveChat();
                }

            }
        }
        catch (SocketException socketException)
        {
//            logger.logThis("error", "Lost connection with " + username);
//            logger.logThis("error", "Error message: " + socketException.getMessage());
        }
        catch (IOException | SQLException | NoSuchAlgorithmException e) {
//            logger.logThis("error", "Error message: " + e.getMessage());
        }
    }

    public void sendMessage(ClientHandler client, String message) throws IOException {
        client.bufferedWriter.write(message);
        client.bufferedWriter.newLine();
        client.bufferedWriter.flush();
    }

    public void broadcastMessage(String message) {
        for(ClientHandler clientHandler : clientsList) {
            try
            {
                if(!clientHandler.username.equals(username))
                {
                    sendMessage(clientHandler, message);
                }
            }
            catch (IOException e)
            {
//                logger.logThis("error", "Error message: " + e.getMessage());
            }
        }
    }

    public void leaveChat() {
        clientsList.remove(this);
        username = null;
    }

}
