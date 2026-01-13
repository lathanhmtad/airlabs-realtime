package com.example.airlabproject.repository;

import com.example.airlabproject.entity.FlightSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface FlightScheduleRepository extends JpaRepository<FlightSchedule, Long> {

    // Tìm các chuyến bay của sân bay X, được tạo sau thời gian Y
    List<FlightSchedule> findByDepIataAndCreatedAtAfter(String depIata, LocalDateTime timeThreshold);

    List<FlightSchedule> findByDepIata(String depIata);

    void deleteByDepIata(String depIata);
}