package com.example.repartir_backend.dto;

import com.example.repartir_backend.enumerations.Genre;
import com.example.repartir_backend.enumerations.Role;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterUtilisateur {
    private String nom;
    private String email;
    private String telephone;
    private String urlPhoto;
    private String motDePasse;
    private Role role;
    private boolean estActive;

    //champs jeune
    private String a_propos;
    private Genre genre;
    private int age;
    private String prenom;
    private String niveau;
    private String urlDiplome;

    //champs mentor
    //private String prenom;
    private int annee_experience;
   // private String a_propos;
    private String profession;

    //champs parrain
    //private String prenom;
   // private String profession;

    //champs centre
    private String adresse;
    private String agrement;

    //champs entreprise
    //private String adresse;
    //private String agrement;

}
