package com.example.airlabproject.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "flight_schedules")
public class FlightSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String airlineIata; // Hãng bay (IATA)
    private String flightIata;  // Số hiệu chuyến bay (VN123)
    private String depIata;     // Sân bay đi (HAN)
    private String arrIata;     // Sân bay đến (SGN)
    private String status;      // Trạng thái

    private LocalDateTime depTime; // Giờ đi
    private LocalDateTime arrTime; // Giờ đến
    private LocalDateTime depTimeUtc; // Giờ đi UTC
    private LocalDateTime arrTimeUtc; // Giờ đến UTC

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // ===== getters & setters =====
    

}