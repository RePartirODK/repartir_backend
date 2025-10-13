package com.example.repartir_backend.controllers;

import com.example.repartir_backend.dto.OffreEmploiDto;
import com.example.repartir_backend.entities.OffreEmploi;
import com.example.repartir_backend.services.OffreEmploiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<OffreEmploi> creerOffre(@RequestBody OffreEmploiDto offreDto) {
        OffreEmploi nouvelleOffre = offreEmploiService.creerOffre(offreDto);
        return ResponseEntity.ok(nouvelleOffre);
    }

    /**
     * Point de terminaison pour qu'une entreprise récupère la liste de ses propres offres.
     * @return Une réponse HTTP avec la liste des offres.
     */
    @GetMapping("/offres")
    public ResponseEntity<List<OffreEmploi>> listerMesOffres() {
        List<OffreEmploi> offres = offreEmploiService.listerOffresParEntreprise();
        return ResponseEntity.ok(offres);
    }
}
