package com.example.repartir_backend.entities;

import com.example.repartir_backend.enumerations.Etat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InscriptionFormation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "jeune_id", nullable = false)
    private Jeune jeune;

    @ManyToOne
    @JoinColumn(name = "formation_id", nullable = false)
    private Formation formation;

    @OneToMany(mappedBy = "inscriptionFormation", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Paiement> paiements = new ArrayList<>();
    @Enumerated(EnumType.STRING)
    private Etat status;
    private Date dateInscription;

    private boolean demandeParrainage = false; // Par défaut, pas de demande
}
