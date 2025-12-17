package com.clinic.management.controller;

import com.clinic.management.dto.PatientRequest;
import com.clinic.management.dto.PatientSummaryResponse;
import com.clinic.management.service.PatientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;

    /**
     * Adds a new patient to the system.
     *
     * @param patientRequest the request object containing patient details
     * @return new patient's ID
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new patient", description = "Add a new patient to the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Patient created successfully",
                    content = @Content(schema = @Schema(implementation = PatientRequest.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Duplicate PESEL found")
    })
    public long addPatient(@Valid @RequestBody PatientRequest patientRequest) {
        return patientService.addPatient(patientRequest);
    }

    /**
     * Retrieves all patients from the system.
     *
     * @return a list of patient summary responses
     */
    @GetMapping
    @Operation(summary = "Get all patients", description = "Get all patients from the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Patients fetched successfully")
    })
    public List<PatientSummaryResponse> getPatients() {
        return patientService.getAllPatients();
    }

    /**
     * Retrieves a specific patient by their ID.
     *
     * @param id the ID of the patient to retrieve
     * @return the patient summary response
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get patient by ID", description = "Retrieve a specific patient by their ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Patient fetched successfully",
                    content = @Content(schema = @Schema(implementation = PatientSummaryResponse.class))),
            @ApiResponse(responseCode = "404", description = "Patient not found")
    })
    public PatientSummaryResponse getPatient(@PathVariable long id) {
        return patientService.getPatient(id);
    }

    /**
     * Deletes a specific patient by their ID.
     *
     * @param id the ID of the patient to delete
     * @return a response entity indicating the result of the operation
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete patient by ID", description = "Delete a specific patient by their ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Patient deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Patient not found")
    })
    public ResponseEntity<Void> deletePatient(@PathVariable long id) {
        patientService.deletePatient(id);
        return ResponseEntity.noContent().build();
    }
}