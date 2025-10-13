package com.example.repartir_backend.dto;

import com.example.repartir_backend.entities.Admin;
import com.example.repartir_backend.enumerations.Role;
import lombok.Builder;

/**
 * DTO (Data Transfer Object) pour la création d'un administrateur.
 * Il transporte les données nécessaires de la requête à la couche de service.
 */
public record AdminDto(
        String prenom,
        String nom,
        String email,
        String motDePasse
) {
}
