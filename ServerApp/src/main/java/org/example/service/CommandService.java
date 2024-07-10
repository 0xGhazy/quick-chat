package org.example.service;

import com.google.gson.Gson;
import org.example.model.Command;

public class CommandService {

    private static final Gson gson = new Gson();

    public static String jsonify (Command command) {
        return gson.toJson(command);
    }

    public static Command jsonToObject(String commandJson) {
        return gson.fromJson(commandJson, Command.class);
    }
}
