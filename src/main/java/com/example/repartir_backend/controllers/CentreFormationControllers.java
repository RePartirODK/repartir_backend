package com.example.repartir_backend.controllers;

import com.example.repartir_backend.dto.RegisterUtilisateur;
import com.example.repartir_backend.dto.ResponseCentre;
import com.example.repartir_backend.dto.ResponseFormation;
import com.example.repartir_backend.entities.CentreFormation;
import com.example.repartir_backend.entities.Formation;
import com.example.repartir_backend.services.CentreFormationServices;
import com.example.repartir_backend.services.FormationServices;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.ResponseEntity;
<<<<<<< HEAD
=======
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
>>>>>>> main
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/centres")
@Tag(name = "Centres de formation", description = "Gestion et consultation des centres de formation et de leurs formations associées.")
public class CentreFormationControllers {

    private final CentreFormationServices centreFormationServices;
    private final FormationServices formationServices;

    public CentreFormationControllers(CentreFormationServices centreFormationServices, FormationServices formationServices) {
        this.centreFormationServices = centreFormationServices;
        this.formationServices = formationServices;
    }

    @GetMapping("/mes-formations")
    @Operation(
            summary = "Lister les formations du centre connecté",
            description = "Retourne la liste des formations appartenant au centre actuellement connecté.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Liste des formations récupérée avec succès")
            }
    )
    public ResponseEntity<List<ResponseFormation>> listerMesFormations() {
        return ResponseEntity.ok(formationServices.getMesFormations());
    }

    @Operation(
            summary = "Lister tous les centres de formation",
            description = "Retourne la liste complète des centres enregistrés dans la plateforme.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès")
            }
    )
    @GetMapping
    public ResponseEntity<List<ResponseCentre>> getAllCentres() {
        return ResponseEntity.ok(centreFormationServices.getAllCentres());
    }

    @Operation(
            summary = "Lister les centres actifs",
            description = "Retourne uniquement les centres de formation actuellement actifs.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Liste des centres actifs")
            }
    )
    @GetMapping("/actifs")
    public ResponseEntity<List<ResponseCentre>> getCentresActifs() {
        return ResponseEntity.ok(centreFormationServices.getCentresActifs());
    }

    @Operation(
            summary = "Obtenir un centre par ID",
            description = "Retourne les informations détaillées d’un centre de formation à partir de son identifiant.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Centre trouvé"),
                    @ApiResponse(responseCode = "404", description = "Centre non trouvé", content = @Content(schema = @Schema(example = "Centre introuvable")))
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<?> getCentreById(@PathVariable int id) {
        try {
            return ResponseEntity.ok(centreFormationServices.getCentreById(id).toResponse());
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @Operation(
            summary = "Obtenir un centre par email",
            description = "Recherche et retourne les informations d’un centre à partir de son adresse email.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Centre trouvé"),
                    @ApiResponse(responseCode = "404", description = "Aucun centre correspondant à cet email")
            }
    )
    @GetMapping("/email/{email}")
    public ResponseEntity<?> getCentreByEmail(@PathVariable String email) {
        try {
            return ResponseEntity.ok(centreFormationServices.getCentreByEmail(email));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @Operation(
            summary = "Modifier les informations d’un centre",
            description = "Met à jour les informations d’un centre de formation existant.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Centre mis à jour avec succès"),
                    @ApiResponse(responseCode = "404", description = "Centre non trouvé")
            }
    )
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCentre(
            @PathVariable int id,
            @RequestBody CentreFormation centreDetails) {
        try {
            return ResponseEntity.ok(centreFormationServices.updateCentre(id, centreDetails).toResponse());
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @PutMapping("/v1")
    public ResponseEntity<?> updateCentreV1(
            @RequestBody RegisterUtilisateur centreDetails) {
        try {
            return ResponseEntity.ok(centreFormationServices.updateCentreV1(centreDetails));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @Operation(
            summary = "Activer un centre de formation",
            description = "Permet d’activer un centre précédemment désactivé.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Centre activé"),
                    @ApiResponse(responseCode = "404", description = "Centre non trouvé")
            }
    )
    @PutMapping("/{id}/activer")
    public ResponseEntity<?> activerCentre(@PathVariable int id) {
        try {
            return ResponseEntity.ok(centreFormationServices.activerCentre(id));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }


    @Operation(
            summary = "Désactiver un centre de formation",
            description = "Permet de désactiver temporairement un centre (il ne sera plus visible dans la liste des centres actifs).",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Centre désactivé"),
                    @ApiResponse(responseCode = "404", description = "Centre non trouvé")
            }
    )

    @PutMapping("/{id}/desactiver")
    public ResponseEntity<?> desactiverCentre(@PathVariable int id) {
        try {
            return ResponseEntity.ok(centreFormationServices.desactiverCentre(id));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @Operation(
            summary = "Supprimer un centre de formation",
            description = "Supprime définitivement un centre de formation et ses relations associées.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Centre supprimé avec succès"),
                    @ApiResponse(responseCode = "404", description = "Centre non trouvé")
            }
    )

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCentre(@PathVariable int id) {
        try {
            centreFormationServices.deleteCentre(id);
            return ResponseEntity.ok("Centre supprimé avec succès.");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @Operation(
            summary = "Lister les formations d’un centre spécifique",
            description = "Retourne toutes les formations appartenant à un centre donné.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Liste des formations renvoyée avec succès"),
                    @ApiResponse(responseCode = "404", description = "Centre non trouvé")
            }
    )
    @GetMapping("/{id}/formations")
    public ResponseEntity<?> getFormationsByCentre(@PathVariable int id) {
        try {
            List<Formation> formations = centreFormationServices.getFormationsByCentre(id);
            return ResponseEntity.ok(formations);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @GetMapping("/me")
    public ResponseEntity<ResponseCentre> getCurrentCentre(Authentication authentication) {
        // L'email est automatiquement injecté par Spring Security via le token JWT
        String email = authentication.getName();
        System.out.println(email);
        ResponseCentre centre = centreFormationServices.getCentreByEmail(email);
        return ResponseEntity.ok(centre);
    }
}
