package com.example.airlabproject.controller;

import com.example.airlabproject.dto.CityDTO;
import com.example.airlabproject.service.CityService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cities")
public class CityController {
    private final CityService cityService;

    public CityController(CityService cityService) {
        this.cityService = cityService;
    }

    @GetMapping
    public List<CityDTO> getAll(@RequestParam(value = "country_code", required = false) String countryCode) {
        if (countryCode != null && !countryCode.isBlank()) {
            return cityService.getByCountryCode(countryCode);
        }
        return cityService.getAll();
    }

    @PostMapping("/set-all")
    public int setAll() {
        return cityService.saveAllVietnamCityFromAirlabs();
    }
}
