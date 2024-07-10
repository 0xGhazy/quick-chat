package org.example.utils;

import java.io.*;
import java.util.ArrayList;

public class Serializer {

    public static byte[] serializeObject(Object object) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bos);
        out.writeObject(object);
        return bos.toByteArray();
    }

    public static Object deserializeObject(byte[] data) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bis = new ByteArrayInputStream(data);
        ObjectInputStream in = new ObjectInputStream(bis);
        return in.readObject();
    }

    public static ArrayList<String> deserializeStringArray(byte[] data) throws IOException, ClassNotFoundException {
        // Create a byte array input stream
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
        // Create an object input stream
        ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
        return (ArrayList<String>) objectInputStream.readObject();
    }
}
