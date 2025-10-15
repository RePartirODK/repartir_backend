package com.example.repartir_backend.controllers;

import com.example.repartir_backend.dto.ResponseCentre;
import com.example.repartir_backend.dto.ResponseFormation;
import com.example.repartir_backend.entities.CentreFormation;
import com.example.repartir_backend.entities.Formation;
import com.example.repartir_backend.services.CentreFormationServices;
import com.example.repartir_backend.services.FormationServices;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/centres")
public class CentreFormationControllers {

    private final CentreFormationServices centreFormationServices;
    private final FormationServices formationServices;

    public CentreFormationControllers(CentreFormationServices centreFormationServices, FormationServices formationServices) {
        this.centreFormationServices = centreFormationServices;
        this.formationServices = formationServices;
    }

    @GetMapping("/mes-formations")
    @PreAuthorize("hasRole('CENTRE')")
    public ResponseEntity<List<ResponseFormation>> listerMesFormations() {
        return ResponseEntity.ok(formationServices.getMesFormations());
    }

    @GetMapping
    public ResponseEntity<List<ResponseCentre>> getAllCentres() {
        return ResponseEntity.ok(centreFormationServices.getAllCentres());
    }

    @GetMapping("/actifs")
    public ResponseEntity<List<ResponseCentre>> getCentresActifs() {
        return ResponseEntity.ok(centreFormationServices.getCentresActifs());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCentreById(@PathVariable int id) {
        try {
            return ResponseEntity.ok(centreFormationServices.getCentreById(id).toResponse());
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<?> getCentreByEmail(@PathVariable String email) {
        try {
            return ResponseEntity.ok(centreFormationServices.getCentreByEmail(email));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

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

    @PutMapping("/{id}/activer")
    public ResponseEntity<?> activerCentre(@PathVariable int id) {
        try {
            return ResponseEntity.ok(centreFormationServices.activerCentre(id));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @PutMapping("/{id}/desactiver")
    public ResponseEntity<?> desactiverCentre(@PathVariable int id) {
        try {
            return ResponseEntity.ok(centreFormationServices.desactiverCentre(id));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCentre(@PathVariable int id) {
        try {
            centreFormationServices.deleteCentre(id);
            return ResponseEntity.ok("Centre supprimé avec succès.");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

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
