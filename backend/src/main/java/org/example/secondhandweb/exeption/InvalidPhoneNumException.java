package org.example.secondhandweb.exeption;

public class InvalidPhoneNumException extends RuntimeException {
    public InvalidPhoneNumException(String message) {
        super(message);
    }
}
