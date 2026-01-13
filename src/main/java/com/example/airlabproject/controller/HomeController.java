package com.example.airlabproject.controller;

import com.example.airlabproject.entity.Continent;
import com.example.airlabproject.repository.ContinentRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
@AllArgsConstructor
public class HomeController {

    private ContinentRepository continentRepository;

    @GetMapping("/")
    public String home() {
        return "index";
    }
}
