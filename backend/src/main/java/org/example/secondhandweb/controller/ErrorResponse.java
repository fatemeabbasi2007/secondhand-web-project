package org.example.secondhandweb.controller;

import java.util.HashMap;
import java.util.Map;

public class ErrorResponse {

    private String error;
    private Map<String, String> fieldErrors; // ذخیره جفت‌های (اسم فیلد -> پیام خطا)

    // ۱. سازنده برای خطاهای عمومی (بدون فیلد خاص مثل خطای سرور یا عدم دسترسی)
    public ErrorResponse(String error) {
        this.error = error;
        this.fieldErrors = new HashMap<>(); // برای جلوگیری از خطای NullPointerException در فرانت‌اند
    }

    // ۲. سازنده برای خطاهای فرم و اعتبارسنجی فیلدها
    public ErrorResponse(String error, Map<String, String> fieldErrors) {
        this.error = error;
        this.fieldErrors = fieldErrors;
    }

    // متدهای Getter و Setter
    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public Map<String, String> getFieldErrors() {
        return fieldErrors;
    }

    public void setFieldErrors(Map<String, String> fieldErrors) {
        this.fieldErrors = fieldErrors;
    }
}