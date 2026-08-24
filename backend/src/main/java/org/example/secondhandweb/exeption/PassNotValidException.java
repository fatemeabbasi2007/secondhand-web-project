package org.example.secondhandweb.exeption;

public class PassNotValidException extends RuntimeException {
    public PassNotValidException(String message) {
        super(message);
    }
}
