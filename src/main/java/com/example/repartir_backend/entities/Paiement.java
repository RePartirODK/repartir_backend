package com.example.repartir_backend.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Paiement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(nullable = false)
    private Double montant;
    @Column(nullable = false)
    private String reference;
    //private statut: enum;
    @Column(nullable = false)
    private LocalDateTime date;

    @ManyToOne
    @JoinColumn(name = "id_jeune")
    private Jeune jeune;
    @ManyToOne
    @JoinColumn(name = "id_parrainage")
    private Parrainage parrainage;
    @ManyToOne
    @JoinColumn(name = "id_inscriptionFormation", nullable = false)
    private InscriptionFormation inscriptionFormation;
}
