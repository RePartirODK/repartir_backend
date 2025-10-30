package com.example.repartir_backend.dto;

/**
 * DTO pour la mise à jour d'un administrateur.
 */
public record UpdateAdminDto(
        String prenom,
        String nom,
        String email,
        String motDePasse
) {
}

