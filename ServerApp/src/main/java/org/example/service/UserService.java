package org.example.service;

import com.google.gson.Gson;
import org.example.model.User;
import org.example.repository.UserRepository;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;


public class UserService {

    private final Gson gson = new Gson();
    private final UserRepository repository = new UserRepository();


    public Optional<User> insert(User user) {
        try {
            return repository.insertUser(user);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<User> update(Long id, User user) {
        try {
            return repository.updateUserById(id, user);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<User> loadUserByUsernameAndPassword(String username, String password) {
        try {
            return repository.findByUsernameAndPassword(username, password);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<User> findById(Long id) {
        try {
            return repository.findById(id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<User> findById(String email) {
        try {
            return repository.findByEmail(email);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public String jsonSerialize(User user) {
        return gson.toJson(user);
    }

    public User jsonToObject(String userJson) {
        return gson.fromJson(userJson, User.class);
    }

}
