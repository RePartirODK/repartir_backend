package com.example.repartir_backend.services;

import com.example.repartir_backend.entities.Admin;
import com.example.repartir_backend.enumerations.Role;
import com.example.repartir_backend.repositories.AdminRepository;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminServices {
    AdminRepository adminRepository;
    public AdminServices(AdminRepository adminRepository){
        this.adminRepository = adminRepository;
    }

    //les informations de l'admin par défaut
    private final String nom = "repartir";
    private final String prenom = "repartir";
    private final String email = "repartirmli2025@gmail.com";
    private final String motDePasse = "repartir_2025@";

    // default admin
    public void Admin() {
        List<Admin> adminList = adminRepository.findAll();
        if (adminList.isEmpty()) {
            System.out.println("Création d'un admin");
            System.out.println(email);
            Admin admin = new Admin();
            admin.setEmail(email);
            admin.setPrenom(prenom);
            admin.setNom(nom);
            admin.setMotDePasse(BCrypt.hashpw(motDePasse, BCrypt.gensalt()));
            admin.setRole(Role.ADMIN);
            adminRepository.save(admin);
        }
    }
}
