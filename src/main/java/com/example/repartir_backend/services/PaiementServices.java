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
import jakarta.mail.MessagingException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
@Service
@RequiredArgsConstructor
public class PaiementServices {
    private final PaiementRepository paiementRepository;
    private final InscriptionFormationRepository inscriptionFormationRepository;
    private final ParrainageRepository parrainageRepository;
    private final JeuneRepository jeuneRepository;
    private final MailSendServices mailSendServices;

    @Transactional
    public ResponsePaiement creerPaiement(RequestPaiement paiementRequest) {
        Jeune jeune = jeuneRepository.findById(paiementRequest.getIdJeune())
                .orElseThrow(() -> new EntityNotFoundException("Jeune non trouvé"));

        InscriptionFormation inscription = inscriptionFormationRepository.findById(paiementRequest.getIdInscription())
                .orElseThrow(() -> new EntityNotFoundException("Inscription non trouvée"));

        Parrainage parrainage = null;
        if (paiementRequest.getIdParrainage() != null) {
            parrainage = parrainageRepository.findById(paiementRequest.getIdParrainage())
                    .orElseThrow(() -> new EntityNotFoundException("Parrainage introuvable"));
        }

        Paiement paiement = new Paiement();
        paiement.setJeune(jeune);
        paiement.setInscriptionFormation(inscription);
        paiement.setParrainage(parrainage);
        paiement.setMontant(paiementRequest.getMontant());
        paiement.setDate(LocalDateTime.now());
        paiement.setStatus(Etat.EN_ATTENTE);
        paiement.setReference("PAY-" + System.currentTimeMillis());

        return paiementRepository.save(paiement).toResponse();
    }

    @Transactional
    public String validerPaiement(int idPaiement) throws Exception {
        Paiement paiement = paiementRepository.findById(idPaiement)
                .orElseThrow(() -> new EntityNotFoundException("Paiement introuvable"));

        paiement.setStatus(Etat.VALIDE);
        paiementRepository.save(paiement);

        InscriptionFormation inscription = paiement.getInscriptionFormation();

        double totalValide = paiementRepository.findByInscriptionFormationAndStatus(inscription, Etat.VALIDE)
                .stream().mapToDouble(Paiement::getMontant).sum();

        if (totalValide >= inscription.getFormation().getCout()) {
            inscription.setStatus(Etat.VALIDE);

            // Mail de confirmation
            String path = "src/main/resources/templates/inscriptionreussi.html";
            mailSendServices.acceptionInscription(
                    inscription.getJeune().getUtilisateur().getEmail(),
                    "Inscription acceptée",
                    inscription.getJeune().getUtilisateur().getNom(),
                    inscription.getFormation().getTitre(),
                    path
            );

            inscriptionFormationRepository.save(inscription);
        }

        return "Paiement validé. Total payé : " + totalValide + "/" + inscription.getFormation().getCout();
    }

    @Transactional
    public String refuserPaiement(int idPaiement) throws Exception {
        Paiement paiement = paiementRepository.findById(idPaiement)
                .orElseThrow(() -> new EntityNotFoundException("Paiement introuvable"));

        paiement.setStatus(Etat.REFUSE);
        paiementRepository.save(paiement);

        InscriptionFormation inscription = paiement.getInscriptionFormation();
        inscription.setStatus(Etat.REFUSE);
        inscriptionFormationRepository.save(inscription);

        String path = "src/main/resources/templates/refusreussi.html";
        mailSendServices.acceptionInscription(
                inscription.getJeune().getUtilisateur().getEmail(),
                "Inscription refusée",
                inscription.getJeune().getUtilisateur().getNom(),
                inscription.getFormation().getTitre(),
                path
        );

        return "Paiement refusé.";
    }

    public List<ResponsePaiement> getPaiementsParInscription(int idInscription) {
        return paiementRepository.findByInscriptionFormationId(idInscription).stream()
                .map(Paiement::toResponse).toList();
    }

    public List<ResponsePaiement> getPaiementByJeune(int idJeune) {
        return paiementRepository.findByJeuneId(idJeune).stream()
                .map(Paiement::toResponse).toList();
    }
}
