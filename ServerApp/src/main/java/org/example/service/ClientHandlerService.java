package org.example.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.exception.InvalidPasswordLength;
import org.example.exception.InvalidUserEntity;
import org.example.model.*;
import org.example.service.validation.UserValidation;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;


public class ClientHandlerService {


    private final UserService userService;
    private final UserValidation userValidation = new UserValidation();
    private final CredentialsService credentialsService = new CredentialsService();
    private static final Logger logger = LogManager.getLogger(ClientHandlerService.class);


    public ClientHandlerService(UserService userService) {
        this.userService = userService;
    }


    public CommandResponse signup(Command command) {
        User user;
        String payload = command.getPayload();
        Optional<User> optionalUser = userService.jsonToObject(payload);

        if (optionalUser.isPresent()) {
            User result = null;
            user = optionalUser.get();
            logger.debug("msg: user json mapped successfully to User model, username: {}", user.getUsername());

            // validate received payload
            try {
                user.setPassword(userValidation.validateUserPassword(user.getPassword()));
            }
            catch (InvalidPasswordLength invalidPasswordLength) {
                return CommandResponse
                        .builder()
                        .status(CommandStatus.FAILED)
                        .message(invalidPasswordLength.getMessage())
                        .build();
            }

            try {
                user.setSecA(userValidation.validateUserSecurityAnswer(user.getSecA()));
            }
            catch (InvalidUserEntity invalidUserEntity) {
                return CommandResponse
                        .builder()
                        .status(CommandStatus.FAILED)
                        .message(invalidUserEntity.getMessage())
                        .build();
            }

            // Hashing sensitive data before storing it in database
            user.setPassword(HashingService.getSha256(user.getPassword()));
            user.setSecA(HashingService.getSha256(user.getSecA()));

            user.setConversations(new ArrayList<>());

            try {
                if (userService.insert(user).isPresent())
                    result = userService.insert(user).get();
            } catch (SQLException | IOException exception) {
                return CommandResponse
                        .builder()
                        .status(CommandStatus.FAILED)
                        .message(exception.getMessage())
                        .build();
            }

            return CommandResponse
                        .builder()
                        .status(CommandStatus.SUCCESS)
                        .message("User registered successfully")
                        .payload(userService.jsonSerialize(result))
                        .build();

        } else {
            logger.error("msg: error while mapping user json to User model, payload: {}", payload);
            return CommandResponse
                    .builder()
                    .status(CommandStatus.FAILED)
                    .message("Invalid user json provided")
                    .build();
        }
    }

    public CommandResponse login(Command command) {
        String payload = command.getPayload();
        Credentials credentials = credentialsService.deSerialize(payload);

        // hash password to search for matches in database
        String username = credentials.getUsername();
        String password = HashingService.getSha256(credentials.getPassword());

        try {
            Optional<User> resultUser = userService.loadUserByUsernameAndPassword(username, password);
            logger.trace("msg: user [{}] loaded from database successfully", username);

            if (resultUser.isPresent()) {
                User result = resultUser.get();
                String serializedUser = userService.jsonSerialize(result);
                logger.trace("msg: user [{}] entity serialized to json successfully", username);

                 return CommandResponse
                            .builder()
                            .status(CommandStatus.SUCCESS)
                            .message(String.format("User.username [%s] authenticated successfully", username))
                            .payload(serializedUser)
                            .metadata(result.getUsername())
                            .build();
            } else {
                    return CommandResponse
                            .builder()
                            .status(CommandStatus.FAILED)
                            .message(String.format("Failed to authenticate User.username [%s] ", username))
                            .build();
            }
        }
        catch (SQLException | IOException | ClassNotFoundException exception) {
            return CommandResponse
                    .builder()
                    .status(CommandStatus.FAILED)
                    .message(exception.getMessage())
                    .build();
        }
    }


}
