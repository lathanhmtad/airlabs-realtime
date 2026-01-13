package com.example.airlabproject.dto;

public class CountryDTO {
    private String code;
    private String code3;
    private String name;
    private String continentId;

    public CountryDTO() {
    }

    public CountryDTO(String code, String code3, String name, String continentId) {
        this.code = code;
        this.code3 = code3;
        this.name = name;
        this.continentId = continentId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCode3() {
        return code3;
    }

    public void setCode3(String code3) {
        this.code3 = code3;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContinentId() {
        return continentId;
    }

    public void setContinentId(String continentId) {
        this.continentId = continentId;
    }
}
