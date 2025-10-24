package com.example.repartir_backend.controllers;

import com.example.repartir_backend.dto.ResponseParrain;
import com.example.repartir_backend.entities.Parrain;
import com.example.repartir_backend.services.ParrainServices;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/parrains")
@RequiredArgsConstructor
public class ParrainControllers {

    private final ParrainServices parrainServices;

    // GET /api/parrains
    @Operation(
            summary = "Lister tous les parrains",
            description = "Récupère la liste complète des parrains enregistrés dans la base de données.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Liste des parrains récupérée avec succès")
            }
    )
    @GetMapping
    public ResponseEntity<List<ResponseParrain>> getAllParrains() {
        return ResponseEntity.ok(parrainServices.getAllParrains());
    }

    // GET /api/parrains/actifs
    @Operation(
            summary = "Lister les parrains actifs",
            description = "Retourne uniquement les parrains actuellement actifs (ayant validé leur statut).",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Liste des parrains actifs renvoyée avec succès")
            }
    )
    @GetMapping("/actifs")
    public ResponseEntity<List<ResponseParrain>> getParrainsActifs() {
        return ResponseEntity.ok(parrainServices.getParrainsActifs());
    }

    // GET /api/parrains/{id}
    @Operation(
            summary = "Obtenir un parrain par ID",
            description = "Permet de récupérer les informations d’un parrain spécifique via son identifiant unique.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Parrain trouvé et renvoyé"),
                    @ApiResponse(responseCode = "404", description = "Parrain non trouvé", content = @Content)
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<ResponseParrain> getParrainById(@PathVariable int id) {
        try {
            return ResponseEntity.ok(parrainServices.getParrainById(id));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // GET /api/parrains/email/{email}
    @Operation(
            summary = "Obtenir un parrain par email",
            description = "Recherche un parrain à partir de son adresse email.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Parrain trouvé et renvoyé"),
                    @ApiResponse(responseCode = "404", description = "Aucun parrain trouvé avec cet email", content = @Content)
            }
    )
    @GetMapping("/email/{email}")
    public ResponseEntity<ResponseParrain> getParrainByEmail(@PathVariable String email) {
        try {
            return ResponseEntity.ok(parrainServices.getParrainByEmail(email));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // PUT /api/parrains/{id}
    @Operation(
            summary = "Mettre à jour un parrain",
            description = "Modifie les informations personnelles d’un parrain existant.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Parrain mis à jour avec succès"),
                    @ApiResponse(responseCode = "404", description = "Parrain non trouvé", content = @Content)
            }
    )
    @PutMapping("/{id}")
    public ResponseEntity<ResponseParrain> updateParrain(
            @PathVariable int id,
            //body doit avoir un requestparrain
            @RequestBody Parrain parrainDetails
    ) {
        try {
            return ResponseEntity.ok(parrainServices.updateParrain(id, parrainDetails));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // DELETE /api/parrains/{id}
    @Operation(
            summary = "Supprimer un parrain",
            description = "Supprime définitivement un parrain de la base de données à partir de son ID.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Parrain supprimé avec succès"),
                    @ApiResponse(responseCode = "404", description = "Parrain non trouvé", content = @Content)
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteParrain(@PathVariable int id) {
        try {
            parrainServices.deleteParrain(id);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}

