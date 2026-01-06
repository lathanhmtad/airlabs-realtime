package com.example.airlabproject.service;

import com.example.airlabproject.dto.ContinentDTO;

import java.util.List;

public interface ContinentService {
    ContinentDTO create(ContinentDTO dto);

    List<ContinentDTO> getAll();
}
