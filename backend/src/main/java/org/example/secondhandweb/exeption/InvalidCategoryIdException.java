package org.example.secondhandweb.exeption;

public class InvalidCategoryIdException extends RuntimeException {
    public InvalidCategoryIdException(String message) {
        super(message);
    }
}
