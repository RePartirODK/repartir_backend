package com.example.repartir_backend.services;

import com.example.repartir_backend.entities.RefreshToken;
import com.example.repartir_backend.repositories.AdminRepository;
import com.example.repartir_backend.repositories.RefreshTokenRepository;
import com.example.repartir_backend.repositories.UtilisateurRepository;
import com.example.repartir_backend.security.JwtServices;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
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
    private final UserDetailsService userDetailsService;
    private final RefreshTokenRepository refreshTokenRepository;

    /**
     * Authentifie l'utilisateur et génère les tokens
     */
    public Map<String, Object> authenticate(String email, String password) {
        try {
            // Authentifie l’utilisateur via Spring Security
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );

            if (!authentication.isAuthenticated()) {
                throw new RuntimeException("Authentification échouée : utilisateur non reconnu.");
            }

            // Récupère les infos de l’utilisateur
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            // Génère un access token
            String accessToken = jwtService.genererToken(userDetails);

            // Crée un refresh token (gestion interne des doublons dans le service)
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(email);

            // Réponse JSON propre
            Map<String, Object> response = new HashMap<>();
            response.put("access_token", accessToken);
            response.put("refresh_token", refreshToken.getToken());
            response.put("email", userDetails.getUsername());
            response.put("role", userDetails.getAuthorities());

            return response;

        } catch (BadCredentialsException e) {
            throw new RuntimeException("Email ou mot de passe incorrect.");
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("Erreur interne : le refresh token existe déjà.");
        } catch (Exception e) {
            throw new RuntimeException("Erreur inattendue : " + e.getMessage());
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

        // Charger les détails de l'utilisateur pour inclure le rôle dans le nouveau token
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        // Génère un nouveau access JWT
        String newAccessToken = jwtService.genererToken(userDetails);

        Map<String, Object> response = new HashMap<>();
        response.put("access_token", newAccessToken);
        response.put("refresh_token", refreshTokenString);
        return response;
    }

    @Transactional
    public void logout(String email){
        utilisateurRepository.findByEmail(email).ifPresent(user -> {
            refreshTokenRepository.deleteByUtilisateur_Id(user.getId());
        });

        adminRepository.findByEmail(email).ifPresent(admin -> {
            refreshTokenRepository.deleteByAdmin_Id(admin.getId());
        });
    }
}
