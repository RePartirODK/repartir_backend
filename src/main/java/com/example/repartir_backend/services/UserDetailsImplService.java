package com.example.repartir_backend.services;

import com.example.repartir_backend.entities.Admin;
import com.example.repartir_backend.entities.UserDetailsImpl;
import com.example.repartir_backend.entities.Utilisateur;
import com.example.repartir_backend.repositories.AdminRepository;
import com.example.repartir_backend.repositories.UtilisateurRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

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
        Admin admin = adminRepo.findByEmail(email).orElseThrow(
                () -> new UsernameNotFoundException("Mot de passe ou email incorrecte : " + email)
        );
        if (admin != null) return new UserDetailsImpl(admin);

        Utilisateur user = utilisateurRepo.findByEmail(email).orElseThrow(
                () -> new UsernameNotFoundException("Mot de passe ou email incorrecte : " + email)
        );
        return new UserDetailsImpl(user);
    }

}
