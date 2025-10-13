package com.example.repartir_backend.controllers;

import com.example.repartir_backend.dto.RefreshRequest;
import com.example.repartir_backend.dto.RequestUtilisateur;
import com.example.repartir_backend.entities.Utilisateur;
import com.example.repartir_backend.security.JwtServices;
import com.example.repartir_backend.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthentificationControllers {
    private final AuthService authService;

    /**
     * Authentification (Login)
     * Reçoit email + mot de passe, renvoie access_token + refresh_token
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody RequestUtilisateur credentials) {
        Map<String, Object> tokens = authService.authenticate(
                credentials.getEmail(),
                credentials.getMotDePasse()
        );
        return ResponseEntity.ok(tokens);
    }

    /**
     * Rafraîchir le token d’accès (access_token)
     * Reçoit un refresh_token, renvoie un nouvel access_token
     */
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody RefreshRequest request) {
        Map<String, Object> tokens = authService.refreshAccessToken(request.getRefreshToken());
        return ResponseEntity.ok(tokens);
    }



    public static record JwtResponse(String token, String email, Object roles) {}
}
