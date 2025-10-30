package com.example.repartir_backend.entities;

import com.example.repartir_backend.dto.ResponsePaiement;
import com.example.repartir_backend.enumerations.Etat;
import com.example.repartir_backend.enumerations.StatutPaiement;
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
    //statut du paiement
    @Column
    private StatutPaiement status;

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

    //convertion en dto
    public ResponsePaiement toResponse(){
        return new ResponsePaiement(
                this.id,
                this.montant,
                this.reference,
                this.date,
                this.status,
                this.jeune.getId(),
                this.parrainage != null ? this.parrainage.getId() : null,
                this.inscriptionFormation.getId()
        );
    }
}
