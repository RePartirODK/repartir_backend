package com.example.repartir_backend.controllers;

import com.example.repartir_backend.services.MailSendServices;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mails")
public class MailSendControllers {
    private final MailSendServices mailSendServices;
    
    @PostMapping("/test")
    public ResponseEntity<String> testEmail(@RequestParam String to) {
        try {
            mailSendServices.envoieSimpleMail(
                to, 
                "Test d'envoi d'email", 
                "Ceci est un email de test pour vérifier que le système d'envoi d'emails fonctionne correctement."
            );
            return ResponseEntity.ok("Email de test envoyé avec succès à " + to);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Erreur lors de l'envoi de l'email: " + e.getMessage());
        }
    }
}