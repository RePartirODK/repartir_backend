package com.example.repartir_backend.services;


import com.example.repartir_backend.dto.JeuneResponseDto;
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

import java.util.Date;
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
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé"));

        Jeune jeune = jeuneRepository.findByUtilisateur(utilisateur)
                .orElseThrow(() -> new EntityNotFoundException("Profil jeune non trouvé"));

        // Statistiques
        List<CandidatureOffre> candidatures = candidatureRepository.findByJeune(jeune);
        List<InscriptionFormation> inscriptions = inscriptionFormationRepository.findByJeune(jeune);
        List<Mentoring> mentorings = mentoringRepository.findAllByJeune_Id(jeune.getId());
        
        long offresPostulees = candidatures.size();
        long formationsInscrites = inscriptions.size();
        long mentorsActifs = mentorings.stream()
                .filter(m -> m.getStatut() == Etat.VALIDE)
                .count();
        long formationsTerminees = inscriptions.stream()
                .filter(ins -> ins.getFormation() != null && 
                        ins.getFormation().getDate_fin().isBefore(java.time.LocalDateTime.now()))
                .count();

        DashboardJeuneDto.StatistiquesDto statistiques = DashboardJeuneDto.StatistiquesDto.builder()
                .offresPostulees(offresPostulees)
                .formationsInscrites(formationsInscrites)
                .mentorsActifs(mentorsActifs)
                .formationsTerminees(formationsTerminees)
                .build();

        // Offres récentes (les 5 plus récentes)
        List<OffreEmploi> offresRecentes = offreEmploiRepository.findAll().stream()
                .sorted((o1, o2) -> {
                    Date date1 = o1.getDateDebut() != null ? o1.getDateDebut() : new Date(0);
                    Date date2 = o2.getDateDebut() != null ? o2.getDateDebut() : new Date(0);
                    return date2.compareTo(date1);
                })
                .limit(5)
                .collect(Collectors.toList());

        List<DashboardJeuneDto.OffreRecentDto> offresRecentDto = offresRecentes.stream()
                .map(offre -> DashboardJeuneDto.OffreRecentDto.builder()
                        .id(offre.getId())
                        .titre(offre.getTitre())
                        .entreprise(offre.getEntreprise() != null && offre.getEntreprise().getUtilisateur() != null ? 
                                offre.getEntreprise().getUtilisateur().getNom() : "N/A")
                        .datePublication(offre.getDateDebut())
                        .build())
                .collect(Collectors.toList());

        // Formations récentes (les 5 inscriptions les plus récentes)
        List<InscriptionFormation> inscriptionsRecentes = inscriptions.stream()
                .sorted((i1, i2) -> {
                    Date date1 = i1.getDateInscription() != null ? i1.getDateInscription() : new Date(0);
                    Date date2 = i2.getDateInscription() != null ? i2.getDateInscription() : new Date(0);
                    return date2.compareTo(date1);
                })
                .limit(5)
                .collect(Collectors.toList());

        List<DashboardJeuneDto.FormationRecentDto> formationsRecentDto = inscriptionsRecentes.stream()
                .map(inscription -> DashboardJeuneDto.FormationRecentDto.builder()
                        .id(inscription.getFormation().getId())
                        .titre(inscription.getFormation().getTitre())
                        .centre(inscription.getFormation().getCentreFormation() != null && 
                                inscription.getFormation().getCentreFormation().getUtilisateur() != null ? 
                                inscription.getFormation().getCentreFormation().getUtilisateur().getNom() : "N/A")
                        .dateDebut(inscription.getFormation().getDate_debut())
                        .build())
                .collect(Collectors.toList());

        return DashboardJeuneDto.builder()
                .statistiques(statistiques)
                .offresRecent(offresRecentDto)
                .formationsRecent(formationsRecentDto)
                .build();
    }

    @Transactional(readOnly = true)
    public List<JeuneResponseDto> getAllJeunes() {
        List<Jeune> jeunes = jeuneRepository.findAll();
        return JeuneResponseDto.fromEntities(jeunes);
    }
}
