package org.example.model;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;

@Data
public class User {

    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private String secQ;
    private String secA;
    private HashMap<String, Long> words = new HashMap<>();
    private ArrayList<String> conversations = new ArrayList<>();
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt;

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", secQ='" + secQ + '\'' +
                ", secA='" + secA + '\'' +
                ", words=" + words +
                ", conversations=" + conversations +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}

