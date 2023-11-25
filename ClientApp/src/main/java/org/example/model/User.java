package org.example.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;

@Data
public class User {

    private String username;
    private String password;
    private String secQ;
    private String secA;
    private HashMap<String, Long> words = new HashMap<>();
    private ArrayList<String> conversations = new ArrayList<>();

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", secQ='" + secQ + '\'' +
                ", secA='" + secA + '\'' +
                ", words=" + words +
                ", conversations=" + conversations +
                '}';
    }
}
