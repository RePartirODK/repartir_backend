package com.example.repartir_backend.entities;

import com.example.repartir_backend.enumerations.Etat;
import com.example.repartir_backend.enumerations.Role;
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
public class Utilisateur {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(nullable = false)
    private String nom;
    @Column(nullable = false)
    private String motDePasse;
    @Column(nullable = false, unique = true)
    private String telephone;

    private String urlPhoto;
    @Column(nullable = false)
    private Role role;
    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false)
    private boolean estActive;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Etat etat;

    @OneToMany(mappedBy = "utilisateur")
    private List<UserDomaine> userDomaineList = new ArrayList<>();

}
