package com.example.repartir_backend.services;


import com.example.repartir_backend.repositories.ParrainRepository;
import org.springframework.stereotype.Service;

@Service
public class ParrainServices {

    ParrainRepository parrainRepository;
    ParrainServices(ParrainRepository parrainRepository){
        this.parrainRepository = parrainRepository;
    }
}
