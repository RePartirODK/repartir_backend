package com.example.repartir_backend.services;

import com.example.repartir_backend.dto.AdminDto;
import com.example.repartir_backend.dto.UtilisateurResponseDto;
import com.example.repartir_backend.entities.Admin;
import com.example.repartir_backend.entities.Utilisateur;
import com.example.repartir_backend.enumerations.Etat;
import com.example.repartir_backend.enumerations.Role;
import com.example.repartir_backend.repositories.AdminRepository;
import com.example.repartir_backend.repositories.UtilisateurRepository;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
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
    private final MailSendServices mailSendServices;
    private final UtilisateurServices utilisateurServices;


    /**
     * Crée un nouvel administrateur.
     * @param adminDto Les données pour la création de l'administrateur.
     * @return L'entité Admin sauvegardée.
     */
    @Transactional
    public Admin creerAdmin(AdminDto adminDto) throws MessagingException, IOException {
        String path = "src/main/resources/templates/creationcompteadmin.html";
        // Vérifier si un admin avec cet email existe déjà
        Optional<Admin> existingAdmin = adminRepository.findByEmail(adminDto.email());
        if (existingAdmin.isPresent()) {
            throw new RuntimeException("Un administrateur avec cet email existe déjà.");
        }

        Admin admin = new Admin();
        admin.setNom(adminDto.nom());
        admin.setPrenom(adminDto.prenom());
        admin.setEmail(adminDto.email());
        admin.setMotDePasse(passwordEncoder.encode(adminDto.motDePasse()));
        admin.setRole(Role.ADMIN);
        adminRepository.save(admin);
        //envoie d'un email au nouveau admin
        //envoi d'un mail de validation de compte
        mailSendServices.inscriptionAdmin(admin.getEmail(),
                "Creation de compte admin",
                admin.getEmail(),
                path);
        return admin;
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
    public UtilisateurResponseDto approuverCompte(Integer userId) throws MessagingException, IOException {
        String path = "src/main/resources/templates/comptevalider.html";
        Utilisateur utilisateur = utilisateurRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        utilisateur.setEtat(Etat.VALIDE);
        utilisateur.setEstActive(true);
        Utilisateur utilisateurSauvegarde = utilisateurRepository.save(utilisateur);
        //envoi d'un mail de validation de compte
        mailSendServices.envoyerEmailBienvenu(utilisateur.getEmail(),
                "Validation de compte",
                utilisateur.getNom(),
                path);
        return mapToUtilisateurResponseDto(utilisateurSauvegarde);
    }

    /**
     * Rejette le compte d'un utilisateur.
     * @param userId L'identifiant de l'utilisateur.
     * @return Le DTO de l'utilisateur avec l'état mis à jour à REFUSE.
     */
    @Transactional
    public UtilisateurResponseDto rejeterCompte(Integer userId) throws MessagingException, IOException {
        String path = "src/main/resources/templates/refusecompte.html";
        Utilisateur utilisateur = utilisateurRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        utilisateur.setEtat(Etat.REFUSE);
        utilisateur.setEstActive(false);
        //envoie de mail à l'utilisateur
        mailSendServices.envoyerEmailBienvenu(utilisateur.getEmail(),
                "Refut de creation de compte",
                utilisateur.getNom(),
                path);
        Utilisateur utilisateurSauvegarde = utilisateurRepository.save(utilisateur);
        //supprimer l'utilisateur de la base données
        utilisateurServices.deleteUtilisateur(utilisateur.getId());
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
