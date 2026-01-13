package com.example.airlabproject.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "city")
public class City {

    @Id
    @Column(name = "city_code")
    private String cityCode;

    @Column(nullable = false)
    private String name;

    @Column(precision = 10, scale = 6)
    private BigDecimal lat;

    @Column(precision = 10, scale = 6)
    private BigDecimal lng;

    @Column(length = 50)
    private String type; // usually "city"

    @ManyToOne
    @JoinColumn(name = "country_code")
    private Country parentCountry;

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Country getParentCountry() {
        return parentCountry;
    }

    public void setParentCountry(Country parentCountry) {
        this.parentCountry = parentCountry;
    }
}
