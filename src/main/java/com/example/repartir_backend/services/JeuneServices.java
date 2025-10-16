package com.example.repartir_backend.services;


import com.example.repartir_backend.dto.UpdateJeuneDto;
import com.example.repartir_backend.entities.Jeune;
import com.example.repartir_backend.entities.Utilisateur;
import com.example.repartir_backend.repositories.JeuneRepository;
import com.example.repartir_backend.repositories.UtilisateurRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class JeuneServices {
    private final JeuneRepository jeuneRepository;
    private final UtilisateurRepository utilisateurRepository;

    public JeuneServices(JeuneRepository jeuneRepository, UtilisateurRepository utilisateurRepository) {
        this.jeuneRepository = jeuneRepository;
        this.utilisateurRepository = utilisateurRepository;
    }

    @Transactional
    public Jeune updateJeune(UpdateJeuneDto updateDto) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé"));

        Jeune jeune = jeuneRepository.findByUtilisateur(utilisateur)
                .orElseThrow(() -> new EntityNotFoundException("Profil jeune non trouvé"));

        // Mise à jour de l'entité Utilisateur
        utilisateur.setNom(updateDto.getNom());
        utilisateur.setTelephone(updateDto.getTelephone());
        if (updateDto.getUrlPhoto() != null) {
            utilisateur.setUrlPhoto(updateDto.getUrlPhoto());
        }

        // Mise à jour de l'entité Jeune
        jeune.setPrenom(updateDto.getPrenom());
        jeune.setAge(updateDto.getAge());
        jeune.setA_propos(updateDto.getA_propos());
        jeune.setNiveau(updateDto.getNiveau());
        jeune.setGenre(updateDto.getGenre());
        if (updateDto.getUrlDiplome() != null) {
            jeune.setUrlDiplome(updateDto.getUrlDiplome());
        }

        utilisateurRepository.save(utilisateur);
        return jeuneRepository.save(jeune);
    }

    @Transactional
    public void deleteJeune() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé"));

        Jeune jeune = jeuneRepository.findByUtilisateur(utilisateur)
                .orElseThrow(() -> new EntityNotFoundException("Profil jeune non trouvé"));

        jeuneRepository.delete(jeune);
        utilisateurRepository.delete(utilisateur);
    }
}
