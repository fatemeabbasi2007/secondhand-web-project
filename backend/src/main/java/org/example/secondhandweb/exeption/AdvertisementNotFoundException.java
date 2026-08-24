package org.example.secondhandweb.exeption;

public class AdvertisementNotFoundException extends RuntimeException {
    public AdvertisementNotFoundException(String message) {
        super(message);
    }
}
