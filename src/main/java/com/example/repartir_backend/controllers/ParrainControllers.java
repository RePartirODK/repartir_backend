package com.example.repartir_backend.controllers;

import com.example.repartir_backend.dto.ResponseParrain;
import com.example.repartir_backend.entities.Parrain;
import com.example.repartir_backend.services.ParrainServices;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/parrains")
@RequiredArgsConstructor
public class ParrainControllers {

    private final ParrainServices parrainServices;

    // GET /api/parrains
    @GetMapping
    public ResponseEntity<List<ResponseParrain>> getAllParrains() {
        return ResponseEntity.ok(parrainServices.getAllParrains());
    }

    // GET /api/parrains/actifs
    @GetMapping("/actifs")
    public ResponseEntity<List<ResponseParrain>> getParrainsActifs() {
        return ResponseEntity.ok(parrainServices.getParrainsActifs());
    }

    // GET /api/parrains/{id}
    @GetMapping("/{id}")
    public ResponseEntity<ResponseParrain> getParrainById(@PathVariable int id) {
        try {
            return ResponseEntity.ok(parrainServices.getParrainById(id));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // GET /api/parrains/email/{email}
    @GetMapping("/email/{email}")
    public ResponseEntity<ResponseParrain> getParrainByEmail(@PathVariable String email) {
        try {
            return ResponseEntity.ok(parrainServices.getParrainByEmail(email));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // PUT /api/parrains/{id}
    @PutMapping("/{id}")
    public ResponseEntity<ResponseParrain> updateParrain(
            @PathVariable int id,
            //body doit avoir un requestparrain
            @RequestBody Parrain parrainDetails
    ) {
        try {
            return ResponseEntity.ok(parrainServices.updateParrain(id, parrainDetails));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // DELETE /api/parrains/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteParrain(@PathVariable int id) {
        try {
            parrainServices.deleteParrain(id);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}

