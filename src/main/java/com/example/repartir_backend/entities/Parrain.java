package com.example.repartir_backend.entities;

import com.example.repartir_backend.dto.ResponseParrain;
import com.example.repartir_backend.enumerations.Role;
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

    public ResponseParrain toResponse(){
        return new ResponseParrain(
        this.utilisateur.getNom(),
        this.prenom,
        this.utilisateur.getEmail(),
        this.utilisateur.getTelephone(),
        this.utilisateur.getUrlPhoto() !=null ? this.utilisateur.getUrlPhoto(): null,
        this.utilisateur.getRole(),
        this.utilisateur.isEstActive(),
        this.profession,
                this.getUtilisateur().getDateCreation()
        );
    }
}
