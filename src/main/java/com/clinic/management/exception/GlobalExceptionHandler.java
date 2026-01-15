package com.clinic.management.exception;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice(basePackages = {"com.clinic.management.controller"})
@Hidden
public class GlobalExceptionHandler {

    @ExceptionHandler(RoomOccupiedAtDateException.class)
    public ResponseEntity<String> handleRoomOccupied(RoomOccupiedAtDateException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body("Room is occupied: " + ex.getMessage());
    }

    @ExceptionHandler(DoctorBusyAtDateException.class)
    public ResponseEntity<String> handleDoctorBusy(DoctorBusyAtDateException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body("Doctor is busy: " + ex.getMessage());
    }

    @ExceptionHandler(DuplicatePeselException.class)
    public ResponseEntity<String> handleDuplicatePesel(DuplicatePeselException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    @ExceptionHandler(DuplicateRoomCodeException.class)
    public ResponseEntity<String> handleDuplicateRoomCode(DuplicateRoomCodeException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    @ExceptionHandler(DutyConflictException.class)
    public ResponseEntity<String> handleDutyConflict(DutyConflictException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    @ExceptionHandler(DutyNotFoundException.class)
    public ResponseEntity<String> handleDutyNotFound(DutyNotFoundException ex){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(InvalidTimeWindowException.class)
    public ResponseEntity<String> handleInvalidTimeWindow(InvalidTimeWindowException ex){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(DoctorNotFoundException.class)
    public ResponseEntity<String> handleDoctorNotFound(DoctorNotFoundException ex){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(ExamRoomNotFoundException.class)
    public ResponseEntity<String> handleExamRoomNotFound(ExamRoomNotFoundException ex){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(PatientNotFoundException.class)
    public ResponseEntity<String> handlePatientNotFound(PatientNotFoundException ex){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }
}