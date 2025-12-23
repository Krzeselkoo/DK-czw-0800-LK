package com.clinic.management.exception;

public class InvalidTimeWindowException extends RuntimeException{
    public InvalidTimeWindowException(String message){
        super(message);
    }
}
