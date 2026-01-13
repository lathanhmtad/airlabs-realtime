package com.example.airlabproject.controller;

import com.example.airlabproject.dto.ContinentDTO;
import com.example.airlabproject.service.ContinentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/continents")
public class ContinentController {
    private final ContinentService continentService;

    public ContinentController(ContinentService continentService) {
        this.continentService = continentService;
    }

    @PostMapping
    public ContinentDTO create(@RequestBody ContinentDTO dto){
        return continentService.create(dto);
    }

    @GetMapping
    public List<ContinentDTO> getAll(){
        return continentService.getAll();
    }
}
