package com.example.repartir_backend.services;

import com.example.repartir_backend.dto.AdminDto;
import com.example.repartir_backend.dto.UtilisateurResponseDto;
import com.example.repartir_backend.entities.Admin;
import com.example.repartir_backend.entities.Utilisateur;
import com.example.repartir_backend.enumerations.Etat;
import com.example.repartir_backend.enumerations.Role;
import com.example.repartir_backend.repositories.AdminRepository;
import com.example.repartir_backend.repositories.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service pour gérer la logique métier des administrateurs.
 */
@Service
@RequiredArgsConstructor
public class AdminServices {
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final UtilisateurRepository utilisateurRepository;

    /**
     * Crée un nouvel administrateur.
     * @param adminDto Les données pour la création de l'administrateur.
     * @return L'entité Admin sauvegardée.
     */
    public Admin creerAdmin(AdminDto adminDto){
        Admin admin = new Admin();
        admin.setNom(adminDto.nom());
        admin.setPrenom(adminDto.prenom());
        admin.setEmail(adminDto.email());
        admin.setMotDePasse(passwordEncoder.encode(adminDto.motDePasse()));
        admin.setRole(Role.ADMIN);

        return adminRepository.save(admin);
    }

    /**
     * Récupère la liste de tous les administrateurs.
     * @return Une liste d'entités Admin.
     */
    public List<Admin> listerAdmins(){
        return adminRepository.findAll();
    }

    /**
     * Récupère la liste des comptes utilisateurs en attente de validation.
     * @return Une liste d'utilisateurs avec l'état EN_ATTENTE.
     */
    public List<Utilisateur> listerComptesEnAttente() {
        return utilisateurRepository.findByEtat(Etat.EN_ATTENTE);
    }

    /**
     * Approuve le compte d'un utilisateur.
     * @param userId L'identifiant de l'utilisateur.
     * @return Le DTO de l'utilisateur avec l'état mis à jour à VALIDE.
     */
    public UtilisateurResponseDto approuverCompte(Integer userId) {
        Utilisateur utilisateur = utilisateurRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        utilisateur.setEtat(Etat.VALIDE);
        utilisateur.setEstActive(true);
        Utilisateur utilisateurSauvegarde = utilisateurRepository.save(utilisateur);
        return mapToUtilisateurResponseDto(utilisateurSauvegarde);
    }

    /**
     * Rejette le compte d'un utilisateur.
     * @param userId L'identifiant de l'utilisateur.
     * @return Le DTO de l'utilisateur avec l'état mis à jour à REFUSE.
     */
    public UtilisateurResponseDto rejeterCompte(Integer userId) {
        Utilisateur utilisateur = utilisateurRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        utilisateur.setEtat(Etat.REFUSE);
        utilisateur.setEstActive(false);
        Utilisateur utilisateurSauvegarde = utilisateurRepository.save(utilisateur);
        return mapToUtilisateurResponseDto(utilisateurSauvegarde);
    }

    // Méthode privée pour mapper l'entité Utilisateur vers le DTO de réponse
    private UtilisateurResponseDto mapToUtilisateurResponseDto(Utilisateur utilisateur) {
        return new UtilisateurResponseDto(
                utilisateur.getId(),
                utilisateur.getNom(),
                utilisateur.getEmail(),
                utilisateur.getTelephone(),
                utilisateur.getRole(),
                utilisateur.getEtat(),
                utilisateur.isEstActive()
        );
    }
}
