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
public class Parrain {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(nullable = false)
    private String prenom;
    @Column(nullable = false)
    private String profession;
    @OneToOne
    @JoinColumn(name = "id_utilisateur")
    private Utilisateur utilisateur;
}
