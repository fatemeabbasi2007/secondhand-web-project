package org.example.secondhandweb.exception;

public abstract class ForbiddenException extends RuntimeException {

    public ForbiddenException(String message) {
        super(message);
    }

    // ==========================================
    // Nested Static Exceptions
    // ==========================================

    public static class NoAccessException extends ForbiddenException {
        public NoAccessException(String message) { super(message); }
    }

    public static class UserBannedException extends ForbiddenException {
        public UserBannedException(String message) { super(message); }
    }
}