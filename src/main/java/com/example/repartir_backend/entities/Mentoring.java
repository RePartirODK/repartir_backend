package com.example.repartir_backend.entities;

import com.example.repartir_backend.dto.ResponseMentoring;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(nullable = false)
    private String objectif;
    @Enumerated(EnumType.STRING)
    private Etat statut;
    private LocalDateTime date_debut;
    @Column(nullable = false)
    private String description;
    private int noteJeune;
    private int noteMentor;

    //relation
    @ManyToOne
    @JoinColumn(name = "id_jeune", nullable = false)
    private Jeune jeune;
    @ManyToOne
    @JoinColumn(name = "id_mentor", nullable = false)
    private Mentor mentor;

    public ResponseMentoring toResponse(){
        return new ResponseMentoring(
                this.jeune.getUtilisateur().getNom(),
                this.jeune.getPrenom(),
                this.mentor.getUtilisateur().getNom(),
                this.mentor.getPrenom(),
        this.date_debut,
        this.getObjectif(),
        this.getDescription(),
        this.getNoteJeune(),
        this.getNoteMentor());
    }

}
