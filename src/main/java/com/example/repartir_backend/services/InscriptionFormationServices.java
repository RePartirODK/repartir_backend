package com.example.repartir_backend.services;

import com.example.repartir_backend.entities.Formation;
import com.example.repartir_backend.entities.InscriptionFormation;
import com.example.repartir_backend.entities.Jeune;
import com.example.repartir_backend.entities.Utilisateur;
import com.example.repartir_backend.repositories.FormationRepository;
import com.example.repartir_backend.repositories.InscriptionFormationRepository;
import com.example.repartir_backend.repositories.JeuneRepository;
import com.example.repartir_backend.repositories.UtilisateurRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class InscriptionFormationServices {
    private final InscriptionFormationRepository inscriptionFormationRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final JeuneRepository jeuneRepository;
    private final FormationRepository formationRepository;

    public InscriptionFormation demanderParrainage(int formationId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé."));

        Jeune jeune = jeuneRepository.findByUtilisateur(utilisateur)
                .orElseThrow(() -> new EntityNotFoundException("Profil jeune non trouvé."));

        Formation formation = formationRepository.findById(formationId)
                .orElseThrow(() -> new EntityNotFoundException("Formation non trouvée."));

        if (inscriptionFormationRepository.existsByJeuneAndFormation(jeune, formation)) {
            throw new IllegalStateException("Une demande pour cette formation existe déjà.");
        }

        InscriptionFormation inscription = new InscriptionFormation();
        inscription.setJeune(jeune);
        inscription.setFormation(formation);
        inscription.setDateInscription(new Date());
        inscription.setDemandeParrainage(true); // C'est une demande de parrainage

        return inscriptionFormationRepository.save(inscription);
    }
}
