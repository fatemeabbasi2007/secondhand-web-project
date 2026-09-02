package org.example.secondhandweb.exception;

public abstract class NotFoundException extends RuntimeException {

    public NotFoundException(String message) {
        super(message);
    }

    // ==========================================
    // Nested Static Exceptions
    // ==========================================

    public static class AdvertisementNotFoundException extends NotFoundException {
        public AdvertisementNotFoundException(String message) { super(message); }
    }

    public static class UserNotFoundException extends NotFoundException {
        public UserNotFoundException(String message) { super(message); }
    }

    public static class ConversationNotFoundException extends NotFoundException {
        public ConversationNotFoundException(String message) { super(message); }
    }

    public static class AdNotFavException extends NotFoundException {
        public AdNotFavException(String message) { super(message); }
    }
}