package com.example.repartir_backend.dto;

import com.example.repartir_backend.entities.InscriptionFormation;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class InscriptionResponseDto {
    private int id;
    private String nomJeune;
    private String titreFormation;
    private Date dateInscription;
    private boolean demandeParrainage;

    public static InscriptionResponseDto fromEntity(InscriptionFormation inscription) {
        return InscriptionResponseDto.builder()
                .id(inscription.getId())
                .nomJeune(inscription.getJeune().getPrenom() + " " + inscription.getJeune().getUtilisateur().getNom())
                .titreFormation(inscription.getFormation().getTitre())
                .dateInscription(inscription.getDateInscription())
                .demandeParrainage(inscription.isDemandeParrainage())
                .build();
    }
}
