package com.example.repartir_backend.controllers;

import com.example.repartir_backend.services.UtilisateurServices;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/utilisateurs")
public class UtilisateurControllers {
    UtilisateurServices utilisateurServices;
    public UtilisateurControllers(UtilisateurServices utilisateurServices){
        this.utilisateurServices = utilisateurServices;
    }
}
