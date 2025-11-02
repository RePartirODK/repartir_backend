package com.example.repartir_backend.services;

import com.example.repartir_backend.entities.Admin;
import com.example.repartir_backend.entities.UserDetailsImpl;
import com.example.repartir_backend.entities.Utilisateur;
import com.example.repartir_backend.enumerations.Etat;
import com.example.repartir_backend.repositories.AdminRepository;
import com.example.repartir_backend.repositories.UtilisateurRepository;
import org.springframework.security.authentication.DisabledException;
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
        // D'abord chercher dans AdminRepository
        Admin admin = adminRepo.findByEmail(email).orElse(null);
        if (admin != null) {
            return new UserDetailsImpl(admin);
        }
   // Sinon chercher dans UtilisateurRepository
        Utilisateur utilisateur = utilisateurRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Mot de passe ou email incorrect : " + email));
        Etat etat = utilisateur.getEtat();
        //verifier si le compte a été supprimé
        if(etat == Etat.SUPPRIME){
            throw new DisabledException("Créer un compte");
        }
        // Vérifier si le compte de l'utilisateur est validé (pour Entreprise et Centre)
        if (etat != Etat.VALIDE) {
            throw new UsernameNotFoundException("Le compte de l'utilisateur n'est pas actif ou est en attente de validation.");
        }

        // Vérifier si le compte est bloqué (estActive = false)
        if (!utilisateur.isEstActive()) {
            throw new DisabledException("Votre compte a été bloqué par un administrateur. Contactez le support.");
        }

        return new UserDetailsImpl(utilisateur);
    }


}
