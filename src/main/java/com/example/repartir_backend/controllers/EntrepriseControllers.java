package com.example.repartir_backend.controllers;

import com.example.repartir_backend.services.EntrepriseServices;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/entreprises")
public class EntrepriseControllers {

    EntrepriseServices entrepriseServices;
    public EntrepriseControllers(EntrepriseServices entrepriseServices){
        this.entrepriseServices = entrepriseServices;
    }
}
