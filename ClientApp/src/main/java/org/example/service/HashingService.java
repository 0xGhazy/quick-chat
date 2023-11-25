package org.example.service;

import com.google.common.hash.Hashing;

import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;

public class HashingService {

    public static String getSha256(String plainPassword) throws NoSuchAlgorithmException {
        return Hashing.sha256()
                .hashString(plainPassword, StandardCharsets.UTF_8)
                .toString();
    }
}
