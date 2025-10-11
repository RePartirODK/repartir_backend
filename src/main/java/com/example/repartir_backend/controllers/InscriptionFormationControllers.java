package com.example.repartir_backend.controllers;

import com.example.repartir_backend.services.InscriptionFormationServices;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/inscriptions")
public class InscriptionFormationControllers {
    private final InscriptionFormationServices inscriptionFormationServices;

    public InscriptionFormationControllers(InscriptionFormationServices inscriptionFormationServices) {
        this.inscriptionFormationServices = inscriptionFormationServices;
    }
}
