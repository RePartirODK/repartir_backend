package com.example.repartir_backend.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdatePasswordRequest {
    private String ancienMotDePasse;
    private String nouveauMotDePasse;
}
