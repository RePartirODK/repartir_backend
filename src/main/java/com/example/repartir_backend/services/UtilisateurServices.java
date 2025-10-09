package com.example.repartir_backend.services;

import com.example.repartir_backend.repositories.UtilisateurRepository;
import org.springframework.stereotype.Service;

@Service
public class UtilisateurServices {
    UtilisateurRepository utilisateurRepository;
    public UtilisateurServices(UtilisateurRepository utilisateurRepository){
        this.utilisateurRepository = utilisateurRepository;
    }
}
