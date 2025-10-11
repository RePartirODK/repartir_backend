package com.example.repartir_backend.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column
    private String contenu;
    @Column
    private LocalDateTime date;

    @ManyToOne
    @JoinColumn(name = "id_jeune")
    private Jeune jeune;
    @ManyToOne
    @JoinColumn(name = "id_mentor")
    private Mentor mentor;
}
