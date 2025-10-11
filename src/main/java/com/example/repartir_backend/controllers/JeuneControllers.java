package com.example.repartir_backend.controllers;

import com.example.repartir_backend.services.JeuneServices;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/jeunes")
public class JeuneControllers {
    JeuneServices jeuneServices;
    public JeuneControllers(JeuneServices jeuneServices){
        this.jeuneServices = jeuneServices;
    }

}
