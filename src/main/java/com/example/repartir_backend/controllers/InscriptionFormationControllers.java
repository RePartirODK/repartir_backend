package com.example.repartir_backend.controllers;
import com.example.repartir_backend.dto.InscriptionResponseDto;
import com.example.repartir_backend.dto.InscriptionDetailDto;
import com.example.repartir_backend.entities.InscriptionFormation;
import com.example.repartir_backend.dto.InscriptionResponseDto;// ... existing code ...
import com.example.repartir_backend.services.InscriptionFormationServices;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.nio.file.AccessDeniedException;
import java.util.List;
@RestController
@RequestMapping("/api/inscriptions")
@Tag(name = "Inscriptions", description = "Endpoints pour la gestion des inscriptions aux formations")

public class InscriptionFormationControllers {
    private final InscriptionFormationServices inscriptionFormationServices;

    public InscriptionFormationControllers(InscriptionFormationServices inscriptionFormationServices) {
        this.inscriptionFormationServices = inscriptionFormationServices;
    }

    @PostMapping("/s-inscrire/{formationId}")
    @PreAuthorize("hasRole('JEUNE')")
    @Operation(
            summary = "S’inscrire à une formation",
            description = "Permet à un utilisateur ayant le rôle 'JEUNE' de s’inscrire à une formation. "
                    + "L’utilisateur peut choisir de payer directement ou plus tard."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Inscription réussie",
                    content = @Content(schema = @Schema(implementation = InscriptionResponseDto.class))),
            @ApiResponse(responseCode = "403", description = "Accès refusé — rôle non autorisé", content = @Content),
            @ApiResponse(responseCode = "500", description = "Erreur interne lors de l’inscription", content = @Content)
    })
    public ResponseEntity<InscriptionResponseDto> sInscrireAFormation(@PathVariable int formationId,
                                                                      @RequestParam(defaultValue = "false")
                                                                      boolean payerDirectement)
    {
        InscriptionResponseDto inscriptionDto = inscriptionFormationServices.sInscrire(formationId, payerDirectement);
        return ResponseEntity.ok(inscriptionDto);
    }
    @GetMapping("/mes-inscriptions")
    @PreAuthorize("hasRole('JEUNE')")
    @Operation(
            summary = "Récupérer les formations auxquelles le jeune est inscrit",
            description = "Retourne la liste des inscriptions du jeune connecté avec les détails complets des formations.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Liste des inscriptions récupérée avec succès"),
                    @ApiResponse(responseCode = "403", description = "Accès refusé — rôle non autorisé", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Jeune non trouvé", content = @Content)
            }
    )
    public ResponseEntity<List<InscriptionDetailDto>> getMesInscriptions() {
        List<InscriptionDetailDto> inscriptions = inscriptionFormationServices.getMesInscriptions();
        return ResponseEntity.ok(inscriptions);}
    // New: list inscriptions for a formation (applicants to a specific formation)
    @GetMapping("/formation/{formationId}")
    @Operation(summary = "Lister les inscriptions d’une formation", description = "Retourne les inscriptions pour une formation donnée.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Liste des inscriptions récupérée avec succès",
                    content = @Content(schema = @Schema(implementation = InscriptionResponseDto.class)))
    })
    public ResponseEntity<List<InscriptionResponseDto>> listerInscriptionsParFormation(@PathVariable int formationId) {
        return ResponseEntity.ok(inscriptionFormationServices.listerParFormation(formationId));
    }
    // New: list inscriptions for all formations of a centre (applicants to centre’s formations)
    @GetMapping("/centre/{centreId}")
    @Operation(summary = "Lister les inscriptions d’un centre", description = "Retourne toutes les inscriptions des formations d’un centre donné.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Liste des inscriptions récupérée avec succès",
                    content = @Content(schema = @Schema(implementation = InscriptionResponseDto.class)))
    })
    public ResponseEntity<List<InscriptionResponseDto>> listerInscriptionsParCentre(@PathVariable int centreId) {
        return ResponseEntity.ok(inscriptionFormationServices.listerParCentre(centreId));
    }
    // Certifier une inscription (réservé au centre)
    @PatchMapping("/{inscriptionId}/certifier")
    @PreAuthorize("hasRole('CENTRE')")
    @Operation(
            summary = "Certifier une inscription",
            description = "Certifie un jeune pour une formation terminée, si l'inscription est validée."
    )
    public ResponseEntity<InscriptionResponseDto> certifierInscription(@PathVariable int inscriptionId) {
        return ResponseEntity.ok(inscriptionFormationServices.certifierInscription(inscriptionId));
    }
}
