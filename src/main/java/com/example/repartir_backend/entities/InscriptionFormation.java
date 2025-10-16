package com.example.repartir_backend.entities;

import com.example.repartir_backend.enumerations.Etat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

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

    @ManyToOne
    @JoinColumn(name = "parrain_id")
    private Parrain parrain;

    private Date dateInscription;

    private boolean demandeParrainage = false; // Par défaut, pas de demande
}
