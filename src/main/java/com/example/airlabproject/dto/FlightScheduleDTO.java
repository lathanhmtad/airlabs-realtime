package com.example.airlabproject.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FlightScheduleDTO {
    // private String airlineIata;
    // private String airlineIcao;

    // private String flightIata;
    // private String flightIcao;

    // private String depIata;
    // private String depIcao;
    // private LocalDateTime depTime;
    // private LocalDateTime depTimeUtc;

    // private String arrIata;
    // private String arrIcao;
    // private LocalDateTime arrTime;
    // private LocalDateTime arrTimeUtc;

    // private String status;

    private String airlineIata; // Hãng bay (IATA)
    private String flightIata;  // Số hiệu chuyến bay (VN123)
    private String depIata;     // Sân bay đi (HAN)
    private String arrIata;     // Sân bay đến (SGN)
    private String status;      // Trạng thái

    private LocalDateTime depTime; // Giờ đi
    private LocalDateTime arrTime; // Giờ đến
    private LocalDateTime depTimeUtc; // Giờ đi UTC
    private LocalDateTime arrTimeUtc; // Giờ đến UTC

    public FlightScheduleDTO() {
    }

    public FlightScheduleDTO(String airlineIata, String flightIata, String depIata, String arrIata, String status, LocalDateTime depTime, LocalDateTime arrTime, LocalDateTime depTimeUtc, LocalDateTime arrTimeUtc) {
        this.airlineIata = airlineIata;
        this.flightIata = flightIata;
        this.depIata = depIata;
        this.arrIata = arrIata;
        this.status = status;
        this.depTime = depTime;
        this.arrTime = arrTime;
        this.depTimeUtc = depTimeUtc;
        this.arrTimeUtc = arrTimeUtc;
    }

    

}
