package com.example.repartir_backend.controllers;

import com.example.repartir_backend.services.DomaineServices;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/domaines")
public class DomaineControllers {
    private final DomaineServices domaineServices;

    public DomaineControllers(DomaineServices domaineServices) {
        this.domaineServices = domaineServices;
    }
}
