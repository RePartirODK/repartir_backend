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
import java.time.LocalDateTime;
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
    private final UserDomaineServices userDomaineServices;

    @Transactional
    public Utilisateur register(RegisterUtilisateur utilisateur) throws MessagingException, IOException {
        System.out.println("Début de l'inscription utilisateur: " + utilisateur.getEmail());
        System.out.println("Données reçues: " + utilisateur);
        
        try {
            //verifier si un utilisateur avec l'email existe déjà
            Utilisateur utilisateur1 = utilisateurRepository.findByEmail(utilisateur.getEmail()).orElse(null);
            if (utilisateur1 != null) {
                System.out.println("Utilisateur déjà existant avec cet email: " + utilisateur.getEmail());
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
            newUtilisateur.setDateCreation(LocalDateTime.now());
            newUtilisateur = utilisateurRepository.save(newUtilisateur); // Sauvegarder et récupérer l'entité avec ID
            System.out.println("Utilisateur sauvegardé avec ID: " + newUtilisateur.getId());

            switch(utilisateur.getRole())
            {
                case JEUNE -> {
                    System.out.println("Création d'un jeune");
                    Jeune jeune = new Jeune();
                    jeune.setUtilisateur(newUtilisateur);
                    jeune.setGenre(utilisateur.getGenre());
                    jeune.setAge(utilisateur.getAge());
                    jeune.setA_propos(utilisateur.getA_propos());
                    jeune.setNiveau(utilisateur.getNiveau());
                    jeune.setUrlDiplome(utilisateur.getUrlDiplome());
                    jeune.setPrenom(utilisateur.getPrenom());
                    jeuneRepository.save(jeune);
                    System.out.println("Jeune créé avec succès");
                }
                case MENTOR -> {
                    System.out.println("Création d'un mentor");
                    Mentor mentor = new Mentor();
                    mentor.setUtilisateur(newUtilisateur);
                    mentor.setPrenom(utilisateur.getPrenom());
                    mentor.setProfession(utilisateur.getProfession());
                    mentor.setA_propos(utilisateur.getA_propos());
                    mentor.setAnnee_experience(utilisateur.getAnnee_experience());
                    mentorRepository.save(mentor);
                    System.out.println("Mentor créé avec succès");
                }
                case CENTRE -> {
                    System.out.println("Création d'un centre");
                    CentreFormation centre = new CentreFormation();
                    newUtilisateur.setEstActive(false);
                    centre.setUtilisateur(newUtilisateur);
                    centre.setAdresse(utilisateur.getAdresse());
                    centre.setAgrement(utilisateur.getAgrement());
                    centreFormationRepository.save(centre);
                    System.out.println("Centre créé avec succès");
                    // Notifier l'administrateur qu'un nouveau centre est en attente de validation.
                    notificationService.notifierAdmin("Un nouveau centre de formation '" + newUtilisateur.getNom() + "' s'est inscrit et est en attente de validation.");
                }
                case ENTREPRISE -> {
                    System.out.println("Création d'une entreprise");
                    Entreprise entreprise = new Entreprise();
                    newUtilisateur.setEstActive(false);
                    entreprise.setUtilisateur(newUtilisateur);
                    entreprise.setAdresse(utilisateur.getAdresse());
                    entreprise.setAgrement(utilisateur.getAgrement());
                    entrepriseRepository.save(entreprise);
                    System.out.println("Entreprise créée avec succès");
                    // Notifier l'administrateur qu'une nouvelle entreprise est en attente de validation.
                    notificationService.notifierAdmin("Une nouvelle entreprise '" + newUtilisateur.getNom() + "' s'est inscrite et est en attente de validation.");
                }
                case PARRAIN -> {
                    System.out.println("Création d'un parrain");
                    Parrain parrain = new Parrain();
                    parrain.setUtilisateur(newUtilisateur);
                    parrain.setProfession(utilisateur.getProfession());
                    parrain.setPrenom(utilisateur.getPrenom());
                    parrainRepository.save(parrain);
                    System.out.println("Parrain créé avec succès");
                }
            }
            
            // Association des domaines si domaineIds est fourni
            if (utilisateur.getDomaineIds() != null && !utilisateur.getDomaineIds().isEmpty()) {
                try {
                    System.out.println("Association des domaines pour l'utilisateur ID: " + newUtilisateur.getId());
                    System.out.println("Domaines à associer: " + utilisateur.getDomaineIds());
                    
                    for (Integer domaineId : utilisateur.getDomaineIds()) {
                        System.out.println("Tentative d'association de l'utilisateur " + newUtilisateur.getId() + " avec le domaine " + domaineId);
                        userDomaineServices.addUserToDomaine(newUtilisateur.getId(), domaineId);
                        System.out.println("Association réussie pour le domaine " + domaineId);
                    }
                    System.out.println("Toutes les associations de domaines ont été effectuées avec succès");
                } catch (Exception e) {
                    System.err.println("Erreur lors de l'association des domaines:");
                    e.printStackTrace();
                    // Ne pas bloquer l'inscription si l'association des domaines échoue
                }
            } else {
                System.out.println("Aucun domaine à associer");
            }

            //envoie d'un mail après la création des comptes utilisateurs
            String path = (newUtilisateur.getEtat() == Etat.VALIDE)
                    ? "templates/comptevalider.html"
                    : "templates/encoursdevalidation.html";

            System.out.println("Envoi de l'email de bienvenue à: " + utilisateur.getEmail());
            mailSendServices.envoyerEmailBienvenu(
                    utilisateur.getEmail(),
                    newUtilisateur.getEtat() == Etat.VALIDE ? "Création de compte" : "Compte en attente",
                    utilisateur.getNom(),
                    path
            );
            
            System.out.println("Inscription terminée avec succès pour: " + utilisateur.getEmail());
            return newUtilisateur;
        } catch (Exception e) {
            System.err.println("Erreur fatale lors de l'inscription:");
            e.printStackTrace();
            throw e;
        }
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
    @Transactional
    public String uploadPhotoProfil(MultipartFile file,String email){

        //on recherche l'utilisateur
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email).orElseThrow(
                ()-> new EntityNotFoundException("Email incorrecte")
        );
        System.out.println("🔍 [DEBUG] Utilisateur trouvé - ID: " + utilisateur.getId() + ", Role: " + utilisateur.getRole());
        
        String fileName = "user_" + utilisateur.getId();
        //appel de la methode
        String urlPhoto = uploadService.uploadFile(file, fileName, TypeFichier.PHOTO);
        utilisateur.setUrlPhoto(urlPhoto);

        //enregistrer l'utilisateur modifier
        utilisateurRepository.save(utilisateur);
        System.out.println("✅ [DEBUG] Utilisateur.urlPhoto mis à jour: " + urlPhoto);

        // Si l'utilisateur est une entreprise, mettre à jour également urlPhotoEntreprise
        if (utilisateur.getRole() == Role.ENTREPRISE) {
            System.out.println("🏢 [DEBUG] C'est une ENTREPRISE, recherche de l'entité...");
            Optional<Entreprise> entrepriseOpt = entrepriseRepository.findByUtilisateurEmail(email);
            if (entrepriseOpt.isPresent()) {
                Entreprise entreprise = entrepriseOpt.get();
                System.out.println("✅ [DEBUG] Entreprise trouvée - ID: " + entreprise.getId());
                entreprise.setUrlPhotoEntreprise(urlPhoto);
                entrepriseRepository.save(entreprise);
                System.out.println("✅ [DEBUG] Entreprise.urlPhotoEntreprise mis à jour: " + urlPhoto);
            } else {
                System.out.println("❌ [DEBUG] ERREUR: Entreprise NON TROUVÉE pour email: " + email);
            }
        } else {
            System.out.println("ℹ️ [DEBUG] Utilisateur n'est pas une entreprise (role: " + utilisateur.getRole() + ")");
        }

        return urlPhoto;
    }

}
