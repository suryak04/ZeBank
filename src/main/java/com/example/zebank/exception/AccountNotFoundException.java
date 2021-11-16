package com.example.zebank.exception;

public class AccountNotFoundException extends Exception {
    private final String message = "Account Not Found";

    @Override
    public String getMessage(){
        return message;
    }
}
