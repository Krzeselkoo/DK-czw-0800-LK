package com.clinic.management.controller;


import com.clinic.management.dto.DutyRequest;
import com.clinic.management.dto.DutySummaryResponse;
import com.clinic.management.service.DutyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/duties")
@RequiredArgsConstructor
public class DutyController {
    private final DutyService dutyService;

    @GetMapping("/{id}")
    @Operation(summary = "Get duty by ID", description = "Retrieve a specific duty by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Duty fetched successfully",
                    content = @Content(schema = @Schema(implementation = DutySummaryResponse.class))),
            @ApiResponse(responseCode = "404", description = "Duty not found")
    })
    public DutySummaryResponse getDutyByID(@PathVariable long id){
        return dutyService.getDuty(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new duty", description = "Add a new duty to the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Duty created successfully",
                    content = @Content(schema = @Schema(implementation = DutyRequest.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Couldn't create duty with given parameters")
    })
    public long addDuty(@Valid @RequestBody DutyRequest dutyRequest) {
        return dutyService.addDuty(dutyRequest);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete duty by ID", description = "Delete a specific duty by their ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Duty deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Duty not found")
    })
    public void deleteDuty(@PathVariable long id){
        dutyService.deleteDuty(id);
    }
}
