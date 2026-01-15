package com.clinic.management.exception;

public class ExamRoomNotFoundException extends RuntimeException{

    public ExamRoomNotFoundException(String message){
        super(message);
    }
}
