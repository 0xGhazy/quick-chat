package org.example.utils;

public class Validator {

    public static boolean haveWhitespaces(String text)
    {
        for (int i =0; i < text.length(); i++)
        {
            if(text.charAt(i) == ' ') return true;
        }
        return false;
    }
}
