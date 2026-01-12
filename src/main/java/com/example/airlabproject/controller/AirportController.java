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
    public List<AirportDTO> getAll(@RequestParam(required = false) String city_code){
        if (city_code != null && !city_code.isBlank()) {
            return airportService.getByCityCode(city_code);
        }
        return airportService.getAll();
    }


    @PostMapping("/set/{city_code}")
    public int setByCityCode(@PathVariable("city_code") String cityCode) {
        return airportService.saveByCityCode(cityCode);
    }

}
