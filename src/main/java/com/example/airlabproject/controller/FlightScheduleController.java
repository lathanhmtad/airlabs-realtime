package com.example.airlabproject.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.airlabproject.dto.FlightScheduleDTO;
import com.example.airlabproject.service.FlightScheduleService;

@RestController
@RequestMapping("api/flights")
public class FlightScheduleController {
    private FlightScheduleService flightService;

    public FlightScheduleController(FlightScheduleService flightService) {
        this.flightService = flightService;
    }

    @GetMapping()
    public List<FlightScheduleDTO> getAllFlights(@RequestParam(value = "airport_code", required = false) String airportCode) {
        if (airportCode != null && !airportCode.isEmpty()) {
            // Ensure data exists/cached by invoking fetch-on-miss then map to DTOs
            return flightService.getFlightsByAirportCode(airportCode);
        }
        return flightService.getAll();
    }

}