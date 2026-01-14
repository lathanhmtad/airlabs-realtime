package com.example.airlabproject;

import com.example.airlabproject.service.AirlineService;
import com.example.airlabproject.service.AirportService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class AirlabProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(AirlabProjectApplication.class, args);
    }

    @Bean
    CommandLineRunner initDatabase(AirlineService airlineService, AirportService airportService) {
        return args -> {
            boolean airlineEmpty = airlineService.isDBEmpty();

            if (airlineEmpty) {
                System.out.println(" Fetching airlines from API...");
                airlineService.AirlinesLoadDB();
            }
        };
    }
}