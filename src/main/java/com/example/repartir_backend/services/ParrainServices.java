package com.example.repartir_backend.services;


import com.example.repartir_backend.dto.ResponseParrain;
import com.example.repartir_backend.entities.Parrain;
import com.example.repartir_backend.entities.Utilisateur;
import com.example.repartir_backend.repositories.ParrainRepository;
import com.example.repartir_backend.repositories.UtilisateurRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ParrainServices {

    ParrainRepository parrainRepository;
    UtilisateurRepository utilisateurRepository;
    ParrainServices(ParrainRepository parrainRepository,
                    UtilisateurRepository utilisateurRepository){
        this.parrainRepository = parrainRepository;
        this.utilisateurRepository = utilisateurRepository;
    }
    @Transactional(readOnly = true)
    public ResponseParrain getParrainById(int id) {
        return parrainRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Parrain introuvable avec l'ID : " + id))
                .toResponse();
    }
    
    @Transactional(readOnly = true)
    public List<ResponseParrain> getAllParrains() {
        return parrainRepository.findAll()
                .stream()
                .map(Parrain::toResponse)
                .toList();
    }

    //recuperer les parrains actifs
    @Transactional(readOnly = true)
    public List<ResponseParrain> getParrainsActifs() {
        return parrainRepository.findAll()
                .stream()
                .filter(p -> p.getUtilisateur() != null && p.getUtilisateur().isEstActive())
                .map(Parrain::toResponse)
                .toList();
    }
    //recuperer parrain par son email
    public ResponseParrain getParrainByEmail(String email) {
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Aucun utilisateur avec cet email."));
        Parrain parrain = parrainRepository.findByUtilisateur_Id(utilisateur.getId())
                .orElseThrow(() -> new EntityNotFoundException("Aucun parrain associé à cet utilisateur."));
        return parrain.toResponse();
    }

    //mettre à jour les informations d'un parrain
    @Transactional
    //mettre en paramètre un requestparrain
    public ResponseParrain updateParrain(int id, Parrain parrainDetails) {
        Parrain existing = parrainRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Parrain introuvable."));

        existing.setPrenom(parrainDetails.getPrenom());
        existing.setProfession(parrainDetails.getProfession());

        return parrainRepository.save(existing).toResponse();
    }

    //supprimer le compte d'un parrain
    @Transactional
    public void deleteParrain(int idParrain) {
        Parrain parrain = parrainRepository.findById(idParrain)
                .orElseThrow(() -> new EntityNotFoundException("Parrain introuvable."));
        parrainRepository.delete(parrain);
    }

}

