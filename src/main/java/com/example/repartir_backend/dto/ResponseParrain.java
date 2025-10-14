package com.example.repartir_backend.dto;

import com.example.repartir_backend.enumerations.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseParrain {
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private String urlPhoto;
    private Role role;
    private boolean estActive;
    private String profession;
}
