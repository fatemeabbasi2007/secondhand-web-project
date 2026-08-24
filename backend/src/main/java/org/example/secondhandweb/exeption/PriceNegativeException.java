package org.example.secondhandweb.exeption;

public class PriceNegativeException extends RuntimeException {
    public PriceNegativeException(String message) {
        super(message);
    }
}
