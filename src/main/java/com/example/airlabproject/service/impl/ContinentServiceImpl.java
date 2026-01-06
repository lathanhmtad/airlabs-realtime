package com.example.airlabproject.service.impl;

import com.example.airlabproject.dto.ContinentDTO;
import com.example.airlabproject.entity.Continent;
import com.example.airlabproject.repository.ContinentRepository;
import com.example.airlabproject.service.ContinentService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ContinentServiceImpl implements ContinentService {
    private final ContinentRepository continentRepository;

    public ContinentServiceImpl(ContinentRepository continentRepository){
        this.continentRepository = continentRepository;
    }
    @Override
    public ContinentDTO create(ContinentDTO dto){
        Continent continent = new Continent(dto.getId(), dto.getName());
        continentRepository.save(continent);
        return dto;
    }

    @Override
    public List<ContinentDTO> getAll(){
        return continentRepository.findAll()
                .stream()
                .map(c -> new ContinentDTO(c.getId(), c.getName()))
                .collect(Collectors.toList());
    }



}
