package com.example.repartir_backend.services;

import com.example.repartir_backend.dto.UpdatePasswordRequest;
import com.example.repartir_backend.entities.Admin;
import com.example.repartir_backend.entities.Utilisateur;
import com.example.repartir_backend.repositories.AdminRepository;
import com.example.repartir_backend.repositories.UtilisateurRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UpdatePassWordService {
    private final UtilisateurRepository utilisateurRepository;
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    public UpdatePassWordService(UtilisateurRepository utilisateurRepository,
                                AdminRepository adminRepository,
                                @Lazy PasswordEncoder passwordEncoder) {
        this.utilisateurRepository = utilisateurRepository;
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
    }
    @Transactional
    public void updateMotDePasse(int id, UpdatePasswordRequest request) {
        // Vérifier si c'est un utilisateur
        Utilisateur utilisateur = utilisateurRepository.findById(id).orElse(null);

        if (utilisateur != null) {
            // Vérifier l'ancien mot de passe
            if (!passwordEncoder.matches(request.getAncienMotDePasse(), utilisateur.getMotDePasse())) {
                throw new IllegalArgumentException("Ancien mot de passe incorrect !");
            }
            utilisateur.setMotDePasse(passwordEncoder.encode(request.getNouveauMotDePasse()));
            utilisateurRepository.save(utilisateur);
            return;
        }

        // Si ce n'est pas un utilisateur, on vérifie si c'est un admin
        Admin admin = adminRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Aucun utilisateur ou admin trouvé avec cet ID : " + id));

        // Vérifier l'ancien mot de passe de l'admin
        if (!passwordEncoder.matches(request.getAncienMotDePasse(), admin.getMotDePasse())) {
            throw new IllegalArgumentException("Ancien mot de passe incorrect !");
        }
        admin.setMotDePasse(passwordEncoder.encode(request.getNouveauMotDePasse()));
        adminRepository.save(admin);
    }

}
