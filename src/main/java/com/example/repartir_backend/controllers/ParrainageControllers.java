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

import com.example.repartir_backend.dto.ParrainageDto;
import com.example.repartir_backend.services.ParrainageServices;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/parrainage")
@RequiredArgsConstructor
public class ParrainageControllers {

    private final ParrainageServices parrainageService;

    @GetMapping("/demandes")
    @PreAuthorize("hasRole('PARRAIN')")
    public ResponseEntity<List<ParrainageDto>> listerDemandesDeParrainage() {
        return ResponseEntity.ok(parrainageService.listerDemandes());
    }
}

