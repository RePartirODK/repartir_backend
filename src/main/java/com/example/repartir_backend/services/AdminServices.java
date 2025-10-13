package com.example.repartir_backend.services;

import com.example.repartir_backend.dto.AdminDto;
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
     * @return Une liste d'utilisateurs avec l'état ATTENTE.

    public List<Utilisateur> listerComptesEnAttente() {
        return utilisateurRepository.findByEtat(Etat.ENATTENTE);
    }*/

    /**
     * Approuve le compte d'un utilisateur.
     * @param userId L'identifiant de l'utilisateur.
     * @return L'utilisateur avec l'état mis à jour à APPROUVE, ou null si l'utilisateur n'est pas trouvé.
     */
    public Utilisateur approuverCompte(Integer userId) {
        Optional<Utilisateur> optionalUtilisateur = utilisateurRepository.findById(userId);
        if (optionalUtilisateur.isPresent()) {
            Utilisateur utilisateur = optionalUtilisateur.get();
            return utilisateurRepository.save(utilisateur);
        }
        return null;
    }

    /**
     * Rejette le compte d'un utilisateur.
     * @param userId L'identifiant de l'utilisateur.
     * @return L'utilisateur avec l'état mis à jour à REJETE, ou null si l'utilisateur n'est pas trouvé.
     */
    public Utilisateur rejeterCompte(Integer userId) {
        Optional<Utilisateur> optionalUtilisateur = utilisateurRepository.findById(userId);
        if (optionalUtilisateur.isPresent()) {
            Utilisateur utilisateur = optionalUtilisateur.get();
            return utilisateurRepository.save(utilisateur);
        }
        return null;
    }
}
