package org.example.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.model.*;
import org.example.service.*;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;


class ClientHandler implements Runnable {

    private Socket socket;
    private String username;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private final UserService userService = new UserService();
    private final ClientHandlerService clientHandlerService = new ClientHandlerService(userService);
    private static ArrayList<ClientHandler> clientsList = new ArrayList<>();
    private static final Logger logger = LogManager.getLogger(ClientHandler.class);

    public ClientHandler(Socket socket, String username) {
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.username = username;
            clientsList.add(this);
        } catch (IOException ioException) {
            logger.error("msg: I/O exception happened while init ClientHandler instance, exception:{}", ioException.toString());
        }
    }

    @Override
    public void run() {

        try {
            while (socket.isConnected()) {
                CommandResponse commandResponse = null;
                String jsonCommand = bufferedReader.readLine();
                Command command = CommandService.jsonToObject(jsonCommand);
                String flag = command.getFlag().name();

                logger.debug("flag: {}, msg: request captured", flag);

                switch (flag) {
                    case "SIGNUP" ->
                    {
                        commandResponse = clientHandlerService.signup(command);
                        sendMessage(this, CommandResponseService.jsonify(commandResponse));
                        break;
                    }

                    case "AUTHENTICATE" ->
                    {
                        commandResponse = clientHandlerService.login(command);
                        this.username = commandResponse.getMetadata();
                        sendMessage(this, CommandResponseService.jsonify(commandResponse));
                        break;
                    }
                    default ->
                    {
                        logger.error("flag: {}, payload: {}, msg: Unsupported command flag", flag, command.getPayload());
                        break;
                    }
                }
            }
        } catch (IOException ioException) {
            logger.error("Client <{}> disconnected", this.username);
            clientsList.remove(this);
            closeResources();
        }

    }


    public void sendMessage (ClientHandler client, String message) throws IOException {
        client.bufferedWriter.write(message);
        client.bufferedWriter.newLine();
        client.bufferedWriter.flush();
    }

    public void broadcastMessage (String message){
        for (ClientHandler clientHandler : clientsList) {
            try {
                if (!clientHandler.username.equals(username)) {
                    sendMessage(clientHandler, message);
                }
            } catch (IOException e) {
//               logger.logThis("error", "Error message: " + e.getMessage());
            }
        }
    }

    public void leaveChat () {
        clientsList.remove(this);
        username = null;
    }


    private void closeResources() {
        try {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            logger.error("Error closing resources for user '{}': {}", this.username, e.getMessage());
        }
    }

}



            /*

                    case "LOAD" -> {
                    Optional<User> user = userService.findByEmail(command.getPayload());
                    if (user.isPresent() && !user.get().getEmail().isEmpty()) {
                        String userJson = userService.jsonSerialize(user.get());
                        commandResponse = CommandResponse
                                .builder()
                                .status(CommandStatus.SUCCESS)
                                .message("User loaded successfully")
                                .payload(userJson)
                                .build();
                    } else {
                        commandResponse = CommandResponse
                                .builder()
                                .status(CommandStatus.FAILED)
                                .message("Invalid username")
                                .build();
                    }
                    sendMessage(this, CommandResponseService.jsonify(commandResponse));
                }
                case "VALIDATE" -> {
                    Optional<User> user = userService.findByEmail(command.getPayload());
                    if (user.isPresent() && !user.get().getEmail().isEmpty()) {
                        commandResponse = CommandResponse
                                .builder()
                                .status(CommandStatus.SUCCESS)
                                .message("Valid Email address")
                                .build();
                    } else {
                        commandResponse = CommandResponse
                                .builder()
                                .status(CommandStatus.FAILED)
                                .message("Invalid email address")
                                .build();
                    }
                    sendMessage(this, CommandResponseService.jsonify(commandResponse));
                }
                case "UPDATE" -> {
                    User user = userService.jsonToObject(payload);
                    if (user != null && user.getId() != null) {
                        Optional<User> updatedUser = userService.update(user.getId(), user);
                        if (updatedUser.isPresent()) {
                            String userJson = userService.jsonSerialize(updatedUser.get());
                            commandResponse = CommandResponse
                                    .builder()
                                    .status(CommandStatus.SUCCESS)
                                    .message("User entity updated successfully")
                                    .payload(userJson)
                                    .build();
                        } else {
                            commandResponse = CommandResponse
                                    .builder()
                                    .status(CommandStatus.FAILED)
                                    .message("Failed to update User entity")
                                    .build();
                        }
                    }
                    sendMessage(this, CommandResponseService.jsonify(commandResponse));
                }
                case "SNAPSHOT" -> {
                    User user = userService.jsonToObject(payload);
                    Conversation conversationCopy = new Conversation();
                    conversationCopy.setMessages(conversation.getMessages());
                    conversationCopy.setUsername(user.getUsername());
                    conversationCopy.setStartTime(conversation.getStartTime());
                    conversationCopy.setEndTime(DateTimeHandler.timeNow());
                    String conversationJson = conversationService.serialize(conversationCopy);
                    commandResponse = CommandResponse
                            .builder()
                            .status(CommandStatus.SUCCESS)
                            .message("Conversation messages backup generated successfully")
                            .payload(conversationJson)
                            .build();
                    sendMessage(this, CommandResponseService.jsonify(commandResponse));
                }
                case "[AUTH]" -> {
                    Credentials credentials = credentialsService.deSerialize(payload);
                    String username = credentials.getUsername();
                    String password = HashingService.getSha256(credentials.getPassword());
                    Optional<User> resultUser = userService.loadUserByUsernameAndPassword(username, password);

                    if (resultUser.isPresent()) {
                        User user = resultUser.get();
                        String serializedUser = userService.jsonSerialize(user);
                        if (user.getUsername() != null && user.getPassword() != null) {
                            this.username = user.getUsername();
                            commandResponse = CommandResponse
                                    .builder()
                                    .status(CommandStatus.SUCCESS)
                                    .message(String.format("User.username %s authenticated successfully", user.getUsername()))
                                    .payload(serializedUser)
                                    .build();
                        } else {
                            commandResponse = CommandResponse
                                    .builder()
                                    .status(CommandStatus.FAILED)
                                    .message(String.format("Failed to authenticate User.username %s ", user.getUsername()))
                                    .build();
                        }
                    } else {
                        commandResponse = CommandResponse
                                .builder()
                                .status(CommandStatus.FAILED)
                                .message("Failed To authenticate the user")
                                .build();
                    }
                    sendMessage(this, CommandResponseService.jsonify(commandResponse));
                }
                case "[JOIN]" -> {
                    sendMessage(this, "[ACCEPTED]");
                    this.username = payload;
                    broadcastMessage(String.format("[MINI DISCORD SERVER] %s has joined the chat", this.username));
                }
                case "[BROADCAST]" -> {
                    String[] message = payload.split("~");
                    String messageBody = message[1];
                    String sender = message[0];
                    String finalMessage = String.format("[%s] %s: %s", DateTimeHandler.timeNow(), sender, messageBody);
                    broadcastMessage(finalMessage);
                    conversation.pushMessage(finalMessage);
                }
                case "[LEAVE]" ->
//                    logger.logThis("request", "Leaving chat room request is captured");
//                    broadcastMessage(String.format("["+  terminal.colored("SERVER", COLOR.RED_BG) +"] %s Has left the chat!", username));
//                    logger.logThis("broadcast", "Inform all members that " + username + " has left the chat room");
//                    logger.logThis("info", username + " has left the chat room");
                        leaveChat();

            } catch(SQLException e){
                throw new RuntimeException(e);
            } catch(IOException e){
                throw new RuntimeException(e);
            } catch(NoSuchAlgorithmException e){
                throw new RuntimeException(e);
            }
        }
    }
             */