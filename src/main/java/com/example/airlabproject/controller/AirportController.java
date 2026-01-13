package com.example.airlabproject.controller;

import com.example.airlabproject.dto.AirportDTO;
import com.example.airlabproject.service.AirportService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/airports")
public class AirportController {
    private final AirportService airportService;

    public AirportController(AirportService airportService) {
        this.airportService = airportService;
    }

    @GetMapping
    public List<AirportDTO> getAll(@RequestParam(value = "country_code", required = false) String countryCode){
        if (countryCode != null && !countryCode.isBlank()) {
            return airportService.getByCountryCode(countryCode);
        }
        return airportService.getAll();
    }

}
