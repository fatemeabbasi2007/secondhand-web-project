package org.example.secondhandweb.exception;

public abstract class ConflictException extends RuntimeException {

    public ConflictException(String message) {
        super(message);
    }

    // ==========================================
    // Nested Static Exceptions
    // ==========================================

    public static class EmailAlreadyExistsException extends ConflictException {
        public EmailAlreadyExistsException(String message) { super(message); }
    }

    public static class UsernameAlreadyExistsException extends ConflictException {
        public UsernameAlreadyExistsException(String message) { super(message); }
    }

    public static class PhoneNumAlreadyExistsException extends ConflictException {
        public PhoneNumAlreadyExistsException(String message) { super(message); }
    }

    public static class ReviewAlreadyExistsException extends ConflictException {
        public ReviewAlreadyExistsException(String message) { super(message); }
    }

    public static class AdAlreadyFavoriteException extends ConflictException {
        public AdAlreadyFavoriteException(String message) { super(message); }
    }
}