package com.example.airlabproject.repository;

import com.example.airlabproject.entity.City;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CityRepository extends JpaRepository<City, String> {
	List<City> findAllByParentCountry_Code(String parentCountryCode);
}
