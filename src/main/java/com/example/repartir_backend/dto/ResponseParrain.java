package com.example.repartir_backend.dto;

import com.example.repartir_backend.entities.Parrain;
import com.example.repartir_backend.enumerations.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponseParrain {
    private int id;
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private String urlPhoto;
    private Role role;
    private String profession;
    private LocalDateTime dateInscription;
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

    public static ResponseParrain fromEntity(Parrain parrain) {
        if (parrain == null || parrain.getUtilisateur() == null) {
            return null;
        }

        var utilisateurInfo = UtilisateurInfoDto.builder()
                .id(parrain.getUtilisateur().getId())
                .nom(parrain.getUtilisateur().getNom())
                .email(parrain.getUtilisateur().getEmail())
                .telephone(parrain.getUtilisateur().getTelephone())
                .urlPhoto(parrain.getUtilisateur().getUrlPhoto())
                .etat(parrain.getUtilisateur().getEtat())
                .estActive(parrain.getUtilisateur().isEstActive())
                .build();

        return ResponseParrain.builder()
                .id(parrain.getId())
                .nom(parrain.getUtilisateur().getNom())
                .prenom(parrain.getPrenom())
                .email(parrain.getUtilisateur().getEmail())
                .telephone(parrain.getUtilisateur().getTelephone())
                .urlPhoto(parrain.getUtilisateur().getUrlPhoto())
                .role(parrain.getUtilisateur().getRole())
                .profession(parrain.getProfession())
                .dateInscription(parrain.getUtilisateur().getDateCreation())
                .utilisateur(utilisateurInfo)
                .build();
    }

    public static List<ResponseParrain> fromEntities(List<Parrain> parrains) {
        return parrains.stream()
                .map(ResponseParrain::fromEntity)
                .filter(dto -> dto != null)
                .collect(Collectors.toList());
    }
}
