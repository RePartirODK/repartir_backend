package com.example.repartir_backend.dto;

import com.example.repartir_backend.enumerations.Etat;
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
    private Etat status;
    private int idJeune;
    private Integer idParrainage;
    private int idFormation;
}
