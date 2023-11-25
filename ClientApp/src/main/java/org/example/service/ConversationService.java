package org.example.service;

import com.google.gson.Gson;
import org.example.model.Conversation;

public class ConversationService {

    private Gson gson = new Gson();

    public String serialize(Conversation conversation) {
        return gson.toJson(conversation);
    }

    public Conversation deSerialize(String conversationJson) {
        return gson.fromJson(conversationJson, Conversation.class);
    }

}
