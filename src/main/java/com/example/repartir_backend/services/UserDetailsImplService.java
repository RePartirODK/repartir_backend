package com.example.repartir_backend.services;

import com.example.repartir_backend.entities.Admin;
import com.example.repartir_backend.entities.UserDetailsImpl;
import com.example.repartir_backend.entities.Utilisateur;
import com.example.repartir_backend.enumerations.Etat;
import com.example.repartir_backend.repositories.AdminRepository;
import com.example.repartir_backend.repositories.UtilisateurRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserDetailsImplService implements UserDetailsService {
    private final AdminRepository adminRepo;
    private final UtilisateurRepository utilisateurRepo;

    public UserDetailsImplService(AdminRepository adminRepo, UtilisateurRepository utilisateurRepo) {
        this.adminRepo = adminRepo;
        this.utilisateurRepo = utilisateurRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Essayer de trouver un admin d'abord
        Optional<Admin> adminOptional = adminRepo.findByEmail(email);
        if (adminOptional.isPresent()) {
            Admin admin = adminOptional.get();
            return new UserDetailsImpl(admin);
        }

        // Sinon, chercher un utilisateur normal
        Utilisateur user = utilisateurRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Aucun utilisateur trouvé avec l'email : " + email));

        // Vérifier si le compte de l'utilisateur est validé (pour Entreprise et Centre)
        if (user.getEtat() != Etat.VALIDE) {
            throw new UsernameNotFoundException("Le compte de l'utilisateur n'est pas actif ou est en attente de validation.");
        }

        return new UserDetailsImpl(user);
    }

}
