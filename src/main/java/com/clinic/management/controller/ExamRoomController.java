package com.clinic.management.controller;

import com.clinic.management.dto.DoctorSummaryResponse;
import com.clinic.management.dto.DutySummaryResponse;
import com.clinic.management.dto.ExamRoomRequest;
import com.clinic.management.dto.ExamRoomSummaryResponse;
import com.clinic.management.service.ExamRoomService;
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
@RequestMapping("/api/exam_rooms")
@RequiredArgsConstructor
public class ExamRoomController {
    public final ExamRoomService examRoomService;

    /**
     * Retrieves an exam room by their ID.
     *
     * @param id the ID of the room to retrieve
     * @return the room summary response
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get exam room by ID", description = "Retrieve a specific room by it's ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Room fetched successfully",
                    content = @Content(schema = @Schema(implementation = ExamRoomSummaryResponse.class))),
            @ApiResponse(responseCode = "404", description = "Room not found")
    })
    public ExamRoomSummaryResponse getExamRoom(@PathVariable long id) {
        return examRoomService.getExamRoom(id);
    }

    /**
     * Retrieves all exam rooms from the repository.
     *
     * @return a list of exam rooms summary responses
     */
    @GetMapping
    @Operation(summary = "Get all examining rooms", description = "Get all examining rooms from the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rooms fetched successfully")
    })
    public List<ExamRoomSummaryResponse> getAllExamRooms() {
        return examRoomService.getAllExamRooms();
    }

    /**
     * Adds a new exam room to the system.
     *
     * @param roomRequest the request object containing room's details
     * @return new room's ID
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new room", description = "Add a new exam room to the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Exam room created successfully",
                    content = @Content(schema = @Schema(implementation = ExamRoomRequest.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Duplicate room code found")
    })
    public long addExamRoom(@Valid @RequestBody ExamRoomRequest roomRequest) {
        return examRoomService.addExamRoom(roomRequest);
    }

    /**
     * Deletes a specific exam room by their ID.
     *
     * @param id the ID of the exam room to delete
     * @return a response entity indicating the result of the operation
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete exam room by ID", description = "Delete a specific exam room by their ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Exam room deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Exam room not found"),
            @ApiResponse(responseCode = "409", description = "Cannot delete room. Room is assigned to duties.")
    })
    public ResponseEntity<Void> deleteExamRoom(@PathVariable long id) {
        examRoomService.deleteExamRoom(id);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/{id}/duties")
    @Operation(summary = "Get all duties of the room with given ID", description = "Get all duties of the room with given ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Duties fetched successfully",
                    content = @Content(schema = @Schema(implementation = DutySummaryResponse.class)))
    })
    public List<DutySummaryResponse> getAllDutiesForExamRoom(@PathVariable long id){
        return examRoomService.getAllDutiesForExamRoom(id);
    }
}