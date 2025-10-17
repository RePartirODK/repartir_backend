package com.example.repartir_backend.controllers;

import com.example.repartir_backend.dto.ParrainageDto;
import com.example.repartir_backend.dto.RequestParrainageDto;
import com.example.repartir_backend.dto.ResponseParrainage;
import com.example.repartir_backend.services.ParrainageServices;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/parrainages")
@RequiredArgsConstructor
public class ParrainageControllers {

    private final ParrainageServices parrainageServices;

    @PostMapping("/creer")
    @PreAuthorize("hasRole('JEUNE')")
    public ResponseEntity<ResponseParrainage> creerParrainage(@RequestBody RequestParrainageDto requestDto) {
        return ResponseEntity.ok(parrainageServices.creerParrainage(
                requestDto.getIdJeune(),
                requestDto.getIdParrain(),
                requestDto.getIdFormation()
        ));
    }

    @PostMapping("/{idParrainage}/accepter/{idParrain}")
    @PreAuthorize("hasRole('PARRAIN')")
    public ResponseEntity<ResponseParrainage> accepterDemande(
            @PathVariable int idParrainage,
            @PathVariable int idParrain) {
        return ResponseEntity.ok(parrainageServices.accepterDemande(idParrainage, idParrain));
    }

    @GetMapping("/demandes-en-attente")
    @PreAuthorize("hasRole('PARRAIN')")
    public ResponseEntity<List<ResponseParrainage>> listerDemandesEnAttente() {
        return ResponseEntity.ok(parrainageServices.listerDemandes());
    }

    @GetMapping("/lister")
    public ResponseEntity<List<ResponseParrainage>> getAllParrainages() {
        return ResponseEntity.ok(parrainageServices.getAllParrainages());
    }
}

