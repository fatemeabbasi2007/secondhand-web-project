package org.example.secondhandweb.exeption;

public class SameSellerAndBuyerIdExcpetion extends RuntimeException {
    public SameSellerAndBuyerIdExcpetion(String message) {
        super(message);
    }
}
