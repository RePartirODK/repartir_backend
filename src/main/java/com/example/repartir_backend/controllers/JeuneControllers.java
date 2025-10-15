package com.example.repartir_backend.controllers;

import com.example.repartir_backend.dto.UpdateJeuneDto;
import com.example.repartir_backend.entities.Jeune;
import com.example.repartir_backend.services.JeuneServices;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/jeunes")
public class JeuneControllers {
    private final JeuneServices jeuneServices;
    public JeuneControllers(JeuneServices jeuneServices){
        this.jeuneServices = jeuneServices;
    }

    @PutMapping("/modifier")
    @PreAuthorize("hasRole('JEUNE')")
    public ResponseEntity<Jeune> updateJeuneProfile(@RequestBody UpdateJeuneDto updateDto) {
        Jeune updatedJeune = jeuneServices.updateJeune(updateDto);
        return ResponseEntity.ok(updatedJeune);
    }

    @DeleteMapping("/supprimer")
    @PreAuthorize("hasRole('JEUNE')")
    public ResponseEntity<?> deleteJeuneProfile() {
        jeuneServices.deleteJeune();
        return ResponseEntity.ok(Map.of("message", "Compte jeune supprimé avec succès."));
    }
}
