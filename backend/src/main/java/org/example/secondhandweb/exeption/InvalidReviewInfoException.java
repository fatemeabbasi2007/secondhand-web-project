package org.example.secondhandweb.exeption;

public class InvalidReviewInfoException extends RuntimeException {
    public InvalidReviewInfoException(String message) {
        super(message);
    }
}
