package com.example.repartir_backend.dto;

import com.example.repartir_backend.entities.OffreEmploi;
import com.example.repartir_backend.enumerations.Contrat;
import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
public class OffreEmploiResponseDto {
    private int id;
    private String titre;
    private String description;
    private String competence;
    private Contrat type_contrat;
    private String lienPostuler;
    private Date dateDebut;
    private Date dateFin;
    private String nomEntreprise;
<<<<<<< HEAD
    private String urlPhotoEntreprise;
    private Integer idEntreprise;
=======
    private String adresseEntreprise;
>>>>>>> main

    public static OffreEmploiResponseDto fromEntity(OffreEmploi offreEmploi) {
        String nomEntreprise = (offreEmploi.getEntreprise() != null && offreEmploi.getEntreprise().getUtilisateur() != null)
                ? offreEmploi.getEntreprise().getUtilisateur().getNom()
                : "Non spécifié";
<<<<<<< HEAD
        
        String urlPhotoEntreprise = (offreEmploi.getEntreprise() != null)
                ? offreEmploi.getEntreprise().getUrlPhotoEntreprise()
                : null;
        
        Integer idEntreprise = (offreEmploi.getEntreprise() != null)
                ? offreEmploi.getEntreprise().getId()
=======
        String adresseEntreprise = (offreEmploi.getEntreprise() != null)
                ? offreEmploi.getEntreprise().getAdresse()
>>>>>>> main
                : null;

        return OffreEmploiResponseDto.builder()
                .id(offreEmploi.getId())
                .titre(offreEmploi.getTitre())
                .description(offreEmploi.getDescription())
                .competence(offreEmploi.getCompetence())
                .type_contrat(offreEmploi.getType_contrat())
                .lienPostuler(offreEmploi.getLienPostuler())
                .dateDebut(offreEmploi.getDateDebut())
                .dateFin(offreEmploi.getDateFin())
                .nomEntreprise(nomEntreprise)
<<<<<<< HEAD
                .urlPhotoEntreprise(urlPhotoEntreprise)
                .idEntreprise(idEntreprise)
=======
                .adresseEntreprise(adresseEntreprise)
>>>>>>> main
                .build();
    }

    public static List<OffreEmploiResponseDto> fromEntities(List<OffreEmploi> offres) {
        return offres.stream()
                .map(OffreEmploiResponseDto::fromEntity)
                .collect(Collectors.toList());
    }
}
