package com.example.repartir_backend.services;

import com.example.repartir_backend.dto.RequestPaiement;
import com.example.repartir_backend.dto.ResponsePaiement;
import com.example.repartir_backend.dto.ResponsePaiementAdmin;
import com.example.repartir_backend.entities.Formation;
import com.example.repartir_backend.entities.InscriptionFormation;
import com.example.repartir_backend.entities.Jeune;
import com.example.repartir_backend.entities.Paiement;
import com.example.repartir_backend.entities.Parrainage;
import com.example.repartir_backend.enumerations.Etat;
import com.example.repartir_backend.enumerations.StatutPaiement;
import com.example.repartir_backend.entities.Parrain;
import com.example.repartir_backend.entities.Utilisateur;
import com.example.repartir_backend.repositories.FormationRepository;
import com.example.repartir_backend.repositories.InscriptionFormationRepository;
import com.example.repartir_backend.repositories.JeuneRepository;
import com.example.repartir_backend.repositories.PaiementRepository;
import com.example.repartir_backend.repositories.ParrainageRepository;
import com.example.repartir_backend.repositories.ParrainRepository;
import com.example.repartir_backend.repositories.UtilisateurRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
@Service
@RequiredArgsConstructor
public class PaiementServices {
    private final PaiementRepository paiementRepository;
    private final InscriptionFormationRepository inscriptionFormationRepository;
    private final ParrainageRepository parrainageRepository;
    private final JeuneRepository jeuneRepository;
    private final ParrainRepository parrainRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final MailSendServices mailSendServices;
    private final FormationRepository formationRepository;

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
            
            // ✅ CORRECTION : Remplir automatiquement id_parrain dans parrainage si ce n'est pas déjà rempli
            if (parrainage.getParrain() == null) {
                // Option 1 : Si idParrain est passé dans la requête
                if (paiementRequest.getIdParrain() != null) {
                    Parrain parrain = parrainRepository.findById(paiementRequest.getIdParrain())
                            .orElseThrow(() -> new EntityNotFoundException("Parrain introuvable avec ID : " + paiementRequest.getIdParrain()));
                    parrainage.setParrain(parrain);
                    parrainageRepository.save(parrainage);
                }
                // Option 2 : Récupérer le parrain authentifié (si c'est un parrain qui fait le paiement)
                else {
                    Optional<Parrain> currentParrain = getCurrentParrain();
                    if (currentParrain.isPresent()) {
                        parrainage.setParrain(currentParrain.get());
                        parrainageRepository.save(parrainage);
                    }
                }
            }
        }

        Paiement paiement = new Paiement();
        paiement.setJeune(jeune);
        paiement.setInscriptionFormation(inscription);
        paiement.setParrainage(parrainage);
        // Pas besoin de mettre id_parrain dans paiement, on récupère via parrainage
        paiement.setMontant(paiementRequest.getMontant());
        paiement.setDate(LocalDateTime.now());
        paiement.setStatus(StatutPaiement.EN_ATTENTE);
        paiement.setReference("PAY-" + System.currentTimeMillis());

        return paiementRepository.save(paiement).toResponse();
    }
    
    /**
     * Récupère le parrain actuellement authentifié (si disponible)
     * @return Optional contenant le parrain si l'utilisateur authentifié est un parrain, sinon Optional.empty()
     */
    private Optional<Parrain> getCurrentParrain() {
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            if (email == null) {
                return Optional.empty();
            }
            
            Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                    .orElse(null);
            
            if (utilisateur == null) {
                return Optional.empty();
            }
            
            return parrainRepository.findByUtilisateur(utilisateur);
        } catch (Exception e) {
            // Si l'utilisateur n'est pas authentifié ou n'est pas un parrain, retourner empty
            return Optional.empty();
        }
    }

    @Transactional
    public String validerPaiement(int idPaiement) throws Exception {
        Paiement paiement = paiementRepository.findById(idPaiement)
                .orElseThrow(() -> new EntityNotFoundException("Paiement introuvable"));

        paiement.setStatus(StatutPaiement.VALIDE);
        paiementRepository.save(paiement);
        
        System.out.println("✅ Paiement #" + idPaiement + " validé et sauvegardé");

        InscriptionFormation inscription = paiement.getInscriptionFormation();
        
        // Préparer les données pour l'email de confirmation de paiement
        String nomComplet = paiement.getJeune().getPrenom() + " " 
                + paiement.getJeune().getUtilisateur().getNom();
        String formationNom = inscription.getFormation().getTitre();
        String montant = String.format("%.0f", paiement.getMontant());
        String emailDestinataire = paiement.getJeune().getUtilisateur().getEmail();
        
        System.out.println("📧 Préparation email de confirmation pour : " + emailDestinataire);
        
        // Envoyer un email de confirmation de paiement validé
        try {
            String pathPaiement = "src/main/resources/templates/inscriptionreussi.html";
            mailSendServices.acceptionInscription(
                    emailDestinataire,
                    "Paiement validé - " + formationNom,
                    nomComplet,
                    formationNom,
                    pathPaiement
            );
            System.out.println("✅ Email de confirmation de paiement envoyé avec succès à " + emailDestinataire);
        } catch (Exception e) {
            System.err.println("❌ ERREUR ENVOI EMAIL CONFIRMATION PAIEMENT : " + e.getMessage());
            e.printStackTrace();
            // Ne pas faire échouer la validation si l'email échoue
        }

        double totalValide = paiementRepository.findByInscriptionFormationAndStatus(inscription, StatutPaiement.VALIDE)
                .stream().mapToDouble(Paiement::getMontant).sum();

        if (totalValide >= inscription.getFormation().getCout()) {
            inscription.setStatus(Etat.VALIDE);
            boolean wasAlreadyValide = inscription.getStatus() == Etat.VALIDE;
            System.out.println("✅ Inscription validée (montant suffisant : " + totalValide + "/" + inscription.getFormation().getCout() + ")");
            
            // Mail de confirmation d'inscription complète
            String pathInscription = "src/main/resources/templates/inscriptionreussi.html";
            try {
                mailSendServices.acceptionInscription(
                        inscription.getJeune().getUtilisateur().getEmail(),
                        "Inscription acceptée",
                        inscription.getJeune().getUtilisateur().getNom(),
                        inscription.getFormation().getTitre(),
                        pathInscription
                );
                System.out.println("✅ Email d'inscription acceptée envoyé");
            } catch (Exception e) {
                System.err.println("❌ ERREUR ENVOI EMAIL INSCRIPTION : " + e.getMessage());
                e.printStackTrace();
            }

            // Décrémenter les places disponibles si ce n'était pas déjà validé
            if (wasAlreadyValide) {
                var formation = inscription.getFormation();
                Integer places = formation.getNbre_place();
                if (places != null && places > 0) {
                    formation.setNbre_place(places - 1);
                    formationRepository.save(formation);
                    System.out.println("Place décrementé de 1");
                } else {
                    System.out.println("⚠️ Aucune place disponible à décrémenter (nbre_place=" + places + ")");
                }
            }

            inscriptionFormationRepository.save(inscription);
        }

        return "Paiement validé. Total payé : " + totalValide + "/" + inscription.getFormation().getCout();
    }

    @Transactional
    public String refuserPaiement(int idPaiement) throws Exception {
        Paiement paiement = paiementRepository.findById(idPaiement)
                .orElseThrow(() -> new EntityNotFoundException("Paiement introuvable"));

        paiement.setStatus(StatutPaiement.REFUSE);
        paiementRepository.save(paiement);
        
        System.out.println("❌ Paiement #" + idPaiement + " refusé et sauvegardé");

        InscriptionFormation inscription = paiement.getInscriptionFormation();
        inscription.setStatus(Etat.REFUSE);
        inscriptionFormationRepository.save(inscription);
        
        String emailDestinataire = inscription.getJeune().getUtilisateur().getEmail();
        System.out.println("📧 Préparation email de refus pour : " + emailDestinataire);

        String path = "src/main/resources/templates/refusreussi.html";
        try {
            mailSendServices.acceptionInscription(
                    emailDestinataire,
                    "Inscription refusée",
                    inscription.getJeune().getUtilisateur().getNom(),
                    inscription.getFormation().getTitre(),
                    path
            );
            System.out.println("✅ Email de refus envoyé avec succès à " + emailDestinataire);
        } catch (Exception e) {
            System.err.println("❌ ERREUR ENVOI EMAIL REFUS : " + e.getMessage());
            e.printStackTrace();
            // Ne pas faire échouer le refus si l'email échoue
        }

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

    /**
     * Récupère tous les paiements avec détails complets (pour l'admin)
     * Utilise une requête optimisée avec JOIN FETCH pour charger parrainage et parrain
     */
    public List<ResponsePaiementAdmin> getAllPaiements() {
        return paiementRepository.findAllWithParrainage().stream()
                .map(Paiement::toAdminResponse)
                .toList();
    }

    public void marquerPaiementsARembourserParFormation(int idFormation) {
        List<Paiement> paiements = paiementRepository.findByInscriptionFormation_Formation_Id(idFormation);
        for (Paiement paiement : paiements) {
            paiement.setStatus(StatutPaiement.A_REMBOURSE);
            paiementRepository.save(paiement);
        }
    }

      public double getTotalDonationsByParrain(int idParrain) {
        double total = 0.0;
        List<Parrainage> parrainages = parrainageRepository.findAllByParrain_Id(idParrain);
        for (Parrainage p : parrainages) {
            List<Paiement> paiements = paiementRepository.findAllByParrainage_Id(p.getId());
            for (Paiement pa : paiements) {
                if (pa.getStatus() == StatutPaiement.VALIDE) {
                    total += pa.getMontant();
                }
            }
        }
        return total;
    }

}
