package org.example.secondhandweb.exeption;

public class InvalidAdvertisementIdException extends RuntimeException {
    public InvalidAdvertisementIdException(String message) {
        super(message);
    }
}
