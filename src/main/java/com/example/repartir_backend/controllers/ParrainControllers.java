package com.example.repartir_backend.controllers;

<<<<<<< HEAD
=======
import com.example.repartir_backend.services.ParrainServices;
>>>>>>> b703b024a9f7a1036623707cddb9b6b525106f73
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
