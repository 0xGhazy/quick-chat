package org.example.service;

import com.google.gson.Gson;
import org.example.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UserService {

    private Gson gson = new Gson();

    public String serialize(User user) {
        return gson.toJson(user);
    }

    public User deSerialize(String userJson) {
        return gson.fromJson(userJson, User.class);
    }

    public HashMap<String, Long> accumulateUserWords(HashMap<String, Long> newWords, User oldUser)
    {
        HashMap<String, Long> oldWords = oldUser.getWords();
        for(Map.Entry<String, Long> tempMap: newWords.entrySet())
        {
            String key = tempMap.getKey();
            Long value = tempMap.getValue();
            if(oldWords.get(key) == null)
                oldWords.put(key, 1l);
            else
                oldWords.put(key, value + newWords.get(key));
        }
        return oldWords;
    }

    public ArrayList<String> accumulateUserConversation(ArrayList<String> newConversation, User oldUser)
    {
        ArrayList<String> oldConversation = oldUser.getConversations();
        oldConversation.addAll(newConversation);
        return oldConversation;
    }

}
