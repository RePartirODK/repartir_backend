package com.example.repartir_backend.dto;

import com.example.repartir_backend.enumerations.Contrat;

import java.util.Date;

/**
 * DTO pour la création d'une offre d'emploi.
 * Il contient toutes les informations nécessaires fournies par l'entreprise.
 */
public record OffreEmploiDto(
        String titre,
        String description,
        String competence,
        Contrat type_contrat,
        String lienPostuler,
        Date dateDebut,
        Date dateFin
) {
}
