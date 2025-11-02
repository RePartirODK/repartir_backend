package com.example.repartir_backend.services;


import com.example.repartir_backend.dto.UpdateJeuneDto;
import com.example.repartir_backend.dto.DashboardJeuneDto;
import com.example.repartir_backend.entities.Jeune;
import com.example.repartir_backend.entities.Utilisateur;
import com.example.repartir_backend.entities.InscriptionFormation;
import com.example.repartir_backend.entities.Mentoring;
import com.example.repartir_backend.entities.CandidatureOffre;
import com.example.repartir_backend.entities.OffreEmploi;
import com.example.repartir_backend.enumerations.Etat;
import com.example.repartir_backend.repositories.JeuneRepository;
import com.example.repartir_backend.repositories.UtilisateurRepository;
import com.example.repartir_backend.repositories.InscriptionFormationRepository;
import com.example.repartir_backend.repositories.MentoringRepository;
import com.example.repartir_backend.repositories.CandidatureRepository;
import com.example.repartir_backend.repositories.OffreEmploiRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class JeuneServices {
    private final JeuneRepository jeuneRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final InscriptionFormationRepository inscriptionFormationRepository;
    private final MentoringRepository mentoringRepository;
    private final CandidatureRepository candidatureRepository;
    private final OffreEmploiRepository offreEmploiRepository;

    public JeuneServices(JeuneRepository jeuneRepository, 
                         UtilisateurRepository utilisateurRepository,
                         InscriptionFormationRepository inscriptionFormationRepository,
                         MentoringRepository mentoringRepository,
                         CandidatureRepository candidatureRepository,
                         OffreEmploiRepository offreEmploiRepository) {
        this.jeuneRepository = jeuneRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.inscriptionFormationRepository = inscriptionFormationRepository;
        this.mentoringRepository = mentoringRepository;
        this.candidatureRepository = candidatureRepository;
        this.offreEmploiRepository = offreEmploiRepository;
    }

    @Transactional
    public Jeune updateJeune(UpdateJeuneDto updateDto) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé"));

        Jeune jeune = jeuneRepository.findByUtilisateur(utilisateur)
                .orElseThrow(() -> new EntityNotFoundException("Profil jeune non trouvé"));

        // Mise à jour de l'entité Utilisateur
        utilisateur.setNom(updateDto.getNom());
        utilisateur.setTelephone(updateDto.getTelephone());
        if (updateDto.getUrlPhoto() != null) {
            utilisateur.setUrlPhoto(updateDto.getUrlPhoto());
        }

        // Mise à jour de l'entité Jeune
        jeune.setPrenom(updateDto.getPrenom());
        jeune.setAge(updateDto.getAge());
        jeune.setA_propos(updateDto.getA_propos());
        jeune.setNiveau(updateDto.getNiveau());
        jeune.setGenre(updateDto.getGenre());
        if (updateDto.getUrlDiplome() != null) {
            jeune.setUrlDiplome(updateDto.getUrlDiplome());
        }

        utilisateurRepository.save(utilisateur);
        return jeuneRepository.save(jeune);
    }

    public Jeune getCurrentJeuneProfile() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé"));

        return jeuneRepository.findByUtilisateur(utilisateur)
                .orElseThrow(() -> new EntityNotFoundException("Profil jeune non trouvé"));
    }

    @Transactional
    public void deleteJeune() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé"));

        Jeune jeune = jeuneRepository.findByUtilisateur(utilisateur)
                .orElseThrow(() -> new EntityNotFoundException("Profil jeune non trouvé"));

        jeuneRepository.delete(jeune);
        utilisateurRepository.delete(utilisateur);
    }

    @Transactional(readOnly = true)
    public DashboardJeuneDto getDashboard() {
        Jeune jeune = getCurrentJeuneProfile();

        // Statistiques
        long offresPostulees = candidatureRepository.findByJeune(jeune).size();
        long formationsInscrites = inscriptionFormationRepository.findByJeune(jeune).size();
        long mentorsActifs = mentoringRepository.findAllByJeune_Id(jeune.getId())
                .stream()
                .filter(m -> m.getStatut() == Etat.VALIDE)
                .count();
        long formationsTerminees = inscriptionFormationRepository.findByJeune(jeune)
                .stream()
                .filter(i -> i.getStatus() == Etat.SUPPRIME) // Utiliser le statut approprié pour "terminée"
                .count();

        DashboardJeuneDto.StatistiquesDto stats = DashboardJeuneDto.StatistiquesDto.builder()
                .offresPostulees(offresPostulees)
                .formationsInscrites(formationsInscrites)
                .mentorsActifs(mentorsActifs)
                .formationsTerminees(formationsTerminees)
                .build();

        // Offres récentes (3 dernières)
        List<DashboardJeuneDto.OffreRecentDto> offresRecent = candidatureRepository.findByJeune(jeune)
                .stream()
                .sorted((a, b) -> b.getDate_candidature().compareTo(a.getDate_candidature()))
                .limit(3)
                .map(c -> DashboardJeuneDto.OffreRecentDto.builder()
                        .id(c.getOffreEmploi().getId())
                        .titre(c.getOffreEmploi().getTitre())
                        .entreprise(c.getOffreEmploi().getEntreprise() != null 
                                ? c.getOffreEmploi().getEntreprise().getUtilisateur().getNom() 
                                : "Non spécifié")
                        .datePublication(new java.util.Date(c.getDate_candidature().atZone(
                                java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()))
                        .build())
                .collect(Collectors.toList());

        // Formations récentes (3 dernières)
        List<DashboardJeuneDto.FormationRecentDto> formationsRecent = inscriptionFormationRepository.findByJeune(jeune)
                .stream()
                .sorted((a, b) -> b.getDateInscription().compareTo(a.getDateInscription()))
                .limit(3)
                .map(i -> DashboardJeuneDto.FormationRecentDto.builder()
                        .id(i.getFormation().getId())
                        .titre(i.getFormation().getTitre())
                        .centre(i.getFormation().getCentreFormation().getUtilisateur().getNom())
                        .dateDebut(i.getFormation().getDate_debut())
                        .build())
                .collect(Collectors.toList());

        return DashboardJeuneDto.builder()
                .statistiques(stats)
                .offresRecent(offresRecent)
                .formationsRecent(formationsRecent)
                .build();
    }
}
