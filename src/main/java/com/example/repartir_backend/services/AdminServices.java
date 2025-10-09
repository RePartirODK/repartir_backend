package com.example.repartir_backend.services;

import com.example.repartir_backend.repositories.AdminRepository;
import org.springframework.stereotype.Service;

@Service
public class AdminServices {
    AdminRepository adminRepository;
    public AdminServices(AdminRepository adminRepository){
        this.adminRepository = adminRepository;
    }
}
