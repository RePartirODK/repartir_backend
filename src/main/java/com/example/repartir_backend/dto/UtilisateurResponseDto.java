package com.example.repartir_backend.dto;

import com.example.repartir_backend.enumerations.Etat;
import com.example.repartir_backend.enumerations.Role;

/**
 * DTO pour renvoyer les informations d'un utilisateur de manière sécurisée.
 * Exclut les données sensibles comme le mot de passe et les relations complexes.
 */
public record UtilisateurResponseDto(
        int id,
        String nom,
        String email,
        String telephone,
        Role role,
        Etat etat,
        boolean estActive
) {
}
