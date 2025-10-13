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
public class Mentor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(nullable = false)
    private String prenom;
    @Column(nullable = false)
    private int annee_experience;
    @Column(nullable = false)
    private String a_propos;
    @Column(nullable = false)
    private String profession;

    @OneToOne
    @JoinColumn(name = "id_utilisateur", nullable = false)
    private Utilisateur utilisateur;

    //relation entre mentor et mentoring
    @OneToMany(mappedBy = "mentor")
    private List<Mentoring> mentorings = new ArrayList<>();

    //relation entre mentor et message
    @OneToMany(mappedBy = "mentor")
    private List<Message> messages = new ArrayList<>();


}
