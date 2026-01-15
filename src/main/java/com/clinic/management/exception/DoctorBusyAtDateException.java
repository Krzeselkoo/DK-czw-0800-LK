package com.clinic.management.exception;

public class DoctorBusyAtDateException extends RuntimeException {
    public DoctorBusyAtDateException(String message) {
        super(message);
    }
}
