package com.example.repartir_backend.services;

import com.example.repartir_backend.dto.RequestPaiement;
import com.example.repartir_backend.dto.ResponsePaiement;
import com.example.repartir_backend.entities.InscriptionFormation;
import com.example.repartir_backend.entities.Jeune;
import com.example.repartir_backend.entities.Paiement;
import com.example.repartir_backend.entities.Parrainage;
import com.example.repartir_backend.enumerations.Etat;
import com.example.repartir_backend.repositories.InscriptionFormationRepository;
import com.example.repartir_backend.repositories.JeuneRepository;
import com.example.repartir_backend.repositories.PaiementRepository;
import com.example.repartir_backend.repositories.ParrainageRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaiementServices {
    private final PaiementRepository paiementRepository;
    private final InscriptionFormationRepository inscriptionFormationRepository;
    private final ParrainageRepository parrainageRepository;
    private final JeuneRepository jeuneRepository;
    //creer un paiement
    public ResponsePaiement creerPaiement(RequestPaiement paiement){
        //rechercher le Jeune
        Jeune jeune = jeuneRepository.findById(paiement.getIdJeune()).orElseThrow(()->
                new EntityNotFoundException("Jeune non trouvé"));

        //rechercher l'inscription
        InscriptionFormation inscriptionFormation = inscriptionFormationRepository.findById(
                paiement.getIdInscription()
        ).orElseThrow(()-> new EntityNotFoundException("inscription non trouvée"));

        Parrainage parrainage = null;
        if (paiement.getIdParrainage() != null) {
            parrainage = parrainageRepository.findById(paiement.getIdParrainage())
                    .orElseThrow(() -> new EntityNotFoundException("Parrainage introuvable"));
        }
        Paiement nouveauPaiement = new Paiement();
        nouveauPaiement.setJeune(jeune);
        nouveauPaiement.setInscriptionFormation(inscriptionFormation);
        nouveauPaiement.setParrainage(parrainage);
        nouveauPaiement.setMontant(paiement.getMontant());
        nouveauPaiement.setDate(LocalDateTime.now());
        nouveauPaiement.setStatus(Etat.EN_ATTENTE);
        nouveauPaiement.setReference("PAY-" + System.currentTimeMillis());

        return paiementRepository.save(nouveauPaiement).toResponse();
    }

    //valider un paiment
    @Transactional
    public String validerPaiement(int idPaiement) {
        Paiement paiement = paiementRepository.findById(idPaiement)
                .orElseThrow(() -> new EntityNotFoundException("Paiement introuvable"));
        paiement.setStatus(Etat.VALIDE);
        paiementRepository.save(paiement);

        InscriptionFormation inscription = paiement.getInscriptionFormation();
        double totalValide = paiementRepository
                .findByInscriptionFormationAndStatut(inscription, Etat.VALIDE)
                .stream()
                .mapToDouble(Paiement::getMontant)
                .sum();

        double coutFormation = inscription.getFormation().getCout();
        // Dès que le total des paiements validés >= coût de la formation, on valide l’inscription
        if (totalValide >= coutFormation) {
            inscription.setStatut(Etat.VALIDE);
            inscriptionFormationRepository.save(inscription);
        }
        return "Paiement validé avec succès. Total validé = " + totalValide + "/" + coutFormation;
    }

    //refuser un paiement
    @Transactional
    public String refuserPaiement(int idPaiement) {
        Paiement paiement = paiementRepository.findById(idPaiement)
                .orElseThrow(() -> new EntityNotFoundException("Paiement introuvable"));
        paiement.setStatus(Etat.REFUSE);
        //mettre l'etat de l'inscription à refuser
        InscriptionFormation inscriptionFormation = paiement.getInscriptionFormation();
        inscriptionFormation.setStatut(Etat.REFUSE);
        inscriptionFormationRepository.save(inscriptionFormation);
        paiementRepository.save(paiement);
        return "Paiement refusé.";
    }

    //lister les paiements d'une inscription
    public List<ResponsePaiement> getPaiementsParInscription(int idInscription) {
        InscriptionFormation inscription = inscriptionFormationRepository.findById(idInscription)
                .orElseThrow(() -> new EntityNotFoundException("Inscription introuvable"));
        return paiementRepository.findByInscriptionFormation(idInscription).stream()
                .map(Paiement::toResponse).toList();
    }

    //lister les paiements d'un jeune
    public List<ResponsePaiement> getPaiementByJeune(int idJeune){
        return paiementRepository.findByJeuneId(idJeune).stream()
                .map(Paiement::toResponse).toList();
    }

}
