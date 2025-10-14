package com.example.repartir_backend.dto;

import com.example.repartir_backend.enumerations.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponseCentre {
    private int id;
    private String nom;
    private String adresse;
    private String telephone;
    private String email;
    private String urlPhoto;
    private Role role;
    private boolean estActive;
    private String agrement;
}
