package com.example.airlabproject.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FlightScheduleDTO {
    private String airlineIata;
    private String airlineIcao;

    private String flightIata;
    private String flightIcao;

    private String depIata;
    private String depIcao;
    private LocalDateTime depTime;
    private LocalDateTime depTimeUtc;

    private String arrIata;
    private String arrIcao;
    private LocalDateTime arrTime;
    private LocalDateTime arrTimeUtc;

    private String status;
}
