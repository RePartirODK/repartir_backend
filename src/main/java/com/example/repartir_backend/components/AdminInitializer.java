package com.example.repartir_backend.components;

import com.example.repartir_backend.entities.Admin;
import com.example.repartir_backend.entities.Utilisateur;
import com.example.repartir_backend.enumerations.Etat;
import com.example.repartir_backend.enumerations.Role;
import com.example.repartir_backend.repositories.AdminRepository;
import com.example.repartir_backend.repositories.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AdminInitializer implements CommandLineRunner {

    private final UtilisateurRepository utilisateurRepository;
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${default.admin.email}")
    private String email;
    @Value("${default.admin.password}")
    private String motDePasse;
    @Override
    public void run(String... args) throws Exception {
        // Le système nécessite un admin dans 2 tables pour fonctionner :
        // 1. Un 'Utilisateur' avec Role.ADMIN pour les notifications et la logique interne.
        // 2. Une entité 'Admin' pour la connexion de l'administrateur.
        // Cette méthode assure que les deux sont présents au démarrage.

        // Étape 1 : Créer l'Utilisateur ADMIN s'il n'existe pas.
        List<Admin> adminList = adminRepository.findAll();
        if (adminList.isEmpty()) {
            Admin adminUser = new Admin();
            adminUser.setNom("Admin_System");
            adminUser.setPrenom("Admin_System");
            adminUser.setEmail(email);
            adminUser.setMotDePasse(passwordEncoder.encode(motDePasse)); // Le mot de passe n'est pas utilisé ici, mais le champ est obligatoire
            adminUser.setRole(Role.ADMIN);
            adminRepository.save(adminUser);
            System.out.println("Utilisateur système ADMIN créé pour les notifications.");
        }
    }
}
