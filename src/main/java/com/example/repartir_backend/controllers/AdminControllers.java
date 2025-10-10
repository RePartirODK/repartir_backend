package com.example.repartir_backend.controllers;

import com.example.repartir_backend.services.AdminServices;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admins")
public class AdminControllers {
<<<<<<< HEAD
    //injection de dépences
    AdminServices adminServices;
    AdminControllers(AdminServices adminServices){
=======
    AdminServices adminServices;
    public AdminControllers(AdminServices adminServices){
>>>>>>> b703b024a9f7a1036623707cddb9b6b525106f73
        this.adminServices = adminServices;
    }
}
