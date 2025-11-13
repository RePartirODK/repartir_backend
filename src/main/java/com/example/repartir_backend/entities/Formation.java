package com.example.repartir_backend.entities;

import com.example.repartir_backend.dto.RequestFormation;
import com.example.repartir_backend.dto.ResponseCentre;
import com.example.repartir_backend.dto.ResponseFormation;
import com.example.repartir_backend.enumerations.Etat;
import com.example.repartir_backend.enumerations.Format;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Formation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(nullable = false)
    private String description;
    @Column(nullable = false)
    private String titre;
    @Column(nullable = false)
    private LocalDateTime date_debut;
    @Column(nullable = false)
    private LocalDateTime date_fin;
    @Enumerated(EnumType.STRING)
    private Etat statut;
    private Double cout;
    @Column(nullable = false)
    private Integer nbre_place;
    @Column(nullable = false)
    private Format format;
    @Column(nullable = false)
    private String duree;
    private String urlFormation;
    private String urlCertificat;

    @OneToMany(mappedBy = "formation", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<InscriptionFormation> inscriptions = new ArrayList<>();
    @OneToMany(mappedBy = "formation", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Parrainage> parrainages = new ArrayList<>();
    @ManyToOne
    @JoinColumn(name = "id_centreformation", nullable = false)
    private CentreFormation centreFormation;

    public ResponseFormation toResponse(){
        return new ResponseFormation(
        this.id,
        this.titre,
        this.description,
        this.date_debut,
        this.date_fin,
        this.getStatutActuel(),
        this.cout,
        this.nbre_place,
       this.format,
        this.duree,
        this.urlFormation,
        this.urlCertificat,
        this.centreFormation.getId()
        );
    }

    public Formation toFormation(RequestFormation requestFormation) {
        Formation formation = new Formation();

        formation.setTitre(requestFormation.getTitre());
        formation.setDescription(requestFormation.getDescription());
        formation.setDate_debut(requestFormation.getDate_debut());
        formation.setDate_fin(requestFormation.getDate_fin());
        formation.setStatut(requestFormation.getStatut());
        formation.setCout(requestFormation.getCout());
        formation.setNbre_place(requestFormation.getNbrePlace());
        formation.setFormat(requestFormation.getFormat());
        formation.setDuree(requestFormation.getDuree());
        formation.setUrlFormation(requestFormation.getUrlFormation()!=null?
                requestFormation.getUrlFormation():null);
        formation.setUrlCertificat(requestFormation.getUrlCertificat());

        return formation;
    }
    public Etat getStatutActuel() {
        LocalDateTime now = LocalDateTime.now();

        if (now.isBefore(this.date_debut)) {
            return Etat.EN_ATTENTE;
        } else if (now.isAfter(this.date_fin)) {
            return Etat.TERMINE;
        } else {
            return Etat.EN_COURS;
        }
    }
}
