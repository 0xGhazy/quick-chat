package org.example.service;

import java.util.HashMap;

public class ChatService {


    public HashMap<String, Long> messageCounter (HashMap<String, Long> map, String message)
    {
        String[] messageWords = message.split(" ");
        for (String word : messageWords)
        {
            if(map.get(word) == null)
            {
                map.put(word, 1l);
            }
            else {
                Long wordFrequency = map.get(word);
                map.put(word, wordFrequency + 1);
            }
        }
        return map;
    }

}
