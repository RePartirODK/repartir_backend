package com.example.repartir_backend.services;

import com.example.repartir_backend.dto.RegisterUtilisateur;
import com.example.repartir_backend.entities.*;
import com.example.repartir_backend.enumerations.Etat;
import com.example.repartir_backend.enumerations.Role;
import com.example.repartir_backend.enumerations.TypeFichier;
import com.example.repartir_backend.repositories.*;
import jakarta.mail.MessagingException;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UtilisateurServices {
    private final UtilisateurRepository utilisateurRepository;
    private final ParrainRepository parrainRepository;
    private final MentorRepository mentorRepository;
    private final CentreFormationRepository centreFormationRepository;
    private final JeuneRepository jeuneRepository;
    private final EntrepriseRepository entrepriseRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailSendServices mailSendServices;
    private final UploadService uploadService;
    private final NotificationService notificationService;

    @Transactional
    public Utilisateur register(RegisterUtilisateur utilisateur) throws MessagingException, IOException {
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
        if (utilisateur.getRole() == Role.JEUNE || utilisateur.getRole() == Role.MENTOR || utilisateur.getRole() == Role.PARRAIN) {
            newUtilisateur.setEtat(Etat.VALIDE);
        } else {
            newUtilisateur.setEtat(Etat.EN_ATTENTE);
        }
        newUtilisateur.setMotDePasse(passwordEncoder.encode(utilisateur.getMotDePasse()));
        newUtilisateur.setEstActive(
                utilisateur.getRole() == Role.JEUNE ||
                        utilisateur.getRole() == Role.MENTOR ||
                        utilisateur.getRole() == Role.PARRAIN
        );
        newUtilisateur.setUrlPhoto(utilisateur.getUrlPhoto());
        utilisateurRepository.save(newUtilisateur);


        switch(utilisateur.getRole())
        {
            case JEUNE -> {
                Jeune jeune = new Jeune();
                //newUtilisateur.setEstActive(true);
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
                //newUtilisateur.setEstActive(true);
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
                // Notifier l'administrateur qu'un nouveau centre est en attente de validation.
                //notificationService.notifierAdmin("Un nouveau centre de formation '" + newUtilisateur.getNom() + "' s'est inscrit et est en attente de validation.");
            }
            case ENTREPRISE -> {
                Entreprise entreprise = new Entreprise();
                newUtilisateur.setEstActive(false);
                entreprise.setUtilisateur(newUtilisateur);
                entreprise.setAdresse(utilisateur.getAdresse());
                entreprise.setAgrement(utilisateur.getAgrement());
                entrepriseRepository.save(entreprise);
                // Notifier l'administrateur qu'une nouvelle entreprise est en attente de validation.
                //notificationService.notifierAdmin("Une nouvelle entreprise '" + newUtilisateur.getNom() + "' s'est inscrite et est en attente de validation.");
            }
            case PARRAIN -> {
                Parrain parrain = new Parrain();
                parrain.setUtilisateur(newUtilisateur);
                //newUtilisateur.setEstActive(true);
                parrain.setProfession(utilisateur.getProfession());
                parrain.setPrenom(utilisateur.getPrenom());
                parrainRepository.save(parrain);
            }
        }

        //envoie d'un mail après la création des comptes utilisateurs
        // Envoi de mail
        String path = (newUtilisateur.getEtat() == Etat.VALIDE)
                ? "src/main/resources/templates/comptevalider.html"
                : "src/main/resources/templates/encoursdevalidation.html";

        mailSendServices.envoyerEmailBienvenu(
                utilisateur.getEmail(),
                newUtilisateur.getEtat() == Etat.VALIDE ? "Création de compte" : "Compte en attente",
                utilisateur.getNom(),
                path
        );
        return newUtilisateur;
    }

    public void deleteUtilisateur(int id) {
        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur introuvable"));

        switch (utilisateur.getRole()) {
            case CENTRE -> {
                centreFormationRepository.deleteByUtilisateurId(id);
            }
            case ENTREPRISE -> {
                entrepriseRepository.deleteByUtilisateurId(id);
            }
            case JEUNE -> {
                jeuneRepository.deleteByUtilisateurId(id);
            }
            case MENTOR -> {
                mentorRepository.deleteByUtilisateurId(id);
            }
            case PARRAIN -> {
                parrainRepository.deleteByUtilisateurId(id);
            }
        }
        utilisateurRepository.delete(utilisateur);
    }

    //cette methode simule l'action de suppression de compte par un utilisateur
    @Transactional
    public void supprimerCompte(String email)
    {
        //chercher l'utilisateur par son email
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email).orElseThrow(
                ()-> new EntityNotFoundException("Email incorrecte")
        );
        utilisateur.setEtat(Etat.SUPPRIME);
        utilisateur.setEstActive(false);

        //modifier l'email du compte supprimer
        utilisateur.setEmail(utilisateur.getEmail()+"_deleted_" + utilisateur.getId());
        //modifier le numero de telephone du compte supprimer
        utilisateur.setTelephone(utilisateur.getTelephone()+"_deleted_"+ utilisateur.getTelephone());

        // Supprimer les refresh tokens associés pour forcer la déconnexion
        refreshTokenRepository.deleteByUtilisateur_Id(utilisateur.getId());

        //on l'enregistre
        utilisateurRepository.save(utilisateur);
    }


    //service pour upload photo de profil
    public String uploadPhotoProfil(MultipartFile file,String email){

        //on recherche l'utilisateur
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email).orElseThrow(
                ()-> new EntityNotFoundException("Email incorrecte")
        );
        String fileName = "user_" + utilisateur.getId();
        //appel de la methode
        String urlPhoto = uploadService.uploadFile(file, fileName, TypeFichier.PHOTO);
        utilisateur.setUrlPhoto(urlPhoto);

        //enregistrer l'utilisateur modifier
        utilisateurRepository.save(utilisateur);
        return urlPhoto;


    }

}
