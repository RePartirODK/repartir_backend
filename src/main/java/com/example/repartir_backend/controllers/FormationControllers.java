package com.example.repartir_backend.controllers;

import com.example.repartir_backend.dto.RequestFormation;
import com.example.repartir_backend.dto.ResponseFormation;
import com.example.repartir_backend.entities.Formation;
import com.example.repartir_backend.enumerations.Etat;
import com.example.repartir_backend.services.FormationServices;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/formations")
@RequiredArgsConstructor
public class FormationControllers {
    private final FormationServices formationServices;
    @PostMapping("/centre/{centreId}")
    public ResponseEntity<ResponseFormation> createFormation(
            @PathVariable int centreId,
            @RequestBody RequestFormation requestFormation) {
        Formation createdFormation = formationServices.createFormation(requestFormation, centreId);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdFormation.toResponse());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseFormation> updateFormation(
            @PathVariable int id,
            @RequestBody RequestFormation requestFormation) {
        ResponseFormation updatedFormation = formationServices.updateFormation(id, requestFormation);
        return ResponseEntity.ok(updatedFormation);
    }

    @PatchMapping("/{id}/statut")
    public ResponseEntity<Formation> updateStatut(
            @PathVariable int id,
            @RequestParam Etat statut) {
        Formation updated = formationServices.updateStatut(id, statut);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFormation(@PathVariable int id) {
        formationServices.deleteFormation(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping
    public ResponseEntity<List<ResponseFormation>> getAllFormations() {
        List<ResponseFormation> formations = formationServices.getAllFormations();
        return ResponseEntity.ok(formations);
    }


    @GetMapping("/centre/{centreId}")
    public ResponseEntity<List<ResponseFormation>> getFormationsByCentre(@PathVariable int centreId) {
        List<ResponseFormation> formations = formationServices.getFormationsByCentre(centreId);
        return ResponseEntity.ok(formations);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseFormation> getFormationById(@PathVariable int id) {
        ResponseFormation formation = formationServices.getFormationById(id);
        return ResponseEntity.ok(formation);
    }

}
