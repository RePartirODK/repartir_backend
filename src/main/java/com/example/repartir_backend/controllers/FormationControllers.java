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
    public ResponseEntity<?> createFormation(
            @PathVariable int centreId,
            @RequestBody RequestFormation requestFormation) {
        try {
            Formation createdFormation = formationServices.createFormation(requestFormation, centreId);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdFormation.toResponse());
        }catch (EntityNotFoundException e){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }catch (RuntimeException e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateFormation(
            @PathVariable int id,
            @RequestBody RequestFormation requestFormation) {
        try {
            ResponseFormation updatedFormation = formationServices.updateFormation(id, requestFormation);
            return ResponseEntity.ok(updatedFormation);
        }catch (EntityNotFoundException e)
        {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }catch (RuntimeException e)
        {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("une erreur interne s'est produite");
        }
    }

    @PatchMapping("/{id}/statut")
    public ResponseEntity<?> updateStatut(
            @PathVariable int id,
            @RequestParam Etat statut) {
        try {
            Formation updated = formationServices.updateStatut(id, statut);
            return ResponseEntity.ok(updated);
        }catch (EntityNotFoundException e)
        {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFormation(@PathVariable int id) {
        try {
            formationServices.deleteFormation(id);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
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
    public ResponseEntity<?> getFormationById(@PathVariable int id) {
        try {
            ResponseFormation formation = formationServices.getFormationById(id);
            return ResponseEntity.ok(formation);
        }catch (EntityNotFoundException e)
        {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }

    }

}
