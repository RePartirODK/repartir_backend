package com.example.repartir_backend.entities;

import com.example.repartir_backend.dto.ResponseParrainage;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
    @JoinColumn(name = "id_parrain")
    private Parrain parrain;

    @OneToMany(mappedBy = "parrainage", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Paiement> paiements;
    @ManyToOne
    @JoinColumn(name = "id_formation", nullable = false)
    private Formation formation;

    public ResponseParrainage toResponse(){
        return new ResponseParrainage(
                this.id,
                this.jeune != null ? this.jeune.getId() : null,
                this.parrain != null ? this.parrain.getId() : null,
                this.formation != null ? this.formation.getId() : null
        );
    }
}
