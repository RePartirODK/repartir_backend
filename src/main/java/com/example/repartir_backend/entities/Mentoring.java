package com.example.repartir_backend.entities;

import com.example.repartir_backend.enumerations.Etat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Mentoring {
    @Id
    private int id;
    @Column(nullable = false)
    private String objectif;
    @Enumerated(EnumType.STRING)
    private Etat statut;
    private LocalDateTime date_debut;
    @Column(nullable = false)
    private String description;
    private int note_jeune;
    private int note_mentor;

    //relation
    @ManyToOne
    @JoinColumn(name = "id_jeune", nullable = false)
    private Jeune jeune;
    @ManyToOne
    @JoinColumn(name = "id_mentor", nullable = false)
    private Mentor mentor;
}
