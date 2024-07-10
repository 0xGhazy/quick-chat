package org.example.repository;

import org.example.database.SingletonDatabaseAPI;
import org.example.model.User;
import org.example.repository.query.UserQuery;
import org.example.utils.Serializer;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

import static org.example.utils.Serializer.serializeObject;

public class UserRepository implements UserRepo {

    private final SingletonDatabaseAPI databaseAPI = SingletonDatabaseAPI.getInstance();

    @Override
    public Optional<User> insertUser(User user) throws SQLException, IOException {
        int result;
        PreparedStatement queryStatement = databaseAPI.setQuery(UserQuery.INSERT_USER);
        queryStatement.setString(1, user.getUsername());
        queryStatement.setString(2, user.getPassword());
        queryStatement.setString(3, user.getSecQ());
        queryStatement.setString(4, user.getSecA());
        byte[] serializedWords = serializeObject(user.getWords());
        queryStatement.setBytes(5, serializedWords);
        byte[] serializedConversations = serializeObject(user.getConversations());
        queryStatement.setBytes(6, serializedConversations);
        result = databaseAPI.executeUpdate(queryStatement);
        if (result != 0)
            return Optional.of(user);
        return Optional.empty();
    }

    @Override
    public Optional<User> findByEmail(String email) throws ClassNotFoundException, SQLException, IOException {
        User user = new User();
        PreparedStatement preparedStatement =  databaseAPI.setQuery(UserQuery.GET_USER_BY_EMAIL);
        preparedStatement.setString(1, email);
        ResultSet resultSet = databaseAPI.executeQuery(preparedStatement);

        if (resultSet.next()) {
            user.setUsername(resultSet.getString("username"));
            user.setSecQ(resultSet.getString("secQ"));
            user.setSecA(resultSet.getString("secA"));

            byte[] serializedWords = resultSet.getBytes("words");
            byte[] serializedConversations = resultSet.getBytes("conversations");

            HashMap<String, Long> words = (HashMap<String, Long>) Serializer.deserializeObject(serializedWords);
            ArrayList<String> conversations = Serializer.deserializeStringArray(serializedConversations);
            user.setWords(words);
            user.setConversations(conversations);
            return Optional.of(user);
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> findById(long id) throws SQLException, IOException, ClassNotFoundException {
        User user = new User();
        PreparedStatement preparedStatement =  databaseAPI.setQuery(UserQuery.GET_USER_BY_ID);
        preparedStatement.setLong(1, id);
        ResultSet resultSet = databaseAPI.executeQuery(preparedStatement);

        if (resultSet.next()) {
            user.setUsername(resultSet.getString("username"));
            user.setSecQ(resultSet.getString("secQ"));
            user.setSecA(resultSet.getString("secA"));

            byte[] serializedWords = resultSet.getBytes("words");
            byte[] serializedConversations = resultSet.getBytes("conversations");

            HashMap<String, Long> words = (HashMap<String, Long>) Serializer.deserializeObject(serializedWords);
            ArrayList<String> conversations = Serializer.deserializeStringArray(serializedConversations);
            user.setWords(words);
            user.setConversations(conversations);
            return Optional.of(user);
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> findByUsernameAndPassword(String username, String password) throws SQLException, IOException, ClassNotFoundException {
        User user = new User();
        PreparedStatement preparedStatement =  databaseAPI.setQuery(UserQuery.AUTHENTICATE_USER);
        preparedStatement.setString(1, username);
        preparedStatement.setString(2, password);
        ResultSet resultSet = databaseAPI.executeQuery(preparedStatement);

        if (resultSet.next()) {
            user.setUsername(resultSet.getString("username"));
            user.setSecQ(resultSet.getString("secQ"));
            user.setSecA(resultSet.getString("secA"));

            byte[] serializedWords = resultSet.getBytes("words");
            byte[] serializedConversations = resultSet.getBytes("conversations");

            HashMap<String, Long> words = (HashMap<String, Long>) Serializer.deserializeObject(serializedWords);
            ArrayList<String> conversations = Serializer.deserializeStringArray(serializedConversations);
            user.setWords(words);
            user.setConversations(conversations);
            return Optional.of(user);
        }

        return Optional.empty();
    }

    @Override
    public Optional<User> updateUserById(Long id, User user) throws SQLException, IOException, ClassNotFoundException {
        Optional<User> oldUser = findById(id);
        if (oldUser.isPresent()) {
            User retrievedUser = oldUser.get();
            retrievedUser.setUsername(user.getUsername());
            retrievedUser.setSecQ(user.getSecQ());
            retrievedUser.setSecA(user.getSecA());
            retrievedUser.setWords(user.getWords());
            retrievedUser.setConversations(user.getConversations());
            return insertUser(retrievedUser);
        }
        return Optional.empty();
    }

}
