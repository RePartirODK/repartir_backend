package com.example.repartir_backend.entities;

import com.example.repartir_backend.enumerations.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Utilisateur {
    @Id
    private int id;
    @Column(nullable = false)
    private String nom;
    @Column(nullable = false)
    private String telephone;
    @Column(nullable = false)
    private String email;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    private String urlPhoto;
    @Column(nullable = false)
    private boolean isActive;
    @Enumerated(EnumType.STRING)
    private Role role;
}
