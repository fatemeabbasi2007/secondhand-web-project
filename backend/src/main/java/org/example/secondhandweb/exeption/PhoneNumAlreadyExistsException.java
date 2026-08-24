package org.example.secondhandweb.exeption;

public class PhoneNumAlreadyExistsException extends RuntimeException {
    public PhoneNumAlreadyExistsException(String message) {
        super(message);
    }
}
