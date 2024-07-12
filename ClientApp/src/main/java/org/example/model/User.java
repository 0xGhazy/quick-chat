package org.example.model;

import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;
import java.util.HashMap;

@Data
@ToString
public class User {

    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private String securityQuestionId;
    private String securityAnswer;
    private HashMap<String, Long> words = new HashMap<>();
    private ArrayList<String> conversations = new ArrayList<>();

}
