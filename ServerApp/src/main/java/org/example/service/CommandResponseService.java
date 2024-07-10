package org.example.service;

import com.google.gson.Gson;
import org.example.model.CommandResponse;

public class CommandResponseService {

    private static final Gson gson = new Gson();

    public static String jsonify (CommandResponse commandResponse) {
        return gson.toJson(commandResponse);
    }

    public static CommandResponse jsonToObject(String commandResponseJson) {
        return gson.fromJson(commandResponseJson, CommandResponse.class);
    }

}
