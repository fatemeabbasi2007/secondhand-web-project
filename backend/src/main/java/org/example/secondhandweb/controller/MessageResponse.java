package org.example.secondhandweb.controller;

public class MessageResponse {

    private String message;

    // ۱. سازنده (Constructor) برای مقداردهی راحت در کنترلر
    public MessageResponse(String m) {
        this.message = m;
    }

    // ۲. متد Getter (اسپرینگ‌بوت برای تبدیل این کلاس به JSON به این گتر نیاز دارد)
    public String getMessage() {
        return message;
    }

    // ۳. متد Setter
    public void setMessage(String m) {
        this.message = m;
    }
}