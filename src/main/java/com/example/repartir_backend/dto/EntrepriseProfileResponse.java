package com.example.repartir_backend.dto;

import com.example.repartir_backend.entities.Entreprise;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EntrepriseProfileResponse {
    private int id;
    private String nom;
    private String email;
    private String urlPhotoEntreprise;
    private String secteurActivite;
    private String adresse;
    private String telephone;
    private String description;

    public static EntrepriseProfileResponse fromEntity(Entreprise entreprise) {
        if (entreprise == null || entreprise.getUtilisateur() == null) {
            return null;
        }

        return EntrepriseProfileResponse.builder()
                .id(entreprise.getId())
                .nom(entreprise.getUtilisateur().getNom())
                .email(entreprise.getUtilisateur().getEmail())
                .urlPhotoEntreprise(entreprise.getUrlPhotoEntreprise())
                .secteurActivite(entreprise.getSecteurActivite())
                .adresse(entreprise.getAdresse())
                .telephone(entreprise.getUtilisateur().getTelephone())
                .description(entreprise.getDescription())
                .build();
    }
}


