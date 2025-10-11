package com.example.repartir_backend.services;
import com.example.repartir_backend.repositories.CentreFormationRepository;
import org.springframework.stereotype.Service;

@Service
public class CentreFormationServices {
    CentreFormationRepository centreFormationRepository;
    public CentreFormationServices(CentreFormationRepository centreFormationRepository){
        this.centreFormationRepository = centreFormationRepository;
    }
}
