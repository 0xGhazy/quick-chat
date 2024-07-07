package org.example.database;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.model.User;
import org.example.service.UserService;
import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;


public class SingletonDatabaseAPI {

    private UserService service = new UserService();
    private static SingletonDatabaseAPI single_instance = null;
    private Connection connection = null;
    private static final Logger logger = LogManager.getLogger(SingletonDatabaseAPI.class);

    public static synchronized SingletonDatabaseAPI getInstance() {
        if (single_instance == null)
            single_instance = new SingletonDatabaseAPI();
        return single_instance;
    }

    // restricted to this class itself
    private SingletonDatabaseAPI() {}

    public void connect(String url, String user, String password) throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        connection = DriverManager.getConnection(url, user, password);
    }

    public String insertUser(User user) throws SQLException {
        String insertQuery = "INSERT INTO users (username, password, secQ, secA, words, conversations) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
            // Set values for parameters
            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, user.getPassword());
            preparedStatement.setString(3, user.getSecQ());
            preparedStatement.setString(4, user.getSecA());
            byte[] serializedWords = serializeObject(user.getWords());
            preparedStatement.setBytes(5, serializedWords);
            byte[] serializedConversations = serializeObject(user.getConversations());
            preparedStatement.setBytes(6, serializedConversations);
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
//                logger.logThis("info", String.format("{%s} user account is created", user.getUsername()));
            } else {
//                logger.logThis("error", String.format("Failed to create user", user));
                return "[ERROR] Create account failed!";
            }
        }
        catch (IOException ex)
        {
//            logger.logThis("error", String.format("Exception in create new account [%s]", ex.getMessage()));
        }
        return "Your account has been created successfully!";
    }

    public Boolean validateUsername(String username) {
        try (Statement statement = connection.createStatement()) {
            // Execute a query
            String query = "SELECT * FROM users WHERE username = '" + username + "';";
            try (ResultSet resultSet = statement.executeQuery(query)) {
                int counter = 0;
                while (resultSet.next()) {counter += 1;}
                if(counter > 0)
                    return false;
            }
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return true;
    }

    public User authenticateUser(String username, String hashedPassword) throws SQLException {
        User user = new User();
        try (Statement statement = connection.createStatement()) {
            String query = "SELECT * FROM users WHERE username = ? AND password = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, hashedPassword);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        user.setUsername(resultSet.getString("username"));
                        user.setPassword(resultSet.getString("password"));
                        user.setSecQ(resultSet.getString("secQ"));
                        user.setSecA(resultSet.getString("secA"));
                        //read words
                        byte[] serializedWords = resultSet.getBytes("words");
                        byte[] serializedConversations = resultSet.getBytes("conversations");
                        HashMap<String, Long> words = (HashMap<String, Long>) deserializeObject(serializedWords);
                        ArrayList<String> conversations = deserializeArray(serializedConversations);
                        user.setWords(words);
                        user.setConversations(conversations);
                        return user;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
            catch (SQLException throwable) {
                throwable.printStackTrace();
            }
        }
        return user;
    }

    public String updateUserConversationAndWords(User user) throws SQLException {
        String insertQuery = "UPDATE users SET words = ?, conversations = ? WHERE username = ? ";
        try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
            // Set values for parameters
            byte[] serializedWords = serializeObject(user.getWords());
            byte[] serializedConversations = serializeObject(user.getConversations());
            preparedStatement.setBytes(1, serializedWords);
            preparedStatement.setBytes(2, serializedConversations);
            preparedStatement.setString(3, user.getUsername());
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
//                logger.logThis("info", String.format("{%s} user account dumbed in database successfully", user.getUsername()));
            } else {
//                logger.logThis("error", String.format("Failed to update user", user));
                return "[ERROR] dump account in database failed!";
            }
        }
        catch (IOException ex)
        {
//            logger.logThis("error", String.format("Exception in create new account [%s]", ex.getMessage()));
        }
        return "[SUCCESS] Your account has been created successfully!";
    }

    public String updatePassword(String username, String password) {

        String insertQuery = "UPDATE users SET password = ? WHERE username = ? ";
        try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
            // Set values for parameters
            preparedStatement.setString(1, password);
            preparedStatement.setString(2, username);
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
//                logger.logThis("info", "{" + username + "} account password updated successfully");
                return "Password updated successfully for " + username;
            } else {
//                logger.logThis("error", "Failed to update {" + username + "} password");
                return "Failed to updated password for " + username;
            }
        }
        catch (SQLException ex) {
//            logger.logThis("error", String.format("Error message: " + ex.getMessage()));
        }
        return "";
    }

    public User loadUserByUsername(String username) throws SQLException {
        User user = new User();
        try (Statement statement = connection.createStatement()) {
            String query = "SELECT * FROM users WHERE username = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, username);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        user.setUsername(resultSet.getString("username"));
                        user.setPassword(resultSet.getString("password"));
                        user.setSecQ(resultSet.getString("secQ"));
                        user.setSecA(resultSet.getString("secA"));
                        //read words
                        byte[] serializedWords = resultSet.getBytes("words");
                        byte[] serializedConversations = resultSet.getBytes("conversations");
                        HashMap<String, Long> words = (HashMap<String, Long>) deserializeObject(serializedWords);
                        ArrayList<String> conversations = deserializeArray(serializedConversations);
                        user.setWords(words);
                        user.setConversations(conversations);
                        return user;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
            catch (SQLException throwable) {
                throwable.printStackTrace();
            }
        }
        return user;
    }


    // helper methods
    private static byte[] serializeObject(Object object) throws IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream out = new ObjectOutputStream(bos)) {
            out.writeObject(object);
            return bos.toByteArray();
        }
    }
    private static Object deserializeObject(byte[] data) throws IOException, ClassNotFoundException {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(data);
             ObjectInputStream in = new ObjectInputStream(bis)) {
            return in.readObject();
        }
    }
    private static ArrayList<String> deserializeArray(byte[] data) throws IOException, ClassNotFoundException {
        try (
                // Create a byte array input stream
                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
                // Create an object input stream
                ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream)
        ) {
            return (ArrayList<String>) objectInputStream.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
