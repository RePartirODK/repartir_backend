package com.example.repartir_backend.controllers;

import com.example.repartir_backend.dto.DomaineDto;
import com.example.repartir_backend.entities.Domaine;
import com.example.repartir_backend.services.DomaineServices;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
