package com.example.repartir_backend.dto;

import com.example.repartir_backend.entities.Entreprise;
import com.example.repartir_backend.enumerations.Etat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EntrepriseResponseDto {
    private int id;
    private String adresse;
    private String agrement;
    private UtilisateurInfoDto utilisateur;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UtilisateurInfoDto {
        private int id;
        private String nom;
        private String email;
        private String telephone;
        private String urlPhoto;
        private Etat etat;
        private boolean estActive;
    }

    public static EntrepriseResponseDto fromEntity(Entreprise entreprise) {
        if (entreprise == null || entreprise.getUtilisateur() == null) {
            return null;
        }

        var utilisateurInfo = UtilisateurInfoDto.builder()
                .id(entreprise.getUtilisateur().getId())
                .nom(entreprise.getUtilisateur().getNom())
                .email(entreprise.getUtilisateur().getEmail())
                .telephone(entreprise.getUtilisateur().getTelephone())
                .urlPhoto(entreprise.getUtilisateur().getUrlPhoto())
                .etat(entreprise.getUtilisateur().getEtat())
                .estActive(entreprise.getUtilisateur().isEstActive())
                .build();

        return EntrepriseResponseDto.builder()
                .id(entreprise.getId())
                .adresse(entreprise.getAdresse())
                .agrement(entreprise.getAgrement())
                .utilisateur(utilisateurInfo)
                .build();
    }

    public static List<EntrepriseResponseDto> fromEntities(List<Entreprise> entreprises) {
        return entreprises.stream()
                .map(EntrepriseResponseDto::fromEntity)
                .filter(dto -> dto != null)
                .collect(Collectors.toList());
    }
}

