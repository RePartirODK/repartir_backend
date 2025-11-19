package com.example.repartir_backend.services;

import com.example.repartir_backend.dto.RefreshTokenDto;
import com.example.repartir_backend.entities.RefreshToken;
import com.example.repartir_backend.repositories.AdminRepository;
import com.example.repartir_backend.repositories.RefreshTokenRepository;
import com.example.repartir_backend.repositories.UtilisateurRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final AdminRepository adminRepository;
    private final BCryptPasswordEncoder encoder;
    public record RefreshTokenResponse(String token, Instant expiration) {}



    @Transactional
    public RefreshTokenResponse createRefreshToken(String email) {
        RefreshToken refreshToken;

        // Génération du token brut (non haché)
        String rawToken = UUID.randomUUID().toString();

        // Hachage avec BCrypt
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hashedToken = encoder.encode(rawToken);

        var userOpt = utilisateurRepository.findByEmail(email);

        if (userOpt.isPresent()) {
            var user = userOpt.get();

            // Supprime tout ancien refresh token lié à cet utilisateur
            System.out.println("ici");
            refreshTokenRepository.deleteByUtilisateur_Id(user.getId());
            refreshTokenRepository.flush();
            System.out.println("La valeur du refresh token est "+hashedToken);
            refreshToken = RefreshToken.builder()
                    .utilisateur(user)
                    .token(hashedToken) //on stocke uniquement la version hachée
                    .dateExpiration(Instant.now().plus(7, ChronoUnit.DAYS))
                    .build();

            refreshTokenRepository.save(refreshToken);
        } else {
            var admin = adminRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

            refreshTokenRepository.deleteByAdmin_Id(admin.getId());
            refreshTokenRepository.flush();
            System.out.println("Suppression effectué avec succès");
            refreshToken = RefreshToken.builder()
                    .admin(admin)
                    .token(hashedToken)
                    .dateExpiration(Instant.now().plus(7, ChronoUnit.DAYS))
                    .build();

            refreshTokenRepository.save(refreshToken);
        }
        return new RefreshTokenDto(
                rawToken,
                refreshToken.getDateExpiration()
        );
    }

    public Optional<RefreshToken> findByToken(String token)  {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return refreshTokenRepository.findAll().stream()
                .filter(rt -> encoder.matches(token, rt.getToken()))
                .findFirst();
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getDateExpiration().isBefore(Instant.now())) {
            refreshTokenRepository.delete(token);
            throw new RuntimeException("Refresh token expiré. Reconnectez-vous.");
        }
        return token;
    }
    public void deleteToken(String refreshToken){
        // On récupère tous les refresh tokens
        List<RefreshToken> allTokens = refreshTokenRepository.findAll();

        // On cherche celui dont le hash correspond
        RefreshToken tokenToDelete = allTokens.stream()
                .filter(rt -> encoder.matches(refreshToken, rt.getToken()))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Refresh token non trouvé."));

        refreshTokenRepository.delete(tokenToDelete);
    }

}
