package com.example.repartir_backend.dto;

import com.example.repartir_backend.entities.InscriptionFormation;
import com.example.repartir_backend.enumerations.Etat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InscriptionDetailDto {
    private int id;
    private FormationDetailDto formation;
    private Etat statut;
    private Date dateInscription;
    private boolean demandeParrainage;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FormationDetailDto {
        private int id;
        private String titre;
        private String description;
        private CentreInfoDto centre;
        private LocalDateTime date_debut;
        private LocalDateTime date_fin;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CentreInfoDto {
        private int id;
        private String nom;
        private String logo; // urlPhoto
    }

    public static InscriptionDetailDto fromEntity(InscriptionFormation inscription) {
        FormationDetailDto formationDto = FormationDetailDto.builder()
                .id(inscription.getFormation().getId())
                .titre(inscription.getFormation().getTitre())
                .description(inscription.getFormation().getDescription())
                .date_debut(inscription.getFormation().getDate_debut())
                .date_fin(inscription.getFormation().getDate_fin())
                .centre(CentreInfoDto.builder()
                        .id(inscription.getFormation().getCentreFormation().getId())
                        .nom(inscription.getFormation().getCentreFormation().getUtilisateur().getNom())
                        .logo(inscription.getFormation().getCentreFormation().getUtilisateur().getUrlPhoto())
                        .build())
                .build();

        return InscriptionDetailDto.builder()
                .id(inscription.getId())
                .formation(formationDto)
                .statut(inscription.getStatus())
                .dateInscription(inscription.getDateInscription())
                .demandeParrainage(inscription.isDemandeParrainage())
                .build();
    }
}


