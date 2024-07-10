package org.example.service;

import com.google.common.hash.Hashing;

import java.nio.charset.StandardCharsets;

public class HashingService {

    public static String getSha256(String plainPassword) {
        return Hashing.sha256()
                .hashString(plainPassword, StandardCharsets.UTF_8)
                .toString();
    }
}
