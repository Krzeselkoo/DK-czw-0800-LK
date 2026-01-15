package com.clinic.management.exception;

public class DuplicateRoomCodeException extends RuntimeException{
    public DuplicateRoomCodeException(String message){
        super(message);
    }
}
