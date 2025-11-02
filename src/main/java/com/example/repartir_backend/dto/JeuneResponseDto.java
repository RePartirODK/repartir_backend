package com.example.repartir_backend.dto;

import com.example.repartir_backend.entities.Jeune;
import com.example.repartir_backend.enumerations.Etat;
import com.example.repartir_backend.enumerations.Genre;
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
public class JeuneResponseDto {
    private int id;
    private String a_propos;
    private Genre genre;
    private int age;
    private String prenom;
    private String niveau;
    private String urlDiplome;
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

    public static JeuneResponseDto fromEntity(Jeune jeune) {
        if (jeune == null || jeune.getUtilisateur() == null) {
            return null;
        }

        var utilisateurInfo = UtilisateurInfoDto.builder()
                .id(jeune.getUtilisateur().getId())
                .nom(jeune.getUtilisateur().getNom())
                .email(jeune.getUtilisateur().getEmail())
                .telephone(jeune.getUtilisateur().getTelephone())
                .urlPhoto(jeune.getUtilisateur().getUrlPhoto())
                .etat(jeune.getUtilisateur().getEtat())
                .estActive(jeune.getUtilisateur().isEstActive())
                .build();

        return JeuneResponseDto.builder()
                .id(jeune.getId())
                .a_propos(jeune.getA_propos())
                .genre(jeune.getGenre())
                .age(jeune.getAge())
                .prenom(jeune.getPrenom())
                .niveau(jeune.getNiveau())
                .urlDiplome(jeune.getUrlDiplome())
                .utilisateur(utilisateurInfo)
                .build();
    }

    public static List<JeuneResponseDto> fromEntities(List<Jeune> jeunes) {
        return jeunes.stream()
                .map(JeuneResponseDto::fromEntity)
                .filter(dto -> dto != null)
                .collect(Collectors.toList());
    }
}

