package com.example.repartir_backend.controllers;

import com.example.repartir_backend.dto.RequestFormation;
import com.example.repartir_backend.dto.ResponseFormation;
import com.example.repartir_backend.entities.Formation;
import com.example.repartir_backend.enumerations.Etat;
import com.example.repartir_backend.services.FormationServices;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/formations")
@RequiredArgsConstructor
@Tag(name = "Formations", description = "Gestion des formations proposées par les centres")
public class FormationControllers {
    private final FormationServices formationServices;

    @Operation(
            summary = "Créer une nouvelle formation",
            description = "Permet à un centre de formation d’ajouter une formation à son catalogue."
    )
    @ApiResponse(responseCode = "201", description = "Formation créée avec succès")
    @ApiResponse(responseCode = "404", description = "Centre non trouvé", content = @Content)
    @PostMapping("/centre/{centreId}")
    public ResponseEntity<?> createFormation(
            @Parameter(description = "ID du centre de formation")
            @PathVariable int centreId,
            @RequestBody RequestFormation requestFormation) {
        Formation createdFormation = formationServices.createFormation(requestFormation, centreId);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdFormation.toResponse());
    }

    @Operation(
            summary = "Mettre à jour une formation",
            description = "Met à jour les informations d’une formation existante."
    )
    @PutMapping("/{id}")
    public ResponseEntity<?> updateFormation(
            @Parameter(description = "ID de la formation à modifier")
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
                    .body("Une erreur est survenue" + e.getMessage());
        }

    }

    @Operation(
            summary = "Modifier le statut d’une formation",
            description = "Permet de changer le statut (ex : EN_COURS, TERMINEE, ANNULEE) d’une formation."
    )
    @PatchMapping("/{id}/statut")
    public ResponseEntity<?> updateStatut(
            @Parameter(description = "ID de la formation à modifier")
            @PathVariable int id,
            @Parameter(description = "Nouveau statut de la formation")
            @RequestParam Etat statut) {
        try {
            Formation updated = formationServices.updateStatut(id, statut);
            return ResponseEntity.ok(updated.toResponse());
        }catch (EntityNotFoundException e)
        {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }catch (RuntimeException e)
        {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Une erreur est survenue" + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFormation(@PathVariable int id) {
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
