package org.example.secondhandweb.exception;


public abstract class BadRequestException extends RuntimeException {

    public BadRequestException(String message) {
        super(message);
    }

    // ==========================================
    // Nested Static Exceptions
    // ==========================================

    public static class AdvertisementStatusException extends BadRequestException {
        public AdvertisementStatusException(String message) { super(message); }
    }

    public static class InvalidAdvertisementIdException extends BadRequestException {
        public InvalidAdvertisementIdException(String message) { super(message); }
    }

    public static class InvalidCategoryIdException extends BadRequestException {
        public InvalidCategoryIdException(String message) { super(message); }
    }

    public static class InvalidMessageException extends BadRequestException {
        public InvalidMessageException(String message) { super(message); }
    }

    public static class InvalidPhoneNumException extends BadRequestException {
        public InvalidPhoneNumException(String message) { super(message); }
    }

    public static class InvalidReviewInfoException extends BadRequestException {
        public InvalidReviewInfoException(String message) { super(message); }
    }

    public static class InvalidScoreException extends BadRequestException {
        public InvalidScoreException(String message) { super(message); }
    }

    public static class PassNotValidException extends BadRequestException {
        public PassNotValidException(String message) { super(message); }
    }

    public static class PriceNegativeException extends BadRequestException {
        public PriceNegativeException(String message) { super(message); }
    }

    public static class SameSellerAndBuyerIdException extends BadRequestException {
        public SameSellerAndBuyerIdException(String message) { super(message); }
    }

    public static class TitleInvalidException extends BadRequestException {
        public TitleInvalidException(String message) { super(message); }
    }

    public static class WrongPasswordException extends BadRequestException {
        public WrongPasswordException(String message) { super(message); }
    }
}