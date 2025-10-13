package com.example.repartir_backend.services;

import com.example.repartir_backend.dto.OffreEmploiDto;
import com.example.repartir_backend.entities.Entreprise;
import com.example.repartir_backend.entities.OffreEmploi;
import com.example.repartir_backend.repositories.OffreEmploiRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OffreEmploiService {
    private final OffreEmploiRepository offreEmploiRepository;

    /**
     * Crée une nouvelle offre d'emploi.
     * @param offreDto Les données de l'offre à créer.
     * @return L'offre d'emploi créée.
     */
    public OffreEmploi creerOffre(OffreEmploiDto offreDto) {
        // Récupérer l'entreprise authentifiée à partir du contexte de sécurité
        Entreprise entreprise = (Entreprise) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

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
        Entreprise entreprise = (Entreprise) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // Utiliser le repository pour trouver les offres par ID d'entreprise
        return offreEmploiRepository.findByEntrepriseId(entreprise.getId());
    }
}
