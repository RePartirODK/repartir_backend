package com.example.repartir_backend.dto;

import com.example.repartir_backend.enumerations.Etat;
import com.example.repartir_backend.enumerations.Format;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestFormation {
    private String titre;
    private String description;
    private LocalDateTime date_debut;
    private LocalDateTime date_fin;
    private Etat statut;
    private Double cout;
    private Integer nbrePlace;
    private Format format;
    private String duree;
    private String urlFormation;
    private String urlCertificat;
}
