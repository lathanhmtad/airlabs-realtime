package com.example.airlabproject.dto;

import java.math.BigDecimal;

public class CityDTO {
    private String cityCode;
    private String name;
    private BigDecimal lat;
    private BigDecimal lng;
    private String countryCode;
    private String type;

    public CityDTO() {}

    public CityDTO(String cityCode, String name, BigDecimal lat, BigDecimal lng, String countryCode, String type) {
        this.cityCode = cityCode;
        this.name = name;
        this.lat = lat;
        this.lng = lng;
        this.countryCode = countryCode;
        this.type = type;
    }

    public String getCityCode() { return cityCode; }
    public void setCityCode(String cityCode) { this.cityCode = cityCode; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public BigDecimal getLat() { return lat; }
    public void setLat(BigDecimal lat) { this.lat = lat; }

    public BigDecimal getLng() { return lng; }
    public void setLng(BigDecimal lng) { this.lng = lng; }

    public String getCountryCode() { return countryCode; }
    public void setCountryCode(String countryCode) { this.countryCode = countryCode; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
}
