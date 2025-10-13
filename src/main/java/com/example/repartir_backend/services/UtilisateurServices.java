package com.example.repartir_backend.services;

import com.example.repartir_backend.dto.RegisterUtilisateur;
import com.example.repartir_backend.entities.Jeune;
import com.example.repartir_backend.entities.Mentor;
import com.example.repartir_backend.entities.Utilisateur;
import com.example.repartir_backend.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCrypt;

@Service
@RequiredArgsConstructor
public class UtilisateurServices {
    UtilisateurRepository utilisateurRepository;
    ParrainRepository parrainRepository;
    MentorRepository mentorRepository;
    CentreFormationRepository centreFormationRepository;
    JeuneRepository jeuneRepository;
    EntrepriseRepository entrepriseRepository;


    public Utilisateur register(RegisterUtilisateur utilisateur) {
        //verifier si un utilisateur avec l'email existe déjà
        Utilisateur existant = utilisateurRepository.findByEmail(utilisateur.getEmail()).orElse(null);
        if (existant != null)
            return null;

        Utilisateur newUtilisateur= new Utilisateur();
        newUtilisateur.setNom(utilisateur.getNom());
        newUtilisateur.setRole(utilisateur.getRole());
        newUtilisateur.setTelephone(utilisateur.getTelephone());
        newUtilisateur.setEmail(utilisateur.getEmail());
        newUtilisateur.setMotDePasse(BCrypt.hashpw(utilisateur.getMotDePasse(), BCrypt.gensalt()));
        if(utilisateur.getUrlPhoto() != null)
        {
            newUtilisateur.setUrlPhoto(utilisateur.getUrlPhoto());
        }
        switch(utilisateur.getRole())
        {
            case JEUNE -> {
                Jeune jeune = new Jeune();
                jeune.setUtilisateur(newUtilisateur);
                jeune.setGenre(utilisateur.getGenre());
                jeune.setAge(utilisateur.getAge());
                jeune.setA_propos(utilisateur.getA_propos());
                jeune.setNiveau(utilisateur.getNiveau());
                jeune.setUrlDiplome(utilisateur.getUrlDiplome());
            }
            case MENTOR -> {
                Mentor mentor = new Mentor();
                mentor.setUtilisateur(newUtilisateur);
                mentor.setProfession(utilisateur.getProfession());
                mentor.setA_propos(utilisateur.getA_propos());
            }
        }
        return newUtilisateur;
    }
}
