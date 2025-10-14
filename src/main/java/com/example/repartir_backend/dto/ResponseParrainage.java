package com.example.repartir_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseParrainage {
    private int id;
    private int idJeune;
    private int idParrain;
    private int idFormation;
}
