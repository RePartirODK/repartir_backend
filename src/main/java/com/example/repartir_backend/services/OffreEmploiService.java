package com.example.repartir_backend.services;

import com.example.repartir_backend.dto.OffreEmploiDto;
import com.example.repartir_backend.entities.Entreprise;
import com.example.repartir_backend.entities.OffreEmploi;
import com.example.repartir_backend.repositories.EntrepriseRepository;
import com.example.repartir_backend.repositories.OffreEmploiRepository;
import com.example.repartir_backend.repositories.UtilisateurRepository;
import com.example.repartir_backend.dto.OffreEmploiResponseDto;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class OffreEmploiService {
    private final OffreEmploiRepository offreEmploiRepository;
    private final EntrepriseRepository entrepriseRepository;

    /**
     * Crée une nouvelle offre d'emploi.
     * @param offreDto Les données de l'offre à créer.
     * @return L'offre d'emploi créée.
     */
    public OffreEmploi creerOffre(OffreEmploiDto offreDto) {
        // Récupérer l'entreprise authentifiée à partir du contexte de sécurité
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username;

        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }
        Entreprise entreprise = entrepriseRepository.findByUtilisateurEmail(username)
                .orElseThrow(() -> new AccessDeniedException("L'utilisateur authentifié n'est pas une entreprise valide."));

        // Créer et mapper l'entité OffreEmploi
        OffreEmploi nouvelleOffre = new OffreEmploi();
        nouvelleOffre.setTitre(offreDto.titre());
        nouvelleOffre.setDescription(offreDto.description());
        nouvelleOffre.setCompetence(offreDto.competence());
        nouvelleOffre.setType_contrat(offreDto.type_contrat());
        nouvelleOffre.setLienPostuler(offreDto.lienPostuler());
        nouvelleOffre.setDateDebut(offreDto.dateDebut());
        nouvelleOffre.setDateFin(offreDto.dateFin());
        nouvelleOffre.setEntreprise(entreprise); // Associer l'offre à l'entreprise

        // Sauvegarder l'offre dans la base de données
        return offreEmploiRepository.save(nouvelleOffre);
    }

    /**
     * Récupère toutes les offres d'emploi publiées par l'entreprise authentifiée.
     * @return Une liste des offres d'emploi de l'entreprise.
     */
    public List<OffreEmploi> listerOffresParEntreprise() {
        // Récupérer l'entreprise authentifiée
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username;

        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }
        Entreprise entreprise = entrepriseRepository.findByUtilisateurEmail(username)
                .orElseThrow(() -> new AccessDeniedException("L'utilisateur authentifié n'est pas une entreprise valide."));

        // Utiliser le repository pour trouver les offres par ID d'entreprise
        return offreEmploiRepository.findByEntrepriseId(entreprise.getId());
    }

    public void supprimerOffre(int offreId) {
        // Récupérer l'entreprise authentifiée
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username;

        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }
        Entreprise entreprise = entrepriseRepository.findByUtilisateurEmail(username)
                .orElseThrow(() -> new AccessDeniedException("L'utilisateur authentifié n'est pas une entreprise valide."));

        // Vérifier que l'offre existe et appartient à l'entreprise
        OffreEmploi offre = offreEmploiRepository.findById(offreId)
                .orElseThrow(() -> new RuntimeException("Offre non trouvée."));

        if (offre.getEntreprise().getId() != entreprise.getId()) {
            throw new AccessDeniedException("Vous n'êtes pas autorisé à supprimer cette offre.");
        }

        offreEmploiRepository.delete(offre);
    }

    @Transactional(readOnly = true)
    public List<OffreEmploiResponseDto> listerToutesLesOffres() {
        List<OffreEmploi> offres = offreEmploiRepository.findAll();
        return OffreEmploiResponseDto.fromEntities(offres);
    }

    @Transactional(readOnly = true)
    public OffreEmploiResponseDto getOffreById(int id) {
        OffreEmploi offre = offreEmploiRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Offre non trouvée"));
        return OffreEmploiResponseDto.fromEntity(offre);
    }
}
