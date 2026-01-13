package com.example.airlabproject.repository;

import com.example.airlabproject.entity.Continent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContinentRepository extends JpaRepository<Continent, String> {

}
