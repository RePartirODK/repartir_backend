package com.example.repartir_backend.components;

import com.example.repartir_backend.entities.Admin;
import com.example.repartir_backend.entities.Utilisateur;
import com.example.repartir_backend.enumerations.Etat;
import com.example.repartir_backend.enumerations.Role;
import com.example.repartir_backend.repositories.AdminRepository;
import com.example.repartir_backend.repositories.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminInitializer implements CommandLineRunner {

    private final UtilisateurRepository utilisateurRepository;
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Le système nécessite un admin dans 2 tables pour fonctionner :
        // 1. Un 'Utilisateur' avec Role.ADMIN pour les notifications et la logique interne.
        // 2. Une entité 'Admin' pour la connexion de l'administrateur.
        // Cette méthode assure que les deux sont présents au démarrage.

        // Étape 1 : Créer l'Utilisateur ADMIN s'il n'existe pas.
        if (utilisateurRepository.findByRole(Role.ADMIN).isEmpty()) {
            Utilisateur adminUser = new Utilisateur();
            adminUser.setNom("Admin_Systeme");
            adminUser.setEmail("admin@repartir.com");
            adminUser.setMotDePasse(passwordEncoder.encode("admin")); // Le mot de passe n'est pas utilisé ici, mais le champ est obligatoire
            adminUser.setTelephone("0000000000");
            adminUser.setRole(Role.ADMIN);
            adminUser.setEtat(Etat.VALIDE);
            adminUser.setEstActive(true);
            utilisateurRepository.save(adminUser);
            System.out.println(">>> Utilisateur système ADMIN créé pour les notifications.");
        } else {
            System.out.println(">>> L'utilisateur système ADMIN existe déjà.");
        }

        // Étape 2 : Créer l'entité Admin pour la connexion si elle n'existe pas.
        if (adminRepository.count() == 0) {
            Admin admin = new Admin();
            admin.setNom("Admin");
            admin.setPrenom("Super");
            admin.setEmail("admin@repartir.com");
            admin.setMotDePasse(passwordEncoder.encode("admin"));
            admin.setRole(Role.ADMIN);
            adminRepository.save(admin);
            System.out.println(">>> Entité Admin créée pour la connexion.");
        } else {
            System.out.println(">>> L'entité Admin pour la connexion existe déjà.");
        }
    }
}
