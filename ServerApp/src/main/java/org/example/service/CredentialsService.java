package org.example.service;

import com.google.gson.Gson;
import org.example.model.Credentials;

public class CredentialsService {

    private Gson gson = new Gson();

    public String serialize(Credentials credential) {
        return gson.toJson(credential);
    }

    public Credentials deSerialize(String credentialsJson) {
        return gson.fromJson(credentialsJson, Credentials.class);
    }
}
