package com.example.repartir_backend.controllers;

import com.example.repartir_backend.entities.InscriptionFormation;
import com.example.repartir_backend.services.InscriptionFormationServices;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.persistence.EntityNotFoundException;

@RestController
@RequestMapping("/api/inscriptions")
public class InscriptionFormationControllers {
    private final InscriptionFormationServices inscriptionFormationServices;

    public InscriptionFormationControllers(InscriptionFormationServices inscriptionFormationServices) {
        this.inscriptionFormationServices = inscriptionFormationServices;
    }

    @PostMapping("/demander-parrainage/{formationId}")
    @PreAuthorize("hasRole('JEUNE')")
    public ResponseEntity<InscriptionFormation> demanderParrainagePourFormation(@PathVariable int formationId) {
        InscriptionFormation inscription = inscriptionFormationServices.demanderParrainage(formationId);
        return ResponseEntity.ok(inscription);
    }
}
