package com.example.repartir_backend.controllers;

import com.example.repartir_backend.services.AdminServices;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admins")
public class AdminControllers {
    AdminServices adminServices;
    public AdminControllers(AdminServices adminServices){
        this.adminServices = adminServices;
    }

    @GetMapping
    public ResponseEntity test(){
        return ResponseEntity.ok("Test done");
    }
}
