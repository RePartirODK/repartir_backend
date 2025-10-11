package com.example.repartir_backend.controllers;

import com.example.repartir_backend.services.OffreEmploiService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/offres")
@RequiredArgsConstructor
public class OffreEmploiControllers {
    private final OffreEmploiService offreEmploiService;
}
