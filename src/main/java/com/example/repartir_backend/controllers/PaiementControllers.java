package com.example.repartir_backend.controllers;

import com.example.repartir_backend.dto.RequestPaiement;
import com.example.repartir_backend.services.PaiementServices;
import com.example.repartir_backend.services.ParrainServices;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/paiements")
@RequiredArgsConstructor
public class PaiementControllers {
    private final ParrainServices parrainServices;
    private final PaiementServices paiementServices;

    @PostMapping("/creer")
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
    public ResponseEntity<?> validerPaiement(@PathVariable int idPaiement) {
        try {
            paiementServices.validerPaiement(idPaiement);
            return ResponseEntity.ok("Paiement validé");
        }catch (EntityNotFoundException e)
        {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }catch (RuntimeException e)
        {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la validation du paiement" + e.getMessage());
        }
    }

    // refuser un paiement
    @PutMapping("/refuser/{idPaiement}")
    public ResponseEntity<?> refuserPaiement(@PathVariable int idPaiement) {
        try {
            paiementServices.refuserPaiement(idPaiement);
            return ResponseEntity.ok("Paiement Refusé");
        }catch (EntityNotFoundException e)
        {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }catch (RuntimeException e)
        {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la validation du paiement" + e.getMessage());
        }
    }

    //lister les paiements d'un jeune
    @GetMapping("/jeunes/{idJeune}")
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

}
