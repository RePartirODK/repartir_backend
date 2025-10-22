package com.example.repartir_backend.controllers;

import com.example.repartir_backend.dto.ParrainageDto;
import com.example.repartir_backend.dto.RequestParrainageDto;
import com.example.repartir_backend.dto.ResponseParrainage;
import com.example.repartir_backend.services.ParrainageServices;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/parrainages")
@RequiredArgsConstructor
@Tag(name = "Parrainages", description = "Endpoints pour la gestion des parrainages entre jeunes et parrains")
public class ParrainageControllers {

    private final ParrainageServices parrainageServices;

    @Operation(summary = "Créer un parrainage", description = "Permet à un jeune de créer une demande de parrainage pour une formation donnée.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Parrainage créé avec succès"),
            @ApiResponse(responseCode = "403", description = "Accès refusé si l'utilisateur n'a pas le rôle JEUNE")
    })
    @PostMapping("/creer")
    @PreAuthorize("hasRole('JEUNE')")
    public ResponseEntity<ResponseParrainage> creerParrainage(@RequestBody RequestParrainageDto requestDto) {
        return ResponseEntity.ok(parrainageServices.creerParrainage(
                requestDto.getIdJeune(),
                requestDto.getIdParrain(),
                requestDto.getIdFormation()
        ));
    }

    @Operation(summary = "Accepter une demande de parrainage", description = "Permet à un parrain d'accepter une demande de parrainage.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Demande acceptée avec succès"),
            @ApiResponse(responseCode = "403", description = "Accès refusé si l'utilisateur n'a pas le rôle PARRAIN")
    })
    @PostMapping("/{idParrainage}/accepter/{idParrain}")
    @PreAuthorize("hasRole('PARRAIN')")
    public ResponseEntity<ResponseParrainage> accepterDemande(
            @PathVariable int idParrainage,
            @PathVariable int idParrain) {
        return ResponseEntity.ok(parrainageServices.accepterDemande(idParrainage, idParrain));
    }

    @Operation(summary = "Lister les demandes en attente", description = "Retourne la liste des demandes de parrainage en attente pour le parrain connecté.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès"),
            @ApiResponse(responseCode = "403", description = "Accès refusé si l'utilisateur n'a pas le rôle PARRAIN")
    })
    @GetMapping("/demandes-en-attente")
    @PreAuthorize("hasRole('PARRAIN')")
    public ResponseEntity<List<ResponseParrainage>> listerDemandesEnAttente() {
        return ResponseEntity.ok(parrainageServices.listerDemandes());
    }

    @Operation(summary = "Lister tous les parrainages", description = "Retourne la liste complète de tous les parrainages.")
    @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès")
    @GetMapping("/lister")
    public ResponseEntity<List<ResponseParrainage>> getAllParrainages() {
        return ResponseEntity.ok(parrainageServices.getAllParrainages());
    }
}

