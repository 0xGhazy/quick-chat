package org.example.service;

import com.google.gson.Gson;
import org.example.model.User;


public class UserService {

    private Gson gson = new Gson();

    public String serialize(User user) {
        return gson.toJson(user);
    }

    public User deSerialize(String userJson) {
        return gson.fromJson(userJson, User.class);
    }


}
