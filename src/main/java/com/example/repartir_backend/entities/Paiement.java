package com.example.repartir_backend.entities;

import com.example.repartir_backend.dto.ResponsePaiement;
import com.example.repartir_backend.dto.ResponsePaiementAdmin;
import com.example.repartir_backend.enumerations.StatutPaiement;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Paiement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(nullable = false)
    private Double montant;
    @Column(nullable = false)
    private String reference;
    //statut du paiement
    @Column
    @Enumerated(EnumType.STRING)
    private StatutPaiement status;

    @Column
    private String motifRefus;

    @Column(nullable = false)
    private LocalDateTime date;

    @ManyToOne
    @JoinColumn(name = "id_jeune")
    private Jeune jeune;
    @ManyToOne
    @JoinColumn(name = "id_parrainage")
    private Parrainage parrainage;
    @ManyToOne
    @JoinColumn(name = "id_inscriptionFormation", nullable = false)
    private InscriptionFormation inscriptionFormation;

    //convertion en dto
    public ResponsePaiement toResponse(){
        return new ResponsePaiement(
                this.id,
                this.montant,
                this.reference,
                this.date,
                this.status,
                this.jeune.getId(),
                this.parrainage != null ? this.parrainage.getId() : null,
                this.inscriptionFormation.getId()
        );
    }

    //convertion en dto admin avec détails complets
    public ResponsePaiementAdmin toAdminResponse(){
        ResponsePaiementAdmin response = new ResponsePaiementAdmin();
        
        // Informations du paiement
        response.setId(this.id);
        response.setMontant(this.montant);
        response.setReference(this.reference);
        response.setDate(this.date);
        response.setStatus(this.status);
        response.setMotifRefus(this.motifRefus);
        
        // Informations du jeune
        if (this.jeune != null) {
            response.setIdJeune(this.jeune.getId());
            response.setPrenomJeune(this.jeune.getPrenom());
            
            if (this.jeune.getUtilisateur() != null) {
                response.setNomJeune(this.jeune.getUtilisateur().getNom());
                response.setEmailJeune(this.jeune.getUtilisateur().getEmail());
            }
        }
        
        // Informations de la formation et du centre
        if (this.inscriptionFormation != null && this.inscriptionFormation.getFormation() != null) {
            Formation formation = this.inscriptionFormation.getFormation();
            response.setIdFormation(formation.getId());
            response.setTitreFormation(formation.getTitre());
            response.setDescriptionFormation(formation.getDescription());
            
            // Informations du centre de formation
            if (formation.getCentreFormation() != null) {
                response.setIdCentre(formation.getCentreFormation().getId());
                
                if (formation.getCentreFormation().getUtilisateur() != null) {
                    response.setNomCentre(formation.getCentreFormation().getUtilisateur().getNom());
                }
            }
        }
        
        // Informations du parrainage (nullable)
        if (this.parrainage != null) {
            response.setIdParrainage(this.parrainage.getId());
        }
        
        return response;
    }
}
