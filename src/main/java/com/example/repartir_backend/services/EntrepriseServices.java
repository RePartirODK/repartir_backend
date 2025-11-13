package com.example.repartir_backend.services;


import com.example.repartir_backend.dto.EntrepriseResponseDto;
import com.example.repartir_backend.entities.Entreprise;
import com.example.repartir_backend.repositories.EntrepriseRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EntrepriseServices {
    EntrepriseRepository entrepriseRepository;
    EntrepriseServices(EntrepriseRepository entrepriseRepository){
        this.entrepriseRepository = entrepriseRepository;
    }

    @Transactional(readOnly = true)
    public List<EntrepriseResponseDto> getAllEntreprises() {
        List<Entreprise> entreprises = entrepriseRepository.findAll();
        return EntrepriseResponseDto.fromEntities(entreprises);
    }

    @Transactional(readOnly = true)
    public Entreprise getEntrepriseByEmail(String email) {
        return entrepriseRepository.findByUtilisateurEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Entreprise non trouvée pour l'email : " + email));
    }
}
