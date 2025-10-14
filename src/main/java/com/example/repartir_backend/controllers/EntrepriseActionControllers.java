package com.example.repartir_backend.controllers;

import com.example.repartir_backend.dto.OffreEmploiDto;
import com.example.repartir_backend.dto.OffreEmploiResponseDto;
import com.example.repartir_backend.entities.OffreEmploi;
import com.example.repartir_backend.services.OffreEmploiService;
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
@RequestMapping("/api/entreprise")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ENTREPRISE')")
public class EntrepriseActionControllers {

    private final OffreEmploiService offreEmploiService;

    /**
     * Point de terminaison pour qu'une entreprise crée une nouvelle offre d'emploi.
     * @param offreDto Les détails de l'offre à créer.
     * @return Une réponse HTTP avec l'offre créée.
     */
    @PostMapping("/offres/creer")
    public ResponseEntity<OffreEmploiResponseDto> creerOffre(@RequestBody OffreEmploiDto offreDto) {
        OffreEmploi nouvelleOffre = offreEmploiService.creerOffre(offreDto);
        return ResponseEntity.ok(OffreEmploiResponseDto.fromEntity(nouvelleOffre));
    }

    /**
     * Point de terminaison pour qu'une entreprise récupère la liste de ses propres offres.
     * @return Une réponse HTTP avec la liste des offres.
     */
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
