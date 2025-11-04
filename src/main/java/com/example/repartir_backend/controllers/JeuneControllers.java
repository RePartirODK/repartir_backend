package com.example.repartir_backend.controllers;

import com.example.repartir_backend.dto.JeuneResponseDto;
import com.example.repartir_backend.dto.UpdateJeuneDto;
import com.example.repartir_backend.dto.DashboardJeuneDto;
import com.example.repartir_backend.entities.Jeune;
import com.example.repartir_backend.services.JeuneServices;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/jeunes")
@Tag(name = "Jeunes", description = "Gestion du profil et du compte des jeunes")

public class JeuneControllers {
    private final JeuneServices jeuneServices;
    public JeuneControllers(JeuneServices jeuneServices){
        this.jeuneServices = jeuneServices;
    }

    @Operation(
            summary = "Récupérer le profil du jeune connecté",
            description = "Permet au jeune connecté de récupérer ses informations personnelles.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Profil récupéré avec succès"),
                    @ApiResponse(responseCode = "404", description = "Jeune introuvable", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content)
            }
    )
    @GetMapping("/profile")
    @PreAuthorize("hasRole('JEUNE')")
    public ResponseEntity<?> getJeuneProfile() {
        try {
            Jeune jeune = jeuneServices.getCurrentJeuneProfile();
            return ResponseEntity.ok(jeune);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la récupération du profil : " + e.getMessage());
        }
    }

    @Operation(
            summary = "Modifier le profil du jeune",
            description = "Permet au jeune connecté de mettre à jour ses informations personnelles.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Profil mis à jour avec succès"),
                    @ApiResponse(responseCode = "404", description = "Jeune introuvable", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content)
            }
    )


    @PutMapping("/modifier")
    @PreAuthorize("hasRole('JEUNE')")
    public ResponseEntity<?> updateJeuneProfile(@RequestBody UpdateJeuneDto updateDto) {
        try {
            Jeune updatedJeune = jeuneServices.updateJeune(updateDto);
            return ResponseEntity.ok(updatedJeune);
    }catch (
    EntityNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(e.getMessage());

    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Erreur lors de la mise à jour du profil : " + e.getMessage());
    }}

    @Operation(
            summary = "Supprimer le compte du jeune",
            description = "Supprime définitivement le compte du jeune connecté.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Compte jeune supprimé avec succès"),
                    @ApiResponse(responseCode = "403", description = "Accès refusé (non autorisé)", content = @Content)
            }
    )
    @DeleteMapping("/supprimer")
    @PreAuthorize("hasRole('JEUNE')")
    public ResponseEntity<?> deleteJeuneProfile() {
        jeuneServices.deleteJeune();
        return ResponseEntity.ok(Map.of("message", "Compte jeune supprimé avec succès."));
    }

    @Operation(
            summary = "Récupérer le dashboard du jeune",
            description = "Retourne les statistiques et données récentes du jeune connecté.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Dashboard récupéré avec succès"),
                    @ApiResponse(responseCode = "404", description = "Jeune introuvable", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content)
            }
    )
    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('JEUNE')")
    public ResponseEntity<?> getDashboard() {
        try {
            DashboardJeuneDto dashboard = jeuneServices.getDashboard();
            return ResponseEntity.ok(dashboard);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la récupération du dashboard : " + e.getMessage());
        }
    }

    @Operation(
            summary = "Lister tous les jeunes",
            description = "Récupère la liste complète des jeunes enregistrés dans la base de données."
    )
    @GetMapping
    public ResponseEntity<List<JeuneResponseDto>> getAllJeunes() {
        return ResponseEntity.ok(jeuneServices.getAllJeunes());
    }
}
