package org.example.repository;

import org.example.model.User;

import java.io.IOException;
import java.sql.SQLException;

public interface UserRepo {

    User insertUser(User user) throws SQLException, ClassNotFoundException, IOException;
    User updateUserById(User user);
    User findByUsername(String username);
    User findByEmail(String email);
    User findById(long id);
    User findByUsernameAndPassword(String username, String password);
    void delete(long id);
    void deleteByUserName(String username);

}
