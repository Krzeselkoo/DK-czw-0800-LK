package com.clinic.management.controller;

import com.clinic.management.dto.PatientRequest;
import com.clinic.management.dto.VisitRequest;
import com.clinic.management.dto.VisitSummaryResponse;
import com.clinic.management.service.VisitManagementService;
import com.clinic.management.service.VisitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;
import com.clinic.management.exception.VisitNotFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/visits")
@RequiredArgsConstructor
public class VisitController {
    private final VisitService visitService;
    private final VisitManagementService visitManagementService;

    /**
     * Creates a new visit in the system.
     *
     * This endpoint allows adding a new visit by providing the necessary details such as
     * patient ID, doctor ID, duty ID, and the start date of the visit.
     *
     * @param request the `VisitRequest` object containing the details of the visit to be created
     * @return the ID of the newly created visit
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new visit", description = "Add a new visit to the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Visit created successfully",
                    content = @Content(schema = @Schema(implementation = PatientRequest.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public Long addVisit(@Valid @RequestBody VisitRequest request){
        return visitManagementService.addVisit(request);
    }

    /**
     * Deletes a specific visit by its ID.
     *
     * This endpoint allows the deletion of a visit from the system by providing its unique ID.
     *
     * @param visitId the ID of the visit to delete
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete visit by ID", description = "Delete a specific visit by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Visit deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Visit not found")
    })
    public void deleteVisit(@PathVariable("id") Long visitId){
        visitService.deleteVisit(visitId);
    }

    /**
     * Retrieves a specific visit by its ID.
     *
     * This endpoint fetches a visit from the database using its unique ID and maps it to a `VisitSummaryResponse` object.
     *
     * @param visitId the ID of the visit to retrieve
     * @return a `VisitSummaryResponse` object representing the visit
     * @throws VisitNotFoundException if the visit with the given ID is not found
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get visit by ID", description = "Retrieve the visit by it's given ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Visit fetched successfully"),
            @ApiResponse(responseCode = "404", description = "Visit with given ID not found")
    })
    public VisitSummaryResponse getVisitById(@PathVariable("id") Long visitId){
        return visitService.getVisitById(visitId);
    }

    /**
     * Retrieves all visits from the system.
     *
     * This endpoint returns a list of all visits stored in the system.
     *
     * @return a list of `VisitSummaryResponse` objects representing all visits
     */
    @GetMapping
    @Operation(summary = "Get all visits", description = "Retrieve all visits from the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Visits fetched successfully")
    })
    public List<VisitSummaryResponse> getAllVisits(){
        return visitService.getAllVisits();
    }

    /**
     * Retrieves all visits for a specific patient by their ID.
     *
     * This endpoint returns a list of visits associated with a specific patient.
     *
     * @param patientId the ID of the patient whose visits are to be retrieved
     * @return a list of `VisitSummaryResponse` objects representing the patient's visits
     */
    @GetMapping("/by-patient/{id}")
    @Operation(summary = "Get all visits for a patient", description = "Retrieve all visits for a specific patient by their ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Visits fetched successfully")
    })
    public List<VisitSummaryResponse> getAllVisitsForPatient(@PathVariable("id") Long patientId) {
        return visitService.getAllVisitsForPatient(patientId);
    }

    /**
     * Retrieves all visits for a specific doctor by their ID.
     *
     * This endpoint returns a list of visits associated with a specific doctor.
     *
     * @param doctorId the ID of the doctor whose visits are to be retrieved
     * @return a list of `VisitSummaryResponse` objects representing the doctor's visits
     */
    @GetMapping("/by-doctor/{id}")
    @Operation(summary = "Get all visits for a doctor", description = "Retrieve all visits for a specific doctor by their ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Visits fetched successfully")
    })
    public List<VisitSummaryResponse> getAllVisitsForDoctor(@PathVariable("id") Long doctorId){
        return visitService.getAllVisitsForDoctor(doctorId);
    }

    /**
     * Retrieves all visits for a specific duty by its ID.
     *
     * This endpoint returns a list of visits associated with a specific duty.
     *
     * @param dutyId the ID of the duty whose visits are to be retrieved
     * @return a list of `VisitSummaryResponse` objects representing the duty's visits
     */
    @GetMapping("/by-duty/{id}")
    @Operation(summary = "Get all visits for a duty", description = "Retrieve all visits for a specific duty by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Visits fetched successfully")
    })
    public List<VisitSummaryResponse> getAllVisitsForDuty(@PathVariable("id") Long dutyId){
        return visitService.getAllVisitsForDuty(dutyId);
    }

    /**
     * Generates a map of available time slots for scheduling a visit with a specific doctor
     * within a given date range, considering the patient's and doctor's existing schedules.
     *
     * The function checks the doctor's duties and both the patient's and doctor's existing visits
     * to determine available time slots. Each available time slot is associated with the corresponding
     * duty ID, which can be used for scheduling purposes.
     *
     * @param patientId the ID of the patient who wants to schedule a visit
     * @param doctorId the ID of the doctor with whom the visit is to be scheduled
     * @param from the start of the date range for which to generate available slots
     * @param to the end of the date range for which to generate available slots
     * @return a map where the key is the start time of an available slot (LocalDateTime),
     *         and the value is the corresponding duty ID (Long)
     */
    @GetMapping("/available-hours")
    @Operation(summary = "Generate available hours for a visit", description = "Generate available time slots for scheduling a visit with a specific doctor within a given date range.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Available time slots generated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters")
    })
    public Map<LocalDateTime, Long> generateAvailableHoursForVisit(@RequestParam("patientId") Long patientId,
                                                                   @RequestParam("doctorId") Long doctorId,
                                                                   @RequestParam("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
                                                                   @RequestParam("to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to){
        return visitManagementService.generateAvailableHoursForVisit(patientId, doctorId, from, to);
    }


}
