package com.example.repartir_backend.dto;

import com.example.repartir_backend.entities.InscriptionFormation;
import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
public class ParrainageDto {
    private int inscriptionId;
    private String nomJeune;
    private String prenomJeune;
    private String titreFormation;
    private double coutFormation;
    private Date dateDemande;

    public static ParrainageDto fromEntity(InscriptionFormation inscription) {
        return ParrainageDto.builder()
                .inscriptionId(inscription.getId())
                .nomJeune(inscription.getJeune().getUtilisateur().getNom())
                .prenomJeune(inscription.getJeune().getPrenom())
                .titreFormation(inscription.getFormation().getTitre())
                .coutFormation(inscription.getFormation().getCout())
                .dateDemande(inscription.getDateInscription())
                .build();
    }

    public static List<ParrainageDto> fromEntities(List<InscriptionFormation> inscriptions) {
        return inscriptions.stream()
                .map(ParrainageDto::fromEntity)
                .collect(Collectors.toList());
    }
}













