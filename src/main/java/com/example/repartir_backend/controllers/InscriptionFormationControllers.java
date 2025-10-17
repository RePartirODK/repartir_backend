package com.example.repartir_backend.controllers;

import com.example.repartir_backend.dto.InscriptionResponseDto;
import com.example.repartir_backend.entities.InscriptionFormation;
import com.example.repartir_backend.services.InscriptionFormationServices;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;

@RestController
@RequestMapping("/api/inscriptions")
public class InscriptionFormationControllers {
    private final InscriptionFormationServices inscriptionFormationServices;

    public InscriptionFormationControllers(InscriptionFormationServices inscriptionFormationServices) {
        this.inscriptionFormationServices = inscriptionFormationServices;
    }

    @PostMapping("/s-inscrire/{formationId}")
    @PreAuthorize("hasRole('JEUNE')")
    public ResponseEntity<InscriptionResponseDto> sInscrireAFormation(@PathVariable int formationId) {
        InscriptionResponseDto inscriptionDto = inscriptionFormationServices.sInscrire(formationId);
        return ResponseEntity.ok(inscriptionDto);
    }
}
