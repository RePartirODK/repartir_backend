package com.example.repartir_backend.services;

import com.example.repartir_backend.repositories.JeuneRepository;
import org.springframework.stereotype.Service;

@Service
public class JeuneServices {
    JeuneRepository jeuneRepository;
    public JeuneServices(JeuneRepository jeuneRepository){
        this.jeuneRepository = jeuneRepository;
    }
}
