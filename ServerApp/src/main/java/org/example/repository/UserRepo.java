package org.example.repository;

import org.example.model.User;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;


public interface UserRepo {

    Optional<User> insertUser(User user) throws SQLException, IOException;
    Optional<User> findByEmail(String email) throws SQLException, ClassNotFoundException, IOException;
    Optional<User> findById(long id) throws SQLException, IOException, ClassNotFoundException;
    Optional<User> findByUsernameAndPassword(String username, String password) throws IOException, ClassNotFoundException, SQLException;
    Optional<User> updateUserById(Long id, User user) throws SQLException, IOException, ClassNotFoundException;

}
