package com.clinic.management.controller;

import com.clinic.management.dto.DoctorRequest;
import com.clinic.management.dto.DoctorSummaryResponse;
import com.clinic.management.service.DoctorService;
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
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
public class DoctorController {

    private final DoctorService doctorService;

    /**
     * Adds a new doctor to the system.
     *
     * @param doctorRequest the request object containing doctor details
     * @return new doctor's ID
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new doctor", description = "Add a new doctor to the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Doctor created successfully",
                    content = @Content(schema = @Schema(implementation = DoctorRequest.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Duplicate PESEL found")
    })
    public long addDoctor(@Valid @RequestBody DoctorRequest doctorRequest) {
        return doctorService.addDoctor(doctorRequest);
    }

    /**
     * Retrieves all doctors from the system.
     *
     * @return a list of doctor summary responses
     */
    @GetMapping
    @Operation(summary = "Get all doctors", description = "Get all doctors from the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Doctors fetched successfully")
    })
    public List<DoctorSummaryResponse> getDoctors() {
        return doctorService.getAllDoctors();
    }

    /**
     * Retrieves a specific doctor by their ID.
     *
     * @param id the ID of the doctor to retrieve
     * @return the doctor entity
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get doctor by ID", description = "Retrieve a specific doctor by their ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Doctor fetched successfully",
                    content = @Content(schema = @Schema(implementation = DoctorSummaryResponse.class))),
            @ApiResponse(responseCode = "404", description = "Doctor not found")
    })
    public DoctorSummaryResponse getDoctor(@PathVariable long id) {
        return doctorService.getDoctor(id);
    }

    /**
     * Deletes a specific doctor by their ID.
     *
     * @param id the ID of the doctor to delete
     * @return a response entity indicating the result of the operation
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete doctor by ID", description = "Delete a specific doctor by their ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Doctor deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Doctor not found")
    })
    public ResponseEntity<Void> deleteDoctor(@PathVariable long id) {
        doctorService.deleteDoctor(id);
        return ResponseEntity.noContent().build();
    }
}

