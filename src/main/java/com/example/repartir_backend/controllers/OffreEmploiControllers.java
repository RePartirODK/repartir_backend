package com.example.repartir_backend.controllers;

import com.example.repartir_backend.dto.OffreEmploiResponseDto;
import com.example.repartir_backend.services.OffreEmploiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/offres")
@RequiredArgsConstructor
public class OffreEmploiControllers {

    private final OffreEmploiService offreEmploiService;

    @GetMapping("/lister")
    public ResponseEntity<List<OffreEmploiResponseDto>> listerToutesLesOffres() {
        return ResponseEntity.ok(offreEmploiService.listerToutesLesOffres());
    }
}
