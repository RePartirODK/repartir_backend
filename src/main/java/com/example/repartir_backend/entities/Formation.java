package com.example.repartir_backend.entities;

import com.example.repartir_backend.enumerations.Etat;
import com.example.repartir_backend.enumerations.Format;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Formation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(nullable = false)
    private String description;
    @Column(nullable = false)
    private String titre;
    @Column(nullable = false)
    private LocalDateTime date_debut;
    @Column(nullable = false)
    private LocalDateTime date_fin;
    @Enumerated(EnumType.STRING)
    private Etat statut;
    private Double cout;
    @Column(nullable = false)
    private int nbre_place;
    @Column(nullable = false)
    private Format format;
    @Column(nullable = false)
    private String duree;
    private String urlFormation;
    private String urlCertificat;

    @OneToMany(mappedBy = "formation")
    private List<InscriptionFormation> inscriptionFormationList = new ArrayList<>();
    @OneToMany(mappedBy = "formation")
    private List<Parrainage> parrainages = new ArrayList<>();
    @ManyToOne
    @JoinColumn(name = "id_centreformation", nullable = false)
    private CentreFormation centreFormation;
}
