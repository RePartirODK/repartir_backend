package com.example.repartir_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MentorUpdateDto {
    private String prenom;
    private String nom;
    private String telephone;
    private String profession;
    private int annee_experience;
    private String a_propos;
}

