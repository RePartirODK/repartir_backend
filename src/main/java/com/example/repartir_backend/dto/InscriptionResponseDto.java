package com.example.repartir_backend.dto;

import com.example.repartir_backend.entities.InscriptionFormation;
import com.example.repartir_backend.enumerations.Etat;
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
    private boolean certifie;
    //formation id
    private int idFormation;
    //inscrption status
    private Etat formationStatut;
    private Etat status;
    // Add: stable jeune identifier to avoid name matching in frontend
    private int idJeune;
    public static InscriptionResponseDto fromEntity(InscriptionFormation inscription) {
        return InscriptionResponseDto.builder()
                .id(inscription.getId())
                .nomJeune(inscription.getJeune().getPrenom() + " " + inscription.getJeune().getUtilisateur().getNom())
                .titreFormation(inscription.getFormation().getTitre())
                .dateInscription(inscription.getDateInscription())
                .demandeParrainage(inscription.isDemandeParrainage())
                .certifie(inscription.isCertifie())
                .idFormation(inscription.getFormation().getId())
                .formationStatut(inscription.getFormation().getStatut())
                .status(inscription.getStatus())
        .idJeune(inscription.getJeune().getId())
                .build();
    }
}
