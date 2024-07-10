package org.example.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;

@Data
public class User {

    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String username;
    private HashMap<String, Long> words = new HashMap<>();
    private ArrayList<String> conversations = new ArrayList<>();

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", username='" + username + '\'' +
                ", words=" + words +
                ", conversations=" + conversations +
                '}';
    }
}
