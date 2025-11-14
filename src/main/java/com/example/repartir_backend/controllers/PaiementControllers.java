package com.example.repartir_backend.controllers;

import com.example.repartir_backend.dto.RequestPaiement;
import com.example.repartir_backend.dto.ResponseParrain;
import com.example.repartir_backend.services.PaiementServices;
import com.example.repartir_backend.services.ParrainServices;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/paiements")
@RequiredArgsConstructor
@Tag(name = "Paiements", description = "Endpoints pour la gestion des paiements (création, validation, refus, consultation)")

public class PaiementControllers {
    private final ParrainServices parrainServices;
    private final PaiementServices paiementServices;

    @PostMapping("/creer")
    @Operation(summary = "Créer un paiement", description = "Permet de créer un paiement pour une inscription donnée")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Paiement créé avec succès"),
            @ApiResponse(responseCode = "404", description = "Utilisateur ou inscription non trouvé", content = @Content),
            @ApiResponse(responseCode = "500", description = "Erreur interne lors de la création", content = @Content)
    })
    public ResponseEntity<?> creerPaiement(@RequestBody RequestPaiement request) {
        try {
            return ResponseEntity.ok(paiementServices.creerPaiement(request));
        }catch (EntityNotFoundException e)
        {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la creation" + e.getMessage());
        }
    }

    //valider un paiement
    @PutMapping("/valider/{idPaiement}")
    @Operation(summary = "Valider un paiement", description = "Permet de valider un paiement existant")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Paiement validé avec succès"),
            @ApiResponse(responseCode = "404", description = "Paiement non trouvé", content = @Content),
            @ApiResponse(responseCode = "500", description = "Erreur lors de la validation", content = @Content)
    })
    public ResponseEntity<?> validerPaiement(@PathVariable int idPaiement) {
        try {
            String result = paiementServices.validerPaiement(idPaiement);
            
            // ✅ Retourner un objet JSON au lieu d'une chaîne
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Paiement validé avec succès");
            response.put("success", true);
            response.put("details", result);
            
            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "Paiement non trouvé");
            error.put("success", false);
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } catch (RuntimeException | MessagingException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "Erreur lors de la validation du paiement");
            error.put("success", false);
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "Erreur lors de la validation du paiement");
            error.put("success", false);
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    // refuser un paiement
    @PutMapping("/refuser/{idPaiement}")
    @Operation(summary = "Refuser un paiement", description = "Permet de refuser un paiement existant")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Paiement refusé avec succès"),
            @ApiResponse(responseCode = "404", description = "Paiement non trouvé", content = @Content),
            @ApiResponse(responseCode = "500", description = "Erreur lors du refus du paiement", content = @Content)
    })
    public ResponseEntity<?> refuserPaiement(@PathVariable int idPaiement) {
        try {
            String result = paiementServices.refuserPaiement(idPaiement);
            
            // ✅ Retourner un objet JSON
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Paiement refusé avec succès");
            response.put("success", true);
            response.put("details", result);
            
            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "Paiement non trouvé");
            error.put("success", false);
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "Erreur lors du refus du paiement");
            error.put("success", false);
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    //lister les paiements d'un jeune
    @GetMapping("/jeunes/{idJeune}")
    @Operation(summary = "Lister les paiements d’un jeune", description = "Récupère tous les paiements effectués par un jeune spécifique")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès"),
            @ApiResponse(responseCode = "500", description = "Erreur lors de la récupération", content = @Content)
    })
    public ResponseEntity<?> getPaiementParJeune(@PathVariable int idJeune){
        try {
            return ResponseEntity.ok(paiementServices.getPaiementByJeune(idJeune));
        }catch (RuntimeException e)
        {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Une erreur est survenue lors de la recuperation" + e.getMessage());
        }
    }

    //lister les paiements d'une formation

    //lister tous les paiements d'une inscription
    @GetMapping("/inscription/{idInscription}")
    @Operation(summary = "Lister les paiements d’une inscription", description = "Récupère tous les paiements liés à une inscription donnée")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès"),
            @ApiResponse(responseCode = "500", description = "Erreur lors de la récupération", content = @Content)
    })
    public ResponseEntity<?> getAllPaiement(
            @PathVariable int idInscription
    )
    {
        try {
            return ResponseEntity.ok(paiementServices.getPaiementsParInscription(idInscription));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Une erreur est survenue lors de la recupération" + e.getMessage());
        }
    }

    @GetMapping("/parrains/me/total")
    @PreAuthorize("hasRole('PARRAIN')")
    @Operation(summary = "Total des donations du parrain courant", description = "Retourne la somme des paiements validés liés aux parrainages du parrain connecté")
    public ResponseEntity<?> getTotalPourParrain(Authentication authentication) {
        try {
            String email = authentication.getName();
            ResponseParrain current = parrainServices.getParrainByEmail(email);
            double total = paiementServices.getTotalDonationsByParrain(current.getId());
            return ResponseEntity.ok(total);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur : " + e.getMessage());
        }
    }

    // Lister tous les paiements (pour l'admin)
    @GetMapping("/tous")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Lister tous les paiements", description = "Récupère tous les paiements de la plateforme (réservé aux administrateurs)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès"),
            @ApiResponse(responseCode = "403", description = "Accès refusé - rôle ADMIN requis", content = @Content),
            @ApiResponse(responseCode = "500", description = "Erreur lors de la récupération", content = @Content)
    })
    public ResponseEntity<?> getAllPaiements() {
        try {
            return ResponseEntity.ok(paiementServices.getAllPaiements());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Une erreur est survenue lors de la récupération : " + e.getMessage());
        }
    }

}
