package com.example.repartir_backend.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Entreprise {
    @Id
    private int id;
    @Column(nullable = false)
    private String adresse;
    @Column(nullable = false)
    private String agrement;

    @OneToOne
    @JoinColumn(name = "id_utilisateur", nullable = false)
    private Utilisateur utilisateur;
    @OneToMany(mappedBy = "entreprise")
    private List<OffreEmploi> offreEmploiList = new ArrayList<>();
}
