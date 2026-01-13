package com.example.airlabproject.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "airport")
public class Airport {

    @Id
    @Column(name = "iata_code", length = 3)
    private String iataCode; // PRIMARY KEY

    @Column(nullable = false)
    private String name;

    @Column(name = "icao_code", length = 4)
    private String icaoCode;

    @Column(precision = 10, scale = 6)
    private BigDecimal lat;

    @Column(precision = 10, scale = 6)
    private BigDecimal lng;

    @ManyToOne
    @JoinColumn(name = "country_code")
    private Country country;
}
