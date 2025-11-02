package com.example.repartir_backend.controllers;

import com.example.repartir_backend.dto.EntrepriseResponseDto;
import com.example.repartir_backend.services.EntrepriseServices;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/entreprises")
public class EntrepriseControllers {

    EntrepriseServices entrepriseServices;
    public EntrepriseControllers(EntrepriseServices entrepriseServices){
        this.entrepriseServices = entrepriseServices;
    }

    @GetMapping
    public ResponseEntity<List<EntrepriseResponseDto>> getAllEntreprises() {
        return ResponseEntity.ok(entrepriseServices.getAllEntreprises());
    }
}
