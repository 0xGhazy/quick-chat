package org.example.model;

import lombok.Data;
import org.example.utils.DateTimeHandler;

import java.util.ArrayList;

@Data
public class Conversation {

    private String username;
    private ArrayList<String> messages = new ArrayList<>();
    private String startTime;
    private String endTime;

    public Conversation() {
        this.startTime = DateTimeHandler.timeNow();
    }

    public void pushMessage(String message) {
        messages.add(message);
    }

    @Override
    public String toString() {
        return "Conversation{" +
                "username='" + username + '\'' +
                ", messages=" + messages +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                '}';
    }

    public Conversation getThis(String username) {
        this.endTime = DateTimeHandler.timeNow();
        this.username = username;
        return this;
    }
}