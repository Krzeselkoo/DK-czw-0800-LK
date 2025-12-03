package com.clinic.management.controller;

import com.clinic.management.service.InitDataService;
import org.springframework.http.ResponseEntity;
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

    @GetMapping
    public String DatabaseWelcome(){
        return "You are in a Database Endpoint area";
    }

    @PostMapping("/initialize")
    public ResponseEntity<String> initializeDatabase(){
        System.out.println("About to initialize");
        initDataService.initialize();
        return ResponseEntity.ok("Database initialized successfully");
    }
    
}
