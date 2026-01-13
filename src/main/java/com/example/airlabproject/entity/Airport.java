package com.example.airlabproject.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

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

    // Foreign key tới bảng countries (self reference)
    @ManyToOne
    @JoinColumn(name = "country_code")
    private Country parentCountry;


    // ===== getters & setters =====

    public String getIataCode() {
        return iataCode;
    }

    public void setIataCode(String iataCode) {
        this.iataCode = iataCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcaoCode() {
        return icaoCode;
    }

    public void setIcaoCode(String icaoCode) {
        this.icaoCode = icaoCode;
    }

    public BigDecimal getLat() {
        return lat;
    }

    public void setLat(BigDecimal lat) {
        this.lat = lat;
    }

    public BigDecimal getLng() {
        return lng;
    }

    public void setLng(BigDecimal lng) {
        this.lng = lng;
    }

    public Country getParentCountry() {
        return parentCountry;
    }

    public void setParentCountry(Country parentCountry) {
        this.parentCountry = parentCountry;
    }

}
