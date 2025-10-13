package com.example.repartir_backend.services;

import com.example.repartir_backend.dto.RegisterUtilisateur;
import com.example.repartir_backend.entities.*;
import com.example.repartir_backend.repositories.*;
import jakarta.persistence.EntityExistsException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCrypt;

@Service
@RequiredArgsConstructor
public class UtilisateurServices {
    private final UtilisateurRepository utilisateurRepository;
    private final ParrainRepository parrainRepository;
    private final MentorRepository mentorRepository;
    private final CentreFormationRepository centreFormationRepository;
    private final JeuneRepository jeuneRepository;
    private final EntrepriseRepository entrepriseRepository;
    private final PasswordEncoder encoder;


    public Utilisateur register(RegisterUtilisateur utilisateur) {
        //verifier si un utilisateur avec l'email existe déjà
        Utilisateur utilisateur1 = utilisateurRepository.findByEmail(utilisateur.getEmail()).orElse(null);
        if (utilisateur1 != null) {
            throw new EntityExistsException("Un utilisateur avec cet email existe déjà.");
        }


        Utilisateur newUtilisateur= new Utilisateur();
        newUtilisateur.setNom(utilisateur.getNom());
        newUtilisateur.setRole(utilisateur.getRole());
        newUtilisateur.setTelephone(utilisateur.getTelephone());
        newUtilisateur.setEmail(utilisateur.getEmail());
        newUtilisateur.setMotDePasse(encoder.encode(utilisateur.getMotDePasse()));
        if(utilisateur.getUrlPhoto() != null)
        {
            newUtilisateur.setUrlPhoto(utilisateur.getUrlPhoto());
        }
        utilisateurRepository.save(newUtilisateur);
        switch(utilisateur.getRole())
        {
            case JEUNE -> {
                Jeune jeune = new Jeune();
                newUtilisateur.setEstActive(true);
                jeune.setUtilisateur(newUtilisateur);
                jeune.setGenre(utilisateur.getGenre());
                jeune.setAge(utilisateur.getAge());
                jeune.setA_propos(utilisateur.getA_propos());
                jeune.setNiveau(utilisateur.getNiveau());
                jeune.setUrlDiplome(utilisateur.getUrlDiplome());
                jeune.setPrenom(utilisateur.getPrenom());
                jeuneRepository.save(jeune);
            }
            case MENTOR -> {
                Mentor mentor = new Mentor();
                newUtilisateur.setEstActive(true);
                mentor.setUtilisateur(newUtilisateur);
                mentor.setPrenom(utilisateur.getPrenom());
                mentor.setProfession(utilisateur.getProfession());
                mentor.setA_propos(utilisateur.getA_propos());
                mentor.setAnnee_experience(utilisateur.getAnnee_experience());

                mentorRepository.save(mentor);
            }
            case CENTRE -> {
                CentreFormation centre = new CentreFormation();
                newUtilisateur.setEstActive(false);
                centre.setUtilisateur(newUtilisateur);
                centre.setAdresse(utilisateur.getAdresse());
                centre.setAgrement(utilisateur.getAgrement());
                centreFormationRepository.save(centre);
            }
            case ENTREPRISE -> {
                Entreprise entreprise = new Entreprise();
                newUtilisateur.setEstActive(false);
                entreprise.setUtilisateur(newUtilisateur);
                entreprise.setAdresse(utilisateur.getAdresse());
                entreprise.setAgrement(utilisateur.getAgrement());
                entrepriseRepository.save(entreprise);
            }
            case PARRAIN -> {
                Parrain parrain = new Parrain();
                //parrainparrain.setUtilisateur(newUtilisateur); // à ajouter !

                newUtilisateur.setEstActive(true);
                parrain.setProfession(utilisateur.getProfession());
                parrain.setPrenom(utilisateur.getPrenom());
                parrainRepository.save(parrain);
            }
        }

        return newUtilisateur;
    }
}
