package com.example.repartir_backend.services;


import com.example.repartir_backend.repositories.EntrepriseRepository;
import org.springframework.stereotype.Service;

@Service
public class EntrepriseServices {
    EntrepriseRepository entrepriseRepository;
    EntrepriseServices(EntrepriseRepository entrepriseRepository){
        this.entrepriseRepository = entrepriseRepository;
    }
}
