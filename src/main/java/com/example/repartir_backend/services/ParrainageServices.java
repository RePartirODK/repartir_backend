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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ParrainageServices {

    private final ParrainageRepository parrainageRepository;
    private final ParrainRepository parrainRepository;
    private final FormationRepository formationRepository;
    private final JeuneRepository jeuneRepository;
    private final PaiementRepository paiementRepository;
    private final InscriptionFormationRepository inscriptionFormationRepository;
    private final NotificationService notificationService;

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
        boolean exists;
        if (parrain == null) {
            exists = parrainageRepository.existsByJeune_IdAndParrainIsNullAndFormation_Id(idJeune, idFormation);
        } else {
            exists = parrainageRepository.existsByJeune_IdAndParrain_IdAndFormation_Id(idJeune, idParrain, idFormation);
        }
        if (exists) {
            throw new EntityExistsException("Ce parrainage existe déjà pour ce jeune, ce parrain et cette formation.");
        }

        Parrainage parrainage = new Parrainage();
        parrainage.setJeune(jeune);
        parrainage.setParrain(parrain); // peut être null
        parrainage.setFormation(formation);

        // Étape 1 : Sauvegarder l'entité. L'objet retourné n'est pas forcément complet.
        Parrainage parrainageSauvegarde = parrainageRepository.save(parrainage);

        // Étape 2 : Recharger l'entité depuis la base de données par son ID pour garantir que toutes les relations sont chargées.
        Parrainage parrainageComplet = parrainageRepository.findById(parrainageSauvegarde.getId())
                .orElseThrow(() -> new EntityNotFoundException("Erreur lors de la récupération du parrainage après création."));

        // Étape 3 : Convertir l'entité complète et renvoyer la réponse.
        return parrainageComplet.toResponse();
    }

    /**
     * Permet à un parrain d'accepter une demande de parrainage existante.
     * La méthode vérifie d'abord que la demande n'a pas déjà été acceptée.
     * Après avoir assigné le parrain, elle envoie une notification au jeune concerné.
     * @param idParrainage L'ID de la demande de parrainage (qui est un Parrainage sans parrain assigné).
     * @param idParrain L'ID du parrain qui accepte la demande.
     * @return Le parrainage mis à jour avec le parrain assigné.
     */
    @Transactional
    public ResponseParrainage accepterDemande(int idParrainage, int idParrain) {
        Parrainage demande = parrainageRepository.findById(idParrainage)
                .orElseThrow(() -> new EntityNotFoundException("Demande de parrainage introuvable avec ID : " + idParrainage));

        if (demande.getParrain() != null) {
            throw new IllegalStateException("Cette demande a déjà été acceptée par un autre parrain.");
        }

        Parrain parrain = parrainRepository.findById(idParrain)
                .orElseThrow(() -> new EntityNotFoundException("Parrain introuvable avec ID : " + idParrain));

        demande.setParrain(parrain);
        Parrainage parrainageAccepte = parrainageRepository.save(demande);

        // Notifier le jeune que sa demande a été acceptée.
        String message = "Bonne nouvelle ! Le parrain '" + parrain.getPrenom() + " " + parrain.getUtilisateur().getNom() + "' a accepté de vous accompagner pour la formation '" + demande.getFormation().getTitre() + "'.";
        notificationService.notifierUtilisateur(demande.getJeune().getUtilisateur(), message);

        return parrainageAccepte.toResponse();
    }

    public List<ResponseParrainage> getAllParrainages() {
        return parrainageRepository.findAll().stream()
                .map(Parrainage::toResponse)
                .toList();
    }

    /**
     * Récupérer les parrainages d'un jeune
     */
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
    public List<Paiement> getPaiementsByParrainage2(int idParrainage) {
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

    /**
     * Récupérer les paiements liés à un parrainage
     */
    public List<Paiement> getPaiementsByParrainage(int idParrainage) {
        Parrainage parrainage = parrainageRepository.findById(idParrainage)
                .orElseThrow(() -> new EntityNotFoundException("Parrainage introuvable avec ID : " + idParrainage));
        return paiementRepository.findAllByParrainage_Id(parrainage.getId());
    }

    public List<ResponseParrainage> listerDemandes() {
        return parrainageRepository.findByParrainIsNull().stream()
                .map(Parrainage::toResponse)
                .collect(Collectors.toList());
    }

}
