package com.example.repartir_backend.controllers;

import com.example.repartir_backend.services.PaiementServices;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/parrainage")
@RequiredArgsConstructor
public class ParrainageControllers {
    private final PaiementServices paiementServices;
}
