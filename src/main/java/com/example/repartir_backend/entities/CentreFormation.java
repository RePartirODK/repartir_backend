package com.example.repartir_backend.entities;

import com.example.repartir_backend.dto.ResponseCentre;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class CentreFormation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(nullable = false)
    private String adresse;
    @Column(nullable = false)
    private String agrement;
    @OneToOne
    @JoinColumn(name = "id_utilisateur", nullable = false)
    private Utilisateur utilisateur;
    @OneToMany(mappedBy = "centreFormation", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Formation> formations = new ArrayList<>();

    public ResponseCentre toResponse(){
        return new ResponseCentre(
                this.id,
                this.utilisateur.getNom(),
                this.adresse,
                this.utilisateur.getTelephone(),
                this.utilisateur.getEmail(),
                this.utilisateur.getUrlPhoto() != null
                        ? this.utilisateur.getUrlPhoto()
                        : null,
                this.utilisateur.getRole(),
                this.utilisateur.isEstActive(),
                this.agrement
        );
    }
}
