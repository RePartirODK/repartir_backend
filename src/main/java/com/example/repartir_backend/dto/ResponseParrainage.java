package com.example.repartir_backend.dto;

public record ResponseParrainage(
        Integer id,
        Integer idJeune,
        Integer idParrain,
        Integer idFormation
) {}
