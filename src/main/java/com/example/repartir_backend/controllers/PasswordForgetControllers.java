package com.example.repartir_backend.controllers;

import com.example.repartir_backend.services.PassWordForget;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/password")
@RequiredArgsConstructor
public class PasswordForgetControllers {

    private final PassWordForget passWordForget;

    /**
     * Envoi du code par email
     */
    @PostMapping("/forget")
    public ResponseEntity<?> forgetPassword(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            if (email == null || email.isEmpty()) {
                return ResponseEntity.badRequest().body("Email requis !");
            }

            String message = passWordForget.passwordForget(email);
            return ResponseEntity.ok(message);

        } catch (MessagingException | IOException e) {
            return ResponseEntity.internalServerError().body("Erreur lors de l'envoi du mail : " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IllegalAccessError e){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    /**
     *Validation du code et changement du mot de passe
     * Body JSON :
     * {
     *   "email": "exemple@gmail.com",
     *   "code": "123456",
     *   "nouveauPassword": "NouveauMotDePasse123!"
     * }
     */
    @PostMapping("/reset")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            String code = request.get("code");
            String nouveauPassword = request.get("nouveauPassword");

            if (email == null || code == null || nouveauPassword == null) {
                return ResponseEntity.badRequest().body("Email, code et nouveau mot de passe sont requis !");
            }

            String message = passWordForget.modifierPassword(email, code, nouveauPassword);
            return ResponseEntity.ok(message);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
