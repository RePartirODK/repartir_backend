package com.example.repartir_backend.controllers;

import com.example.repartir_backend.dto.DomaineDto;
import com.example.repartir_backend.dto.DomaineResponseDto;
import com.example.repartir_backend.entities.Domaine;
import com.example.repartir_backend.services.DomaineServices;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/domaines")
public class DomaineControllers {
    private final DomaineServices domaineServices;

    public DomaineControllers(DomaineServices domaineServices) {
        this.domaineServices = domaineServices;
    }

    @PostMapping("/creer")
    public Domaine creerDomaine(@RequestBody DomaineDto domaineDto) {
        return domaineServices.creerDomaine(domaineDto);
    }

    @GetMapping("/lister")
    public ResponseEntity<?> listerDomaines() {
        try {
            List<DomaineResponseDto> domaines = domaineServices.listerDomaines();
            return ResponseEntity.ok(domaines);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Une erreur est survenue lors de la récupération des domaines.");
        }
    }
}
