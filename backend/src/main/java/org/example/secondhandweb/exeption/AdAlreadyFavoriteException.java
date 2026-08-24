package org.example.secondhandweb.exeption;

public class AdAlreadyFavoriteException extends RuntimeException {
    public AdAlreadyFavoriteException(String message) {
        super(message);
    }
}
