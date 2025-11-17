package com.example.repartir_backend.dto;

import com.example.repartir_backend.enumerations.Etat;
import com.example.repartir_backend.enumerations.StatutPaiement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponsePaiement {
    private int id;
    private Double montant;
    private String reference;
    private LocalDateTime date;
    private StatutPaiement status;
    private int idJeune;
    private Integer idParrainage;
    private Integer idParrain;  // ID du parrain qui a fait le paiement (récupéré depuis paiement.id_parrain ou parrainage.id_parrain)
    private int idFormation;
}
