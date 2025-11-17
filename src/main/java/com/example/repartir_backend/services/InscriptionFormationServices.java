package com.example.repartir_backend.services;

import com.example.repartir_backend.dto.RequestPaiement;
import com.example.repartir_backend.entities.Formation;
import com.example.repartir_backend.entities.InscriptionFormation;
import com.example.repartir_backend.entities.Jeune;
import com.example.repartir_backend.entities.Utilisateur;
import com.example.repartir_backend.enumerations.Etat;
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
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import com.example.repartir_backend.dto.InscriptionResponseDto;
import com.example.repartir_backend.dto.InscriptionDetailDto;

@Service
@RequiredArgsConstructor
public class InscriptionFormationServices {
    private final InscriptionFormationRepository inscriptionFormationRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final JeuneRepository jeuneRepository;
    private final FormationRepository formationRepository;
    private final PaiementServices paiementServices;

    @Transactional
    public InscriptionResponseDto sInscrire(int formationId, boolean payerDirectement) {
        Jeune jeune = getCurrentJeune();
        Formation formation = formationRepository.findById(formationId)
                .orElseThrow(() -> new EntityNotFoundException("Formation non trouvée."));

        if (inscriptionFormationRepository.existsByJeuneAndFormation(jeune, formation)) {
            throw new IllegalStateException("Vous êtes déjà inscrit à cette formation.");
        }
        //verifier qu'il reste des places
        if(formation.getNbre_place() <= 0 
        && formation.getNbre_place()!=null)
            throw new IllegalStateException("Il n'y a plus de places disponibles pour cette formation.");
        InscriptionFormation inscription = new InscriptionFormation();
        inscription.setJeune(jeune);
        inscription.setStatus(Etat.EN_ATTENTE);
        inscription.setFormation(formation);
        inscription.setDateInscription(new Date());
        inscription.setDemandeParrainage(false); // Inscription simple, sans demande de parrainage
        InscriptionFormation savedInscription = inscriptionFormationRepository.save(inscription);
        //si le jeune veut payer directement
        if (payerDirectement) {
            RequestPaiement requestPaiement = new RequestPaiement();
            requestPaiement.setIdJeune(jeune.getId());
            requestPaiement.setIdInscription(savedInscription.getId());
            requestPaiement.setMontant(formation.getCout());
            requestPaiement.setIdParrainage(null); // pas de parrainage
            paiementServices.creerPaiement(requestPaiement);
        }
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

    @Transactional(readOnly = true)
    public List<InscriptionDetailDto> getMesInscriptions() {
        Jeune jeune = getCurrentJeune();
        List<InscriptionFormation> inscriptions = inscriptionFormationRepository.findByJeune(jeune);
        return inscriptions.stream()
                .map(InscriptionDetailDto::fromEntity)
                .collect(Collectors.toList());
    }

    private Jeune getCurrentJeune() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé."));

        return jeuneRepository.findByUtilisateur(utilisateur)
                .orElseThrow(() -> new EntityNotFoundException("Profil jeune non trouvé."));
    }

     // New: list inscriptions for a formation
    @Transactional(readOnly = true)
    public List<InscriptionResponseDto> listerParFormation(int formationId) {
        return inscriptionFormationRepository.findAllByFormation_Id(formationId)
                .stream()
                .map(InscriptionResponseDto::fromEntity)
                .toList();
    }

    // New: list inscriptions for all formations of a centre
    @Transactional(readOnly = true)
    public List<InscriptionResponseDto> listerParCentre(int centreId) {
        return inscriptionFormationRepository.findAllByFormation_CentreFormation_Id(centreId)
                .stream()
                .map(InscriptionResponseDto::fromEntity)
                .toList();
    }

    // Certifier une inscription (centre)
    @Transactional
    public InscriptionResponseDto certifierInscription(int inscriptionId) {
        InscriptionFormation inscription = inscriptionFormationRepository.findById(inscriptionId)
                .orElseThrow(() -> new EntityNotFoundException("Inscription non trouvée."));

        // La formation doit être terminée
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime fin = inscription.getFormation().getDate_fin();
        if (fin == null || fin.isAfter(now)) {
            throw new IllegalStateException("La formation n'est pas terminée.");
        }

        // L'inscription doit être validée (paiement suffisant)
        if (inscription.getStatus() != Etat.VALIDE) {
            throw new IllegalStateException("L'inscription doit être validée avant certification.");
        }

        // Appliquer la certification
        inscription.setCertifie(true);
        inscriptionFormationRepository.save(inscription);

        return InscriptionResponseDto.fromEntity(inscription);
    }
}
