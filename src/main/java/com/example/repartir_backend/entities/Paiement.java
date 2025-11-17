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
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_parrainage")
    private Parrainage parrainage;  // Relation vers la table parrainage
    
    @ManyToOne
    @JoinColumn(name = "id_inscriptionFormation", nullable = false)
    private InscriptionFormation inscriptionFormation;

    /**
     * Méthode pour obtenir l'ID du parrain via parrainage
     * Récupère idParrain depuis parrainage.id_parrain (via la relation parrainage.parrain.id)
     * @return L'ID du parrain ou null si aucun parrain n'est associé
     */
    public Integer getIdParrain() {
        if (this.parrainage != null && this.parrainage.getParrain() != null) {
            return this.parrainage.getParrain().getId();
        }
        return null;
    }

    //convertion en dto
    public ResponsePaiement toResponse(){
        ResponsePaiement dto = new ResponsePaiement();
        dto.setId(this.id);
        dto.setMontant(this.montant);
        dto.setReference(this.reference);
        dto.setDate(this.date);
        dto.setStatus(this.status);
        dto.setIdJeune(this.jeune != null ? this.jeune.getId() : null);
        dto.setIdParrainage(this.parrainage != null ? this.parrainage.getId() : null);
        // Récupérer idParrain via parrainage.id_parrain (via jointure)
        dto.setIdParrain(this.getIdParrain());
        dto.setIdFormation(this.inscriptionFormation != null ? this.inscriptionFormation.getId() : null);
        return dto;
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
        
        // Récupérer idParrain via parrainage.id_parrain (via jointure)
        response.setIdParrain(this.getIdParrain());
        
        return response;
    }
}
