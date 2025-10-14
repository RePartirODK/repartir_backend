package com.example.repartir_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseMentoring {
    private String nomJeune;
    private String prenomJeune;
    private String nomMentor;
    private String prenomMentor;
    private LocalDateTime dateDebut;
    private String objectif;
    private String description;
    private int noteMentor;
    private int noteJeune;
}
