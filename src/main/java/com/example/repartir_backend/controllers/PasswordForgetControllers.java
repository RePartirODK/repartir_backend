package com.example.repartir_backend.controllers;

import com.example.repartir_backend.services.PassWordForget;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Mot de passe", description = "Gestion du mot de passe oublié et réinitialisation")
public class PasswordForgetControllers {

    private final PassWordForget passWordForget;

    /**
     * Envoi du code par email
     */
    @Operation(
            summary = "Demande de réinitialisation du mot de passe",
            description = "Envoie un code de vérification par email pour réinitialiser le mot de passe.",

            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Email envoyé avec succès",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(example = "\"Code de réinitialisation envoyé à votre adresse email.\""))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Requête invalide ou email manquant",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(example = "\"Email requis !\""))
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Accès interdit",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(example = "\"Accès refusé à cette ressource.\""))
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Erreur interne (envoi d’email)",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(example = "\"Erreur lors de l'envoi du mail : ...\""))
                    )
            }
    )
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
    @Operation(
            summary = "Réinitialisation du mot de passe",
            description = "Permet à l'utilisateur de saisir le code reçu et un nouveau mot de passe.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Mot de passe réinitialisé avec succès",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(example = "\"Mot de passe modifié avec succès.\""))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Requête invalide (paramètres manquants ou code incorrect)",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(example = "\"Code invalide ou expiré.\""))
                    )
            }
    )
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
