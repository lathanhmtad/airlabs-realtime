package com.example.airlabproject.controller;

import com.example.airlabproject.dto.AirlineDTO;
import com.example.airlabproject.service.AirlineService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/airlines")
public class AirlineController {
    private final AirlineService airlineService;

    public AirlineController(AirlineService airlineService) {
        this.airlineService = airlineService;
    }

    @GetMapping
    public List<AirlineDTO> getAll() {
        return airlineService.getAll();
    }

    @PostMapping("/set-all")
    public void setAll() {
        airlineService.fetchAllAirlines();
    }
}