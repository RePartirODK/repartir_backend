package com.example.repartir_backend.dto;

import lombok.Data;

@Data
public class RequestParrainageDto {
    private int idJeune;
    private Integer idParrain; // Optionnel, car la demande peut être ouverte
    private int idFormation;
}
