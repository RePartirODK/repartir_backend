package com.example.repartir_backend.controllers;

import com.example.repartir_backend.dto.OffreEmploiDto;
import com.example.repartir_backend.dto.OffreEmploiResponseDto;
import com.example.repartir_backend.entities.OffreEmploi;
import com.example.repartir_backend.services.OffreEmploiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;

/**
 * Contrôleur pour les actions spécifiques aux entreprises.
 */
@RestController
@RequestMapping("/api/entreprises")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ENTREPRISE')")
@Tag(name = "Entreprises", description = "Endpoints permettant aux entreprises de gérer leurs offres d’emploi")

public class EntrepriseActionControllers {

    private final OffreEmploiService offreEmploiService;

    /**
     * Point de terminaison pour qu'une entreprise crée une nouvelle offre d'emploi.
     * @param offreDto Les détails de l'offre à créer.
     * @return Une réponse HTTP avec l'offre créée.
     */
    @Operation(
            summary = "Créer une offre d’emploi",
            description = "Permet à une entreprise connectée de créer une nouvelle offre d’emploi."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Offre créée avec succès"),
            @ApiResponse(responseCode = "403", description = "Accès refusé — l’utilisateur n’a pas le rôle ENTREPRISE"),
            @ApiResponse(responseCode = "400", description = "Requête invalide")
    })
    @PostMapping("/offres/creer")
    public ResponseEntity<OffreEmploiResponseDto> creerOffre(@RequestBody OffreEmploiDto offreDto) {
        OffreEmploi nouvelleOffre = offreEmploiService.creerOffre(offreDto);
        return ResponseEntity.ok(OffreEmploiResponseDto.fromEntity(nouvelleOffre));
    }

    /**
     * Point de terminaison pour qu'une entreprise récupère la liste de ses propres offres.
     * @return Une réponse HTTP avec la liste des offres.
     */
    @Operation(
            summary = "Lister les offres d’emploi de l’entreprise connectée",
            description = "Récupère toutes les offres d’emploi créées par l’entreprise actuellement connectée."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des offres récupérée avec succès"),
            @ApiResponse(responseCode = "403", description = "Accès refusé — l’utilisateur n’a pas le rôle ENTREPRISE")
    })
    @GetMapping("/offres")
    public ResponseEntity<List<OffreEmploiResponseDto>> listerMesOffres() {
        List<OffreEmploi> offres = offreEmploiService.listerOffresParEntreprise();
        return ResponseEntity.ok(OffreEmploiResponseDto.fromEntities(offres));
    }

    /**
     * Point de terminaison pour qu'une entreprise supprime une de ses offres d'emploi.
     * @param offreId L'ID de l'offre à supprimer.
     * @return Une réponse HTTP indiquant le succès ou l'échec de l'opération.
     */
    @Operation(
            summary = "Supprimer une offre d’emploi",
            description = "Permet à une entreprise de supprimer l’une de ses offres d’emploi existantes."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Offre supprimée avec succès"),
            @ApiResponse(responseCode = "403", description = "Accès refusé — l’utilisateur n’est pas propriétaire de l’offre"),
            @ApiResponse(responseCode = "404", description = "Offre introuvable")
    })
    @DeleteMapping("/offres/supprimer/{offreId}")
    public ResponseEntity<?> supprimerOffre(@PathVariable int offreId) {
        try {
            offreEmploiService.supprimerOffre(offreId);
            return ResponseEntity.ok(Map.of("message", "Offre supprimée avec succès."));
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }
}
