package com.example.repartir_backend.entities;

import com.example.repartir_backend.enumerations.Genre;
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
public class Jeune {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(nullable = false)
    private String a_propos;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Genre genre;
    @Column(nullable = false)
    private int age;
    @Column(nullable = false)
    private String prenom;
    private String niveau;
    private String urlDiplome;

    //relation d'heritage
    @OneToOne
    @JoinColumn(name="id_utilisateur", nullable = false)
    private Utilisateur utilisateur;

    //relation jeune et mentoring
    @OneToMany(mappedBy = "jeune")
    private List<Mentoring> mentorings = new ArrayList<>();

    //relation entre jeune et paiement
    @OneToMany(mappedBy = "jeune")
    private List<Paiement> paiements = new ArrayList<>();

    //relation entre jeune et inscription
    @OneToMany(mappedBy = "jeune")
    private List<InscriptionFormation> inscriptionFormations = new ArrayList<>();

    //relation entre jeune et condidature
    @OneToMany(mappedBy = "jeune")
    private List<CandidatureOffre> candidatureOffres = new ArrayList<>();

    //relation entre jeune et parrainage
    @OneToMany(mappedBy = "jeune")
    private List<Parrainage> parrainages = new ArrayList<>();

    //relation entre jeune et message
    @OneToMany(mappedBy = "jeune")
    private List<Message> messages= new ArrayList<>();



}
