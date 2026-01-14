package com.example.airlabproject.repository;

import com.example.airlabproject.entity.Airline;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AirlineRepository extends JpaRepository<Airline, Long> {

    Optional<Airline> findByIataCode(String iataCode);

    @Modifying
    @Transactional
    @Query(value =
            """
            INSERT INTO airline (iata_code, icao_code, name)
            VALUES (:iataCode, :icaoCode, :name)
            ON DUPLICATE KEY UPDATE
                name = :name,
                icao_code = :icaoCode
            """, nativeQuery = true)
    void upsertAirline(
            @Param("iataCode") String iataCode,
            @Param("icaoCode") String icaoCode,
            @Param("name") String name
    );
}