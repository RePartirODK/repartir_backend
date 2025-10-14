package com.example.repartir_backend.controllers;
import com.example.repartir_backend.dto.ResponseParrainage;
import com.example.repartir_backend.entities.Paiement;
import com.example.repartir_backend.services.ParrainageServices;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/parrainages")
@RequiredArgsConstructor
public class ParrainageControllers {

    private final ParrainageServices parrainageServices;

    /**
     * Créer un parrainage
     */
    @PostMapping("/create")
    public ResponseEntity<?> creerParrainage(
            @RequestParam int idJeune,
            @RequestParam int idParrain,
            @RequestParam int idFormation
    ) {
        try {
            ResponseParrainage parrainage = parrainageServices.creerParrainage(idJeune, idParrain, idFormation);
            return ResponseEntity.status(HttpStatus.CREATED).body(parrainage);
        } catch (EntityExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de la création du parrainage");
        }
    }

    /**
     * Récupérer tous les parrainages
     */
    @GetMapping
    public ResponseEntity<List<ResponseParrainage>> getAllParrainages() {
        List<ResponseParrainage> parrainages = parrainageServices.getAllParrainages();
        return ResponseEntity.ok(parrainages);
    }

    /**
     * Récupérer par jeune
     */
    @GetMapping("/jeune/{idJeune}")
    public ResponseEntity<?> getParrainagesByJeune(@PathVariable int idJeune) {
        try {
            return ResponseEntity.ok(parrainageServices.getParrainagesByJeune(idJeune));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * Récupérer par parrain
     */
    @GetMapping("/parrain/{idParrain}")
    public ResponseEntity<?> getParrainagesByParrain(@PathVariable int idParrain) {
        try {
            return ResponseEntity.ok(parrainageServices.getParrainagesByParrain(idParrain));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * Récupérer par formation
     */
    @GetMapping("/formation/{idFormation}")
    public ResponseEntity<?> getParrainagesByFormation(@PathVariable int idFormation) {
        try {
            return ResponseEntity.ok(parrainageServices.getParrainagesByFormation(idFormation));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * Supprimer un parrainage
     */
    @DeleteMapping("/{idParrainage}")
    public ResponseEntity<?> deleteParrainage(@PathVariable int idParrainage) {
        try {
            parrainageServices.deleteParrainage(idParrainage);
            return ResponseEntity.ok("Parrainage supprimé avec succès");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * Récupérer les paiements liés à un parrainage
     */
    @GetMapping("/{idParrainage}/paiements")
    public ResponseEntity<?> getPaiementsByParrainage(@PathVariable int idParrainage) {
        try {
            List<Paiement> paiements = parrainageServices.getPaiementsByParrainage(idParrainage);
            return ResponseEntity.ok(paiements);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}

