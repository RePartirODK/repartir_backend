package com.example.repartir_backend.dto;

import com.example.repartir_backend.entities.Domaine;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
public class DomaineResponseDto {
    private int id;
    private String libelle;

    public static DomaineResponseDto fromEntity(Domaine domaine) {
        return DomaineResponseDto.builder()
                .id(domaine.getId())
                .libelle(domaine.getLibelle())
                .build();
    }

    public static List<DomaineResponseDto> fromEntities(List<Domaine> domaines) {
        return domaines.stream()
                .map(DomaineResponseDto::fromEntity)
                .collect(Collectors.toList());
    }
}
