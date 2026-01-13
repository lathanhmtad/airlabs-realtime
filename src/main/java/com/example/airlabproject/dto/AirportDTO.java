package com.example.airlabproject.dto;

import java.math.BigDecimal;

public class AirportDTO {
    private String iataCode;
    private String name;
    private String icaoCode;
    private BigDecimal lat;
    private BigDecimal lng;
    private String countryCode; // FK dạng String

    public AirportDTO(String iataCode, String name, String icaoCode, BigDecimal lat, BigDecimal lng, String countryCode) {
        this.iataCode = iataCode;
        this.name = name;
        this.icaoCode = icaoCode;
        this.lat = lat;
        this.lng = lng;
        this.countryCode = countryCode;
    }

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

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

}
