package com.example.repartir_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestPaiement {
    private int idJeune;
    private int idInscription;
    private Double montant;
    private Integer idParrainage;
}
