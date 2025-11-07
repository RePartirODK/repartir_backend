package com.example.repartir_backend.services;

import com.example.repartir_backend.dto.RequestFormation;
import com.example.repartir_backend.dto.ResponseFormation;
import com.example.repartir_backend.entities.CentreFormation;
import com.example.repartir_backend.entities.Formation;
import com.example.repartir_backend.entities.Paiement;
import com.example.repartir_backend.enumerations.Etat;
import com.example.repartir_backend.enumerations.StatutPaiement;
import com.example.repartir_backend.repositories.CentreFormationRepository;
import com.example.repartir_backend.repositories.FormationRepository;
import com.example.repartir_backend.repositories.PaiementRepository;
import com.example.repartir_backend.repositories.UtilisateurRepository;
import com.example.repartir_backend.entities.Utilisateur;
import jakarta.mail.MessagingException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FormationServices {
    private final FormationRepository formationRepository;
    private final CentreFormationRepository centreFormationRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final PaiementRepository paiementRepository;
    private final MailSendServices mailSendServices;

    //creation d'une formation
    public Formation createFormation(RequestFormation requestFormation, int centreId) {
        CentreFormation centre = centreFormationRepository.findById(centreId)
                .orElseThrow(() -> new EntityNotFoundException("Centre de formation introuvable"));
        Formation formation = new Formation().toFormation(requestFormation);
        formation.setCentreFormation(centre);
        formation.setStatut(Etat.EN_ATTENTE); // par défaut
        return formationRepository.save(formation);
    }
    //mettre à jour une formation
    // Mettre à jour une formation
    public ResponseFormation updateFormation(int id, RequestFormation requestFormation) {
        Formation formation = formationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Formation non trouvée"));

        if (requestFormation.getTitre() != null)
            formation.setTitre(requestFormation.getTitre());

        if (requestFormation.getDescription() != null)
            formation.setDescription(requestFormation.getDescription());

        if (requestFormation.getDate_debut() != null)
            formation.setDate_debut(requestFormation.getDate_debut());

        if (requestFormation.getDate_fin() != null)
            formation.setDate_fin(requestFormation.getDate_fin());

        if (requestFormation.getCout() != null)
            formation.setCout(requestFormation.getCout());

        if (requestFormation.getNbrePlace() != null)
            formation.setNbre_place(requestFormation.getNbrePlace());

        if (requestFormation.getFormat() != null)
            formation.setFormat(requestFormation.getFormat());

        if (requestFormation.getDuree() != null)
            formation.setDuree(requestFormation.getDuree());

        if (requestFormation.getUrlFormation() != null)
            formation.setUrlFormation(requestFormation.getUrlFormation());

        if (requestFormation.getUrlCertificat() != null)
            formation.setUrlCertificat(requestFormation.getUrlCertificat());

        if (requestFormation.getStatut() != null)
            formation.setStatut(requestFormation.getStatut());

        Formation updatedFormation = formationRepository.save(formation);
        return updatedFormation.toResponse();
    }

    //mettre à jour le statut d'une formation
    public Formation updateStatut(int id, Etat statut) {
        Formation formation = formationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Formation introuvable"));
        formation.setStatut(statut);
        return formationRepository.save(formation);
    }
    //supprimer une formation
    @Transactional
    public void deleteFormation(int idFormation) {
        Formation formation = formationRepository.findById(idFormation)
                .orElseThrow(() -> new EntityNotFoundException("Formation non trouvée"));

        // Étape 1 : récupérer les jeunes et parrains concernés
        List<Paiement> paiements = paiementRepository.findByInscriptionFormation_Formation_Id(idFormation);

        // Étape 2 : marquer les paiements "à rembourser"
        for (Paiement paiement : paiements) {
            paiement.setStatus(StatutPaiement.A_REMBOURSE);
            paiementRepository.save(paiement);

            // Étape 3 : envoyer les mails
            try {
                String path = "templates/remboursement.html"; // modèle d'email
                mailSendServices.envoiMimeMessage(
                        paiement.getParrainage().getParrain().getUtilisateur().getEmail(),
                        "Remboursement suite à annulation de formation",
                        "<p>Bonjour " +
                                paiement.getParrainage().getParrain().getPrenom()
                                + ",</p>" +
                                "<p>La formation <strong>" +
                                formation.getTitre()
                                + "</strong> a été annulée.</p>" +
                                "<p>Vous serez remboursé sous peu.</p>"
                );

                mailSendServices.envoiMimeMessage(
                        paiement.getJeune().getUtilisateur().getEmail(),
                        "Formation annulée",
                        "<p>Bonjour " + paiement.getJeune().getUtilisateur().getNom() + ",</p>" +
                                "<p>La formation <strong>" + formation.getTitre() + "</strong> a été annulée.</p>" +
                                "<p>Le remboursement est en cours.</p>"
                );
            } catch (MessagingException e) {
                throw new RuntimeException("Erreur lors de l'envoi du mail : " + e.getMessage());
            }
        }

        // Étape 4 : suppression de la formation
        formationRepository.delete(formation);
    }


    //recuperer toutes les formations
    public List<ResponseFormation> getAllFormations() {
        return formationRepository.findAll().stream().map(
                Formation::toResponse
        ).toList();
    }

    //recuperer les formations d'un centre
    public List<ResponseFormation> getFormationsByCentre(int centreId) {
        return formationRepository.findByCentreFormationId(centreId).stream()
                .map(Formation::toResponse).collect(Collectors.toList());
    }

    public List<ResponseFormation> getFormationsByCentreEmail(String email) {
        return formationRepository
                .findByCentreFormation_Utilisateur_Email(email).stream()
                .map(Formation::toResponse)
                .collect(Collectors.toList());
    }

    public List<ResponseFormation> getMesFormations() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé."));

        CentreFormation centre = centreFormationRepository.findByUtilisateur(utilisateur)
                .orElseThrow(() -> new EntityNotFoundException("Centre de formation non trouvé."));

        return formationRepository.findByCentreFormationId(centre.getId()).stream()
                .map(Formation::toResponse).collect(Collectors.toList());
    }

    public ResponseFormation getFormationById(int id) {
        return formationRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("formation avec l'id non trouvée: " + id)
        ).toResponse();
    }



    /**
     * Cette méthode s'exécute toutes les x minutes.
     * Elle doit être changée en vingt-quatre heures en prod.
     * Elle met à jour automatiquement le statut des formations.
     */
    @Scheduled(fixedRate = 24 * 60 * 60 * 1000) // toutes les 2 minutes
    public void updateFormationStatus() {
        LocalDateTime now = LocalDateTime.now();
        List<Formation> formations = formationRepository.findAll();

        for (Formation f : formations) {
            Etat nouveauStatut;

            if (now.isBefore(f.getDate_debut())) {
                nouveauStatut = Etat.EN_ATTENTE;
            } else if (now.isAfter(f.getDate_fin())) {
                nouveauStatut = Etat.TERMINE;
            } else {
                nouveauStatut = Etat.EN_COURS;
            }

            // Mise à jour seulement si le statut a changé
            if (f.getStatut() != nouveauStatut) {
                f.setStatut(nouveauStatut);
                formationRepository.save(f);
            }
        }

        System.out.println("✅ Statuts des formations mis à jour à " + now);
    }
}
