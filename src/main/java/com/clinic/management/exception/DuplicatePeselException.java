package com.clinic.management.exception;

public class DuplicatePeselException extends RuntimeException{
    public DuplicatePeselException(String message){
        super(message);
    }
}
