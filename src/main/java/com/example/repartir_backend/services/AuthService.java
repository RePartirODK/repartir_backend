package com.example.repartir_backend.services;

import com.example.repartir_backend.entities.RefreshToken;
import com.example.repartir_backend.repositories.AdminRepository;
import com.example.repartir_backend.repositories.UtilisateurRepository;
import com.example.repartir_backend.security.JwtServices;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtServices jwtService;
    private final RefreshTokenService refreshTokenService;
    private final UtilisateurRepository utilisateurRepository;
    private final AdminRepository adminRepository;

    /**
     * Authentifie l'utilisateur et génère les tokens
     */
    public Map<String, Object> authenticate(String email, String password) {
        try {
            // Vérifie les identifiants via Spring Security
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );

            if (authentication.isAuthenticated()) {

                // Récupère les infos de l’utilisateur
                UserDetails userDetails = (UserDetails) authentication.getPrincipal();

                // Génère un access token
                String accessToken = jwtService.genererToken(email);

                // Crée un refresh token et le sauvegarde
                RefreshToken refreshToken = refreshTokenService.createRefreshToken(email);

                // Prépare la réponse JSON
                Map<String, Object> response = new HashMap<>();
                response.put("access_token", accessToken);
                response.put("refresh_token", refreshToken.getToken());
                response.put("email", userDetails.getUsername());
                response.put("role", userDetails.getAuthorities());


                return response;
            } else {
                throw new RuntimeException("Authentification échouée : utilisateur non reconnu.");
            }

        } catch(BadCredentialsException e1)
        {
            throw new RuntimeException("Email ou mot de passe incorrecte");
        }
    }

    /**
     * Rafraîchit un access token à partir du refresh token
     */
    public Map<String, Object> refreshAccessToken(String refreshTokenString) {
        RefreshToken refreshToken = refreshTokenService.findByToken(refreshTokenString)
                .map(refreshTokenService::verifyExpiration)
                .orElseThrow(() -> new RuntimeException("Refresh token invalide."));

        String email;
        if (refreshToken.getUtilisateur() != null) {
            email = refreshToken.getUtilisateur().getEmail();
        } else if (refreshToken.getAdmin() != null) {
            email = refreshToken.getAdmin().getEmail();
        } else {
            throw new RuntimeException("Aucun utilisateur associé au refresh token.");
        }

        // Génère un nouveau access JWT
        String newAccessToken = jwtService.genererToken(email);

        Map<String, Object> response = new HashMap<>();
        response.put("access_token", newAccessToken);
        response.put("refresh_token", refreshTokenString);
        return response;
    }
}
