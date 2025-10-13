package com.example.repartir_backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Classe de configuration pour les beans de l'application.
 */
@Configuration
public class Config {
    /**
     * Crée un bean pour l'encodeur de mots de passe.
     * Utilise BCrypt pour un hachage sécurisé des mots de passe.
     * @return Une instance de PasswordEncoder.
     */
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
