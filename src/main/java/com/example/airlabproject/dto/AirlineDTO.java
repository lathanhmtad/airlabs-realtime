package com.example.airlabproject.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class AirlineDTO {
    private String iataCode;
    private String name;
    private String icaoCode;

    public AirlineDTO(String iataCode, String name, String  icaoCode) {
        this.iataCode = iataCode;
        this.name = name;
        this.icaoCode = icaoCode;
    }
}