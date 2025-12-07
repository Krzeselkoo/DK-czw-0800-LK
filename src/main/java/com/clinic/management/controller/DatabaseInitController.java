package com.clinic.management.controller;

import com.clinic.management.dto.DoctorRequest;
import com.clinic.management.service.InitDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/database")
public class DatabaseInitController {
    private final InitDataService initDataService;

    public DatabaseInitController(InitDataService initDataService){
        this.initDataService = initDataService;
    }


    /**
     * Fills the database with 7 placeholder doctors.
     *
     * @return a response entity indicating the result of the operation
     */
    @PostMapping("/initialize")
    @Operation(summary = "Initialize 7 placeholder doctors", description = "Add 7 placeholder doctors to the system's database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Doctors added successfully")
    })
    public ResponseEntity<String> initializeDatabase(){
        System.out.println("About to initialize");
        initDataService.initialize();
        return ResponseEntity.ok("Database initialized successfully");
    }
    
}
