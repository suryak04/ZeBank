package com.example.zebank.exception;

public class InvalidInputException extends Exception{
    private final String message = "Invalid Input";

    @Override
    public String getMessage(){
        return message;
    }
}
