package com.example.repartir_backend.dto;

import com.example.repartir_backend.enumerations.Etat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseMentoring {
    private int id;
    
    // Informations du jeune
    private String nomJeune;
    private String prenomJeune;
    private int idJeune;
    private String urlPhotoJeune;
    
    // Informations du mentor
    private String nomMentor;
    private String prenomMentor;
    private int idMentor;
    private String specialiteMentor;
    private Integer anneesExperienceMentor;
    private String urlPhotoMentor;
    
    // Informations du mentoring
    private LocalDateTime dateDebut;
    private String objectif;
    private String description;
    private int noteMentor;
    private int noteJeune;
    private String statut;
}
