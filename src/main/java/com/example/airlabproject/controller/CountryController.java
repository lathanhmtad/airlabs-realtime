package com.example.airlabproject.controller;

import com.example.airlabproject.dto.CountryDTO;
import com.example.airlabproject.entity.Continent;
import com.example.airlabproject.service.CountryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/countries")
public class CountryController {
    private final CountryService countryService;


    public CountryController(CountryService countryService) {
        this.countryService = countryService;
    }

    @GetMapping
    public List<CountryDTO> getAll(@RequestParam(value = "continent_id", required = false) String continentId) {
        if (continentId != null && !continentId.isBlank()) {
            return countryService.getByContinentId(continentId);
        }
        return countryService.getAll();
    }

    @PostMapping("/set-all")
    public int setAll() {
//        return countryService.setAll();
        return 0;
    }
}