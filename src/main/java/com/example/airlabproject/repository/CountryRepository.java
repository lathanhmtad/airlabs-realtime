package com.example.airlabproject.repository;

import com.example.airlabproject.entity.Country;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CountryRepository extends JpaRepository<Country, String> {
	java.util.List<Country> findAllByContinent_Id(String continentId);
}
