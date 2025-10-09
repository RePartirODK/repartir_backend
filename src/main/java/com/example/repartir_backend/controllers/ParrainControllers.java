package com.example.repartir_backend.controllers;

import com.example.repartir_backend.services.ParrainServices;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/parrains")
public class ParrainControllers {
    ParrainServices parrainServices;
    public ParrainControllers(ParrainServices parrainServices){
        this.parrainServices = parrainServices;
    }
}
