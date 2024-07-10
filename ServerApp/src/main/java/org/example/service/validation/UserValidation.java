package org.example.service.validation;

import org.example.exception.InvalidPasswordLength;
import org.example.exception.InvalidUserEntity;

public class UserValidation {


    public String validateUserPassword(String password) {
        if(password.length() < 8) {
            throw new InvalidPasswordLength("Password must be at least 8 characters");
        }
        return password;
    }

    public String validateUserSecurityAnswer(String securityAnswer) {
        if(securityAnswer.isEmpty()) {
            throw new InvalidUserEntity("Security answer cannot be empty");
        }
        return securityAnswer;
    }

}
