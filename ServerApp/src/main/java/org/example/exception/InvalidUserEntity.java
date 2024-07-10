package org.example.exception;

public class InvalidUserEntity extends RuntimeException {

    public InvalidUserEntity(String message) {
        super(message);
    }

}
