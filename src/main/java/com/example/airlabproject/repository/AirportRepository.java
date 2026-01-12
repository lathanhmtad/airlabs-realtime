package com.example.airlabproject.repository;

import com.example.airlabproject.dto.AirportDTO;
import com.example.airlabproject.entity.Airport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AirportRepository extends JpaRepository<Airport, String> {
    List<AirportDTO> findAllByParentCountry_Code(String parentCountryCode);
    List<Airport> findAllByParentCity_CityCode(String cityCode);
}