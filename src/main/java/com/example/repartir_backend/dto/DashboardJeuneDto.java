package com.example.repartir_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardJeuneDto {
    private StatistiquesDto statistiques;
    private List<OffreRecentDto> offresRecent;
    private List<FormationRecentDto> formationsRecent;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StatistiquesDto {
        private long offresPostulees;
        private long formationsInscrites;
        private long mentorsActifs;
        private long formationsTerminees;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OffreRecentDto {
        private int id;
        private String titre;
        private String entreprise;
        private Date datePublication;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FormationRecentDto {
        private int id;
        private String titre;
        private String centre;
        private LocalDateTime dateDebut;
    }
}

