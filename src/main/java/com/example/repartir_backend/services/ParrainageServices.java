package com.example.repartir_backend.services;

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

    private final ParrainageRepository parrainageRepository;
    private final ParrainRepository parrainRepository;
    private final JeuneRepository jeuneRepository;
    private final FormationRepository formationRepository;

    @Transactional
    public ResponseParrainage creerParrainage(int idJeune, Integer idParrain, int idFormation) {
        Jeune jeune = jeuneRepository.findById(idJeune)
                .orElseThrow(() -> new EntityNotFoundException("Jeune introuvable avec ID : " + idJeune));

        Formation formation = formationRepository.findById(idFormation)
                .orElseThrow(() -> new EntityNotFoundException("Formation introuvable avec ID : " + idFormation));

        Parrain parrain = null;
        if (idParrain != null) {
            parrain = parrainRepository.findById(idParrain)
                    .orElseThrow(() -> new EntityNotFoundException("Parrain introuvable avec ID : " + idParrain));
        }

        // Vérifier qu'un parrainage identique n'existe pas déjà
        boolean exists = parrainageRepository.existsByJeune_IdAndParrain_IdAndFormation_Id(
                idJeune, idParrain, idFormation);
        if (exists) {
            throw new EntityExistsException("Ce parrainage existe déjà pour ce jeune, ce parrain et cette formation.");
        }

        Parrainage parrainage = new Parrainage();
        parrainage.setJeune(jeune);
        parrainage.setParrain(parrain); // peut être null
        parrainage.setFormation(formation);

        return parrainageRepository.save(parrainage).toResponse();
    }

    public List<ResponseParrainage> getAllParrainages() {
        return parrainageRepository.findAll().stream()
                .map(Parrainage::toResponse)
                .toList();
    }

    public List<Parrainage> getParrainagesByJeune(int idJeune) {
        jeuneRepository.findById(idJeune)
                .orElseThrow(() -> new EntityNotFoundException("Jeune introuvable avec ID : " + idJeune));
        return parrainageRepository.findAllByJeune_Id(idJeune);
    }

    public List<ResponseParrainage> getParrainagesByParrain(int idParrain) {
        parrainRepository.findById(idParrain)
                .orElseThrow(() -> new EntityNotFoundException("Parrain introuvable avec ID : " + idParrain));
        return parrainageRepository.findAllByParrain_Id(idParrain).stream()
                .map(Parrainage::toResponse)
                .toList();
    }

    public List<Parrainage> getParrainagesByFormation(int idFormation) {
        formationRepository.findById(idFormation)
                .orElseThrow(() -> new EntityNotFoundException("Formation introuvable avec ID : " + idFormation));
        return parrainageRepository.findAllByFormation_Id(idFormation);
    }
    public List<Paiement> getPaiementsByParrainage(int idParrainage) {
        Parrainage parrainage = parrainageRepository.findById(idParrainage)
                .orElseThrow(() -> new EntityNotFoundException("Parrainage introuvable avec l'ID : " + idParrainage));

        // Retourner directement la liste des paiements
        return parrainage.getPaiements();
    }

    @Transactional
    public void deleteParrainage(int idParrainage) {
        Parrainage parrainage = parrainageRepository.findById(idParrainage)
                .orElseThrow(() -> new EntityNotFoundException("Parrainage introuvable avec ID : " + idParrainage));
        parrainageRepository.delete(parrainage);
    }
}
