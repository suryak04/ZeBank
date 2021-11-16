package com.example.zebank.exception;

public class InsufficientBalanceException extends Exception {
    private final String message = "Insufficient Balance";

    @Override
    public String getMessage(){
        return message;
    }
}
