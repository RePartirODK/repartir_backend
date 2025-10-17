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
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.util.Date;
import com.example.repartir_backend.dto.InscriptionResponseDto;

@Service
@RequiredArgsConstructor
public class InscriptionFormationServices {
    private final InscriptionFormationRepository inscriptionFormationRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final JeuneRepository jeuneRepository;
    private final FormationRepository formationRepository;

    @Transactional
    public InscriptionResponseDto sInscrire(int formationId) {
        Jeune jeune = getCurrentJeune();
        Formation formation = formationRepository.findById(formationId)
                .orElseThrow(() -> new EntityNotFoundException("Formation non trouvée."));

        if (inscriptionFormationRepository.existsByJeuneAndFormation(jeune, formation)) {
            throw new IllegalStateException("Vous êtes déjà inscrit à cette formation.");
        }

        InscriptionFormation inscription = new InscriptionFormation();
        inscription.setJeune(jeune);
        inscription.setFormation(formation);
        inscription.setDateInscription(new Date());
        inscription.setDemandeParrainage(false); // Inscription simple, sans demande de parrainage

        InscriptionFormation savedInscription = inscriptionFormationRepository.save(inscription);
        return InscriptionResponseDto.fromEntity(savedInscription);
    }

    @Transactional
    public InscriptionResponseDto activerDemandeParrainage(int inscriptionId) throws AccessDeniedException {
        Jeune jeune = getCurrentJeune();
        InscriptionFormation inscription = inscriptionFormationRepository.findById(inscriptionId)
                .orElseThrow(() -> new EntityNotFoundException("Inscription non trouvée."));

        // Sécurité : Vérifier que l'inscription appartient bien au jeune connecté
        if (inscription.getJeune().getId() != jeune.getId()) {
            throw new AccessDeniedException("Vous n'êtes pas autorisé à modifier cette inscription.");
        }

        if (inscription.isDemandeParrainage()) {
            throw new IllegalStateException("Une demande de parrainage a déjà été faite pour cette inscription.");
        }

        inscription.setDemandeParrainage(true);
        InscriptionFormation updatedInscription = inscriptionFormationRepository.save(inscription);
        return InscriptionResponseDto.fromEntity(updatedInscription);
    }

    private Jeune getCurrentJeune() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé."));

        return jeuneRepository.findByUtilisateur(utilisateur)
                .orElseThrow(() -> new EntityNotFoundException("Profil jeune non trouvé."));
    }
}
