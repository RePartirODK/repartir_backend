package com.example.repartir_backend.dto;

import com.example.repartir_backend.entities.Admin;
import lombok.Builder;
import lombok.Data;

/**
 * DTO pour la réponse des informations d'un administrateur.
 * Évite les problèmes de LazyInitializationException lors de la sérialisation JSON.
 */
@Data
@Builder
public class AdminResponseDto {
    private int id;
    private String nom;
    private String prenom;
    private String email;
    private String role;

    public static AdminResponseDto fromEntity(Admin admin) {
        return AdminResponseDto.builder()
                .id(admin.getId())
                .nom(admin.getNom())
                .prenom(admin.getPrenom())
                .email(admin.getEmail())
                .role(admin.getRole().name())
                .build();
    }
}
