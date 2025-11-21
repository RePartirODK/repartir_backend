package com.example.repartir_backend.controllers;

import com.example.repartir_backend.dto.RefreshRequest;
import com.example.repartir_backend.dto.RequestUtilisateur;
import com.example.repartir_backend.entities.Utilisateur;
import com.example.repartir_backend.repositories.RefreshTokenRepository;
import com.example.repartir_backend.security.JwtServices;
import com.example.repartir_backend.services.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentification", description = "Endpoints liés à la connexion et au rafraîchissement de token")
public class AuthentificationControllers {
    private final AuthService authService;

    /**
     * Authentification (Login)
     * Reçoit email + mot de passe, renvoie access_token + refresh_token
     */
    @Operation(
            summary = "Connexion d’un utilisateur",
            description = "Authentifie un utilisateur à partir de son email et mot de passe, et renvoie les tokens JWT (access & refresh).",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Authentification réussie"),
                    @ApiResponse(responseCode = "403", description = "Identifiants invalides",
                            content = @Content(schema = @Schema(example = "Email ou mot de passe incorrect"))),
            }
    )
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody RequestUtilisateur credentials) {
        try {
            Map<String, Object> tokens = authService.authenticate(
                    credentials.getEmail(),
                    credentials.getMotDePasse()
            );
            return ResponseEntity.ok(tokens);
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "status", 401,
                            "error", "Unauthorized",
                            "message", "Email ou mot de passe incorrect",
                            "path", "/api/auth/login"
                    ));
        }
    }

    /**
     * Rafraîchir le token d’accès (access_token)
     * Reçoit un refresh_token, renvoie un nouvel access_token
     */

    @Operation(
            summary = "Rafraîchir un token d’accès",
            description = "Reçoit un refresh_token valide et renvoie un nouveau access_token.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Token régénéré avec succès"),
                    @ApiResponse(responseCode = "403", description = "Refresh token invalide ou expiré")
            }
    )
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody RefreshRequest request) {
        Map<String, Object> tokens = authService.refreshAccessToken(request.getRefreshToken());
        return ResponseEntity.ok(tokens);
    }





}
