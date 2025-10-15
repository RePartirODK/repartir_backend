package com.example.repartir_backend.entities;

import com.example.repartir_backend.enumerations.Etat;
import com.example.repartir_backend.enumerations.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.type.TrueFalseConverter;

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
    @Enumerated(EnumType.STRING)
    private Role role;
    @Column(nullable = false, unique = true)
    private String email;
    @Enumerated(EnumType.STRING)
    private Etat etat;
    @Column(nullable = false)
    @Convert(converter = TrueFalseConverter.class)
    private boolean estActive;


    @OneToMany(mappedBy = "utilisateur")
    @JsonIgnore
    private List<UserDomaine> userDomaineList = new ArrayList<>();

}
