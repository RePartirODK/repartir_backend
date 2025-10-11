package com.example.repartir_backend.controllers;

import com.example.repartir_backend.services.CentreFormationServices;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/centres")
public class CentreFormationControllers {

    CentreFormationServices centreFormationServices;
    public CentreFormationControllers(CentreFormationServices centreFormationServices)
    {
        this.centreFormationServices = centreFormationServices;
    }

}
