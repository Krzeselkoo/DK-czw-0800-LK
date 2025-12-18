package com.clinic.management.exception;

public class RoomOccupiedAtDateException extends RuntimeException {
    public RoomOccupiedAtDateException(String message) {
        super(message);
    }
}
