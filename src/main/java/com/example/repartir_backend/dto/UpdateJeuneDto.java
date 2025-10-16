package com.example.repartir_backend.dto;

import com.example.repartir_backend.enumerations.Genre;
import lombok.Data;

@Data
public class UpdateJeuneDto {
    private String nom;
    private String prenom;
    private String telephone;
    private int age;
    private String a_propos;
    private String niveau;
    private String urlPhoto;
    private String urlDiplome;
    private Genre genre;
}

