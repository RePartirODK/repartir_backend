package com.example.repartir_backend.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @OneToOne
    @JoinColumn(name = "id_utilisateur")
    private Utilisateur utilisateur;
    @OneToOne
    @JoinColumn(name = "id_admin")
    private Admin admin;
    @Column(nullable = false, unique = true)
    private String token;
    @Column(nullable = false)
    private Instant dateExpiration;
}
