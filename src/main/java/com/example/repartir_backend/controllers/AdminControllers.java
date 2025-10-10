package com.example.repartir_backend.controllers;

import com.example.repartir_backend.services.AdminServices;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admins")
public class AdminControllers {
    //injection de dépences
    AdminServices adminServices;
    AdminControllers(AdminServices adminServices){
        this.adminServices = adminServices;
    }
}
