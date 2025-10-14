package com.example.repartir_backend.controllers;

import com.example.repartir_backend.dto.ResponseCentre;
import com.example.repartir_backend.entities.CentreFormation;
import com.example.repartir_backend.entities.Formation;
import com.example.repartir_backend.services.CentreFormationServices;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/centres")
public class CentreFormationControllers {

    CentreFormationServices centreFormationServices;
    public CentreFormationControllers(CentreFormationServices centreFormationServices)
    {
        this.centreFormationServices = centreFormationServices;
    }

    //recuperer tous les centres
    @GetMapping
    public ResponseEntity<List<ResponseCentre>> getAllCentres() {
        return ResponseEntity.ok(centreFormationServices.getAllCentres());
    }

    //recuperer les centres actifs
    @GetMapping("/actifs")
    public ResponseEntity<List<ResponseCentre>> getCentresActifs() {
        return ResponseEntity.ok(centreFormationServices.getCentresActifs());
    }

    //recuperer un centre par id
    @GetMapping("/{id}")
    public ResponseEntity<?> getCentreById(@PathVariable int id) {
        try {
            return ResponseEntity.ok(centreFormationServices.getCentreById(id).toResponse());
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    //recuperer un centre par son email
    @GetMapping("/email/{email}")
    public ResponseEntity<?> getCentreByEmail(@PathVariable String email) {
        try {
            return ResponseEntity.ok(centreFormationServices.getCentreByEmail(email));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    //Modifier un centre
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCentre(
            @PathVariable int id,
            @RequestBody CentreFormation centreDetails) {
        try {
            return ResponseEntity.ok(centreFormationServices.updateCentre(id, centreDetails).toResponse());
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    //activer un centre
    @PutMapping("/{id}/activer")
    public ResponseEntity<?> activerCentre(@PathVariable int id) {
        try {
            return ResponseEntity.ok(centreFormationServices.activerCentre(id));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }
    //desactiver un centre
    @PutMapping("/{id}/desactiver")
    public ResponseEntity<?> desactiverCentre(@PathVariable int id) {
        try {
            return ResponseEntity.ok(centreFormationServices.desactiverCentre(id));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    //supprimer un centre
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCentre(@PathVariable int id) {
        try {
            centreFormationServices.deleteCentre(id);
            return ResponseEntity.ok("Centre supprimé avec succès.");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    // Récupérer toutes les formations d’un centre
    @GetMapping("/{id}/formations")
    public ResponseEntity<?> getFormationsByCentre(@PathVariable int id) {
        try {
            List<Formation> formations = centreFormationServices.getFormationsByCentre(id);
            return ResponseEntity.ok(formations);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }


}
