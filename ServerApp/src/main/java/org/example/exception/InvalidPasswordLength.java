package org.example.exception;

public class InvalidPasswordLength extends RuntimeException{

    public InvalidPasswordLength(String message) {
        super(message);
    }
}
