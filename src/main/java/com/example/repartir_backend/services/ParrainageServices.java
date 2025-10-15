package com.example.repartir_backend.services;

import com.example.repartir_backend.dto.ParrainageDto;
import com.example.repartir_backend.dto.ResponseParrainage;
import com.example.repartir_backend.entities.*;
import com.example.repartir_backend.repositories.*;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ParrainageServices {
    private final ParrainRepository parrainRepository;
    private final FormationRepository formationRepository;
    private final JeuneRepository jeuneRepository;
    private final PaiementRepository paiementRepository;
    private final ParrainageRepository parrainageRepository;
    private final InscriptionFormationRepository inscriptionFormationRepository;

    @Transactional
    public ResponseParrainage creerParrainage(int idJeune, int idParrain, int idFormation) {
        Jeune jeune = jeuneRepository.findById(idJeune)
                .orElseThrow(() -> new EntityNotFoundException("Jeune introuvable avec ID : " + idJeune));

        Parrain parrain = parrainRepository.findById(idParrain)
                .orElseThrow(() -> new EntityNotFoundException("Parrain introuvable avec ID : " + idParrain));

        Formation formation = formationRepository.findById(idFormation)
                .orElseThrow(() -> new EntityNotFoundException("Formation introuvable avec ID : " + idFormation));

        // Vérifier qu'un parrainage identique n'existe pas déjà
        boolean exists = parrainageRepository.existsByJeune_IdAndParrain_IdAndFormation_Id(
                idJeune, idParrain, idFormation);
        if (exists) {
            throw new EntityExistsException("Ce parrainage existe déjà pour ce jeune, ce parrain et cette formation.");
        }

        Parrainage parrainage = new Parrainage();
        parrainage.setJeune(jeune);
        parrainage.setParrain(parrain);
        parrainage.setFormation(formation);

        return parrainageRepository.save(parrainage).toResponse();
    }

    //recuperer tous les parrainages
    public List<ResponseParrainage> getAllParrainages() {
        return parrainageRepository.findAll().stream().map(
                Parrainage::toResponse
        ).toList();
    }

    /**
     * Récupérer les parrainages d'un jeune
     */
    public List<Parrainage> getParrainagesByJeune(int idJeune) {
        jeuneRepository.findById(idJeune)
                .orElseThrow(() -> new EntityNotFoundException("Jeune introuvable avec ID : " + idJeune));
        return parrainageRepository.findAllByJeune_Id(idJeune);
    }
    /**
     * Récupérer les parrainages d'un jeune
     */
    public List<ResponseParrainage> getParrainagesByParrain(int idParrain) {
        parrainRepository.findById(idParrain)
                .orElseThrow(() -> new EntityNotFoundException("Jeune introuvable avec ID : " + idParrain));
        return parrainageRepository.findAllByParrain_Id(idParrain).stream().map(
                Parrainage::toResponse
        ).toList();
    }

    /**
     * Récupérer les parrainages pour une formation
     */
    public List<Parrainage> getParrainagesByFormation(int idFormation) {
        formationRepository.findById(idFormation)
                .orElseThrow(() -> new EntityNotFoundException("Formation introuvable avec ID : " + idFormation));
        return parrainageRepository.findAllByFormation_Id(idFormation);
    }

    @Transactional
    public void deleteParrainage(int idParrainage) {
        Parrainage parrainage = parrainageRepository.findById(idParrainage)
                .orElseThrow(() -> new EntityNotFoundException("Parrainage introuvable avec ID : " + idParrainage));
        parrainageRepository.delete(parrainage);
    }

    /**
     * Récupérer les paiements liés à un parrainage
     */
    public List<Paiement> getPaiementsByParrainage(int idParrainage) {
        Parrainage parrainage = parrainageRepository.findById(idParrainage)
                .orElseThrow(() -> new EntityNotFoundException("Parrainage introuvable avec ID : " + idParrainage));
        return paiementRepository.findAllByParrainage_Id(parrainage.getId());
    }

    public List<ParrainageDto> listerDemandes() {
        return ParrainageDto.fromEntities(
            inscriptionFormationRepository.findByDemandeParrainageTrueAndParrainIsNull()
        );
    }

}
