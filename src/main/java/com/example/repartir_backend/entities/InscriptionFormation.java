package com.example.repartir_backend.entities;

import com.example.repartir_backend.enumerations.Etat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class InscriptionFormation {
    @Id
    private int id;
    private LocalDate date;
    @Enumerated(EnumType.STRING)
    private Etat statut;

    @ManyToOne
    @JoinColumn(name = "id_jeune")
    private Jeune jeune;

    @OneToMany(mappedBy = "inscriptionFormation")
    private List<Paiement> paiements;
    @ManyToOne
    @JoinColumn(name = "id_formation")
    private Formation formation;
}
