package com.example.repartir_backend.dto;

import com.example.repartir_backend.enumerations.StatutPaiement;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ResponsePaiementAdmin {
    // Informations du paiement
    private int id;
    private Double montant;
    private String reference;
    private LocalDateTime date;
    private StatutPaiement status;
    private String motifRefus;
    
    // Informations du jeune
    private Integer idJeune;
    private String nomJeune;
    private String prenomJeune;
    private String emailJeune;
    
    // Informations de la formation
    private Integer idFormation;
    private String titreFormation;
    private String descriptionFormation;
    
    // Informations du centre de formation
    private Integer idCentre;
    private String nomCentre;
    
    // Informations du parrainage (nullable)
    private Integer idParrainage;
}


