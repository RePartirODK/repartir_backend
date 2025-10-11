package com.example.repartir_backend.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Parrainage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "id_jeune", nullable = false)
    private Jeune jeune;
    @ManyToOne
    @JoinColumn(name = "id_parrain", nullable = false)
    private Parrain parrain;

    @OneToMany(mappedBy = "parrainage")
    private List<Paiement> paiements;
    @ManyToOne
    @JoinColumn(name = "id_formation", nullable = false)
    private Formation formation;
}
