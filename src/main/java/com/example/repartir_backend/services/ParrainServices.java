package com.example.repartir_backend.services;


import com.example.repartir_backend.dto.RegisterUtilisateur;
import com.example.repartir_backend.dto.ResponseCentre;
import com.example.repartir_backend.dto.ResponseParrain;
import com.example.repartir_backend.entities.CentreFormation;
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


    /**
     * Met à jour les informations d'un centre.
     */
    public ResponseParrain updateCentreV1(RegisterUtilisateur request) {
        // Chercher l'utilisateur lié au centre via l'email
        Utilisateur utilisateur = utilisateurRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé pour l'email : " + request.getEmail()));

        // Mettre à jour les champs modifiables
        utilisateur.setNom(request.getNom());
        utilisateur.setTelephone(request.getTelephone());

        // Pour le mot de passe, soit tu le modifies ici, soit via une page séparée
        // utilisateur.setMotDePasse(request.getMotDePasse());

        // Sauvegarder l'utilisateur
        utilisateurRepository.save(utilisateur);

        // Mettre à jour le centre (adresse, agrément)
        Parrain parrain = parrainRepository.findByUtilisateur(utilisateur)
                .orElseThrow(() -> new EntityNotFoundException("Centre non trouvé pour l'utilisateur : " + utilisateur.getEmail()));

        parrain.setPrenom(request.getPrenom());

        // Sauvegarder le centre
        parrainRepository.save(parrain);

        // Retourner la réponse
        return parrain.toResponse();
    }

    //supprimer le compte d'un parrain
    @Transactional
    public void deleteParrain(int idParrain) {
        Parrain parrain = parrainRepository.findById(idParrain)
                .orElseThrow(() -> new EntityNotFoundException("Parrain introuvable."));
        parrainRepository.delete(parrain);
    }

}

