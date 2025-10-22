package com.example.repartir_backend.controllers;

import com.example.repartir_backend.dto.LogoutRequest;
import com.example.repartir_backend.services.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/logout")
@RequiredArgsConstructor
@Tag(name = "Authentification", description = "Gestion de la connexion et déconnexion des utilisateurs")
public class LogoutControllers {
    private final AuthService authService;

    @Operation(
            summary = "Déconnexion d’un utilisateur",
            description = "Permet de déconnecter un utilisateur en supprimant ses tokens actifs.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Déconnexion réussie",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(example = "\"Déconnecté avec succès\""))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Utilisateur non trouvé",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(example = "\"Utilisateur non trouvé\""))
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Erreur interne du serveur",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(example = "\"Une erreur s'est produite lors de la déconnexion\""))
                    )
            }
    )
    @DeleteMapping
    public ResponseEntity<?> logout(@RequestBody LogoutRequest request)
    {
        try {
            authService.logout(request.getEmail());
            return ResponseEntity.ok("Deconnecté");
        }catch (EntityNotFoundException e)
        {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Une erreur s'est produite");
        }

    }
}
