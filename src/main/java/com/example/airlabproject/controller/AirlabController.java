package com.example.airlabproject.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/home")
public class AirlabController {

    @Value("${api-key-airlabs}")
    String apiKeyAirlabs;

    @GetMapping
    public String getHomePage() {
        System.out.println(apiKeyAirlabs);
        System.out.println(apiKeyAirlabs);
        return "index";
    }
}
