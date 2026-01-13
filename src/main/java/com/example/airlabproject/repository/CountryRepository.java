package com.example.airlabproject.repository;

import com.example.airlabproject.entity.Country;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CountryRepository extends JpaRepository<Country, String> {
	List<Country> findAllByContinent_Id(String continentId);
}
