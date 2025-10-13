package com.example.repartir_backend.entities;

import com.example.repartir_backend.enumerations.Contrat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class OffreEmploi {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(nullable = false)
    private String titre;
    @Column(nullable = false)
    private String description;
    private String competence;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Contrat type_contrat;
    @Column(nullable = false)
    private String lienPostuler;
    private Date dateDebut;
    private Date dateFin;

    @OneToMany(mappedBy = "offreEmploi")
    private List<CandidatureOffre> candidatureOffreList = new ArrayList<>();
    @ManyToOne
    @JoinColumn(name = "id_offreEmploi")
    private Entreprise entreprise;
}
