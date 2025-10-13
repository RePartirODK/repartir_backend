package com.example.repartir_backend.components;

import com.example.repartir_backend.entities.Admin;
import com.example.repartir_backend.enumerations.Role;
import com.example.repartir_backend.repositories.AdminRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Ce composant initialise un administrateur par défaut au démarrage de l'application.
 */
@Component
@RequiredArgsConstructor
public class AdminInitializer {

    private final AdminRepository adminRepository;

    private final PasswordEncoder passwordEncoder;

    /**
     * Cette méthode est exécutée après l'injection des dépendances.
     * Elle vérifie si un administrateur avec l'email "booba@gmail.com" existe.
     * Si ce n'est pas le cas, elle en crée un nouveau avec un mot de passe prédéfini.
     */
    @PostConstruct
    public void initAdmin() {
        Optional<Admin> adminOptional = adminRepository.findByEmail("booba@gmail.com");

        if (adminOptional.isEmpty()) {
            Admin admin = new Admin();
            admin.setEmail("booba@gmail.com");
            admin.setMotDePasse(passwordEncoder.encode("booba12"));
            admin.setNom("Booba");
            admin.setPrenom("Booba");
            admin.setRole(Role.ADMIN);
            adminRepository.save(admin);
        }
    }
}
