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
        ResponseMentoring response = new ResponseMentoring();
        
        response.setId(this.id);
        response.setNomJeune(this.jeune.getUtilisateur().getNom());
        response.setPrenomJeune(this.jeune.getPrenom());
        response.setNomMentor(this.mentor.getUtilisateur().getNom());
        response.setPrenomMentor(this.mentor.getPrenom());
        response.setDateDebut(this.date_debut);
        response.setObjectif(this.objectif);
        response.setDescription(this.description);
        response.setNoteMentor(this.noteMentor);
        response.setNoteJeune(this.noteJeune);
        response.setStatut(this.statut != null ? this.statut.name() : null);
        
        // Informations supplémentaires du mentor
        response.setIdMentor(this.mentor.getId());
        response.setSpecialiteMentor(this.mentor.getProfession());  // profession = spécialité
        response.setAnneesExperienceMentor(this.mentor.getAnnee_experience());
        response.setUrlPhotoMentor(this.mentor.getUtilisateur().getUrlPhoto());
        
        return response;
    }

}
