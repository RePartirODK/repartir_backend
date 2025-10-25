package com.example.repartir_backend.services;

import com.example.repartir_backend.dto.AdminDto;
import com.example.repartir_backend.dto.AdminResponseDto;
import com.example.repartir_backend.dto.UpdateAdminDto;
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
     * @return Le DTO de l'administrateur créé.
     */
    @Transactional
    public AdminResponseDto creerAdmin(AdminDto adminDto) throws MessagingException, IOException {
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
        Admin adminSauvegarde = adminRepository.save(admin);
        //envoie d'un email au nouveau admin
        //envoi d'un mail de validation de compte
        mailSendServices.inscriptionAdmin(adminSauvegarde.getEmail(),
                "Creation de compte admin",
                adminSauvegarde.getEmail(),
                path);
        return AdminResponseDto.fromEntity(adminSauvegarde);
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

    /**
     * Bloque un utilisateur (empêche sa connexion).
     * @param userId L'identifiant de l'utilisateur à bloquer.
     * @return Le DTO de l'utilisateur avec le statut bloqué.
     */
    @Transactional
    public UtilisateurResponseDto bloquerUtilisateur(Integer userId) {
        Utilisateur utilisateur = utilisateurRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        
        // Vérifier que l'utilisateur n'est pas déjà bloqué
        if (!utilisateur.isEstActive()) {
            throw new RuntimeException("L'utilisateur est déjà bloqué");
        }
        
        utilisateur.setEstActive(false);
        Utilisateur utilisateurSauvegarde = utilisateurRepository.save(utilisateur);
        
        return mapToUtilisateurResponseDto(utilisateurSauvegarde);
    }

    /**
     * Débloque un utilisateur (autorise sa connexion).
     * @param userId L'identifiant de l'utilisateur à débloquer.
     * @return Le DTO de l'utilisateur avec le statut débloqué.
     */
    @Transactional
    public UtilisateurResponseDto debloquerUtilisateur(Integer userId) {
        Utilisateur utilisateur = utilisateurRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        
        // Vérifier que l'utilisateur n'est pas déjà débloqué
        if (utilisateur.isEstActive()) {
            throw new RuntimeException("L'utilisateur est déjà débloqué");
        }
        
        utilisateur.setEstActive(true);
        Utilisateur utilisateurSauvegarde = utilisateurRepository.save(utilisateur);
        
        return mapToUtilisateurResponseDto(utilisateurSauvegarde);
    }

    /**
     * Met à jour les informations d'un administrateur existant.
     * @param adminId L'ID de l'administrateur à modifier.
     * @param updateAdminDto Les nouvelles données de l'administrateur.
     * @return Le DTO de l'administrateur mis à jour.
     */
    @Transactional
    public AdminResponseDto modifierAdmin(Integer adminId, UpdateAdminDto updateAdminDto) {
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Administrateur non trouvé avec l'ID : " + adminId));

        // Mise à jour du prénom si fourni
        if (updateAdminDto.prenom() != null && !updateAdminDto.prenom().trim().isEmpty()) {
            admin.setPrenom(updateAdminDto.prenom().trim());
        }

        // Mise à jour du nom si fourni
        if (updateAdminDto.nom() != null && !updateAdminDto.nom().trim().isEmpty()) {
            admin.setNom(updateAdminDto.nom().trim());
        }

        // Mise à jour de l'email si fourni
        if (updateAdminDto.email() != null && !updateAdminDto.email().trim().isEmpty()) {
            String newEmail = updateAdminDto.email().trim();
            
            // Vérifier que le nouvel email n'est pas déjà utilisé par un autre admin
            Optional<Admin> existingAdmin = adminRepository.findByEmail(newEmail);
            if (existingAdmin.isPresent() && existingAdmin.get().getId() != adminId) {
                throw new RuntimeException("Un autre administrateur utilise déjà cet email : " + newEmail);
            }
            
            admin.setEmail(newEmail);
        }

        // Mise à jour du mot de passe si fourni
        if (updateAdminDto.motDePasse() != null && !updateAdminDto.motDePasse().trim().isEmpty()) {
            String newPassword = updateAdminDto.motDePasse().trim();
            if (newPassword.length() < 6) {
                throw new RuntimeException("Le mot de passe doit contenir au moins 6 caractères.");
            }
            admin.setMotDePasse(passwordEncoder.encode(newPassword));
        }

        Admin adminSauvegarde = adminRepository.save(admin);
        return AdminResponseDto.fromEntity(adminSauvegarde);
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
