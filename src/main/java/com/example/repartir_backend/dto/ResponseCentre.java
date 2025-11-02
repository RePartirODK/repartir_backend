package com.example.repartir_backend.dto;

import com.example.repartir_backend.entities.CentreFormation;
import com.example.repartir_backend.enumerations.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

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
    private String agrement;
    private UtilisateurInfoDto utilisateur; // Objet utilisateur imbriqué

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
        private com.example.repartir_backend.enumerations.Etat etat;
        private boolean estActive;
    }

    public static ResponseCentre fromEntity(CentreFormation centre) {
        if (centre == null || centre.getUtilisateur() == null) {
            return null;
        }

        var utilisateurInfo = UtilisateurInfoDto.builder()
                .id(centre.getUtilisateur().getId())
                .nom(centre.getUtilisateur().getNom())
                .email(centre.getUtilisateur().getEmail())
                .telephone(centre.getUtilisateur().getTelephone())
                .urlPhoto(centre.getUtilisateur().getUrlPhoto())
                .etat(centre.getUtilisateur().getEtat())
                .estActive(centre.getUtilisateur().isEstActive())
                .build();

        return ResponseCentre.builder()
                .id(centre.getId())
                .nom(centre.getUtilisateur().getNom())
                .adresse(centre.getAdresse())
                .telephone(centre.getUtilisateur().getTelephone())
                .email(centre.getUtilisateur().getEmail())
                .urlPhoto(centre.getUtilisateur().getUrlPhoto())
                .role(centre.getUtilisateur().getRole())
                .agrement(centre.getAgrement())
                .utilisateur(utilisateurInfo)
                .build();
    }

    public static List<ResponseCentre> fromEntities(List<CentreFormation> centres) {
        return centres.stream()
                .map(ResponseCentre::fromEntity)
                .filter(dto -> dto != null)
                .collect(Collectors.toList());
    }
}
