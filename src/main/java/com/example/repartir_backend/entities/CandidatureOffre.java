package com.example.repartir_backend.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CandidatureOffre {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(nullable = false)
    private LocalDateTime date_candidature;

    @ManyToOne
    @JoinColumn(name = "id_jeune", nullable = false)
    private Jeune jeune;
    @ManyToOne
    @JoinColumn(name = "id_offre", nullable = false)
    private OffreEmploi offreEmploi;
}
