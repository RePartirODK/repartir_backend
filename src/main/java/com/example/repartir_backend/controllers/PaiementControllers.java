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
            paiementServices.validerPaiement(idPaiement);
            return ResponseEntity.ok("Paiement validé");
        }catch (EntityNotFoundException e)
        {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }catch (RuntimeException | MessagingException e)
        {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la validation du paiement" + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la validation du paiement" + e.getMessage());
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
            paiementServices.refuserPaiement(idPaiement);
            return ResponseEntity.ok("Paiement Refusé");
        }catch (EntityNotFoundException e)
        {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la validation du paiement" + e.getMessage());
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

}
