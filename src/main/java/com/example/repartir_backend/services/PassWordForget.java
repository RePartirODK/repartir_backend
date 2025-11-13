package com.example.repartir_backend.services;

import com.example.repartir_backend.entities.Utilisateur;
import com.example.repartir_backend.enumerations.Etat;
import com.example.repartir_backend.repositories.UtilisateurRepository;
import jakarta.mail.MessagingException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PassWordForget {

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailSendServices mailSendServices;

    // Stock temporaire des codes avec date d’expiration
    private final Map<String, CodeInfo> codes = new HashMap<>();

    // Structure pour stocker le code et sa date d’expiration
    private static class CodeInfo {
        String code;
        LocalDateTime expiration;

        CodeInfo(String code, LocalDateTime expiration) {
            this.code = code;
            this.expiration = expiration;
        }
    }

    /**
     * Générer un code + envoyer l’email
     */
    public String passwordForget(String email) throws MessagingException, IOException {
        Optional<Utilisateur> utilisateurOptional = utilisateurRepository.findByEmail(email);
        if (utilisateurOptional.isEmpty()) {
            throw new EntityNotFoundException("Email incorrect.");
        }

        Utilisateur utilisateur = utilisateurOptional.get();
        //verifier que son compte et valide
        if(utilisateur.getEtat() == Etat.EN_ATTENTE){
            throw new IllegalAccessError("Votre compte n'est pas encore validé");
        }

        // Générer un code aléatoire à 6 chiffres
        String code = String.format("%06d", new Random().nextInt(999999));

        // Définir la date d’expiration à 10 minutes
        LocalDateTime expiration = LocalDateTime.now().plusMinutes(10);
        codes.put(email, new CodeInfo(code, expiration));

        // Envoyer l’email HTML avec le code
        String path = "src/main/resources/templates/codereinitialisation.html";
        mailSendServices.envoyerCode(
                utilisateur.getEmail(),
                "Réinitialisation du mot de passe",
                utilisateur.getNom(),
                path,
                code
        );

        return "Un code de vérification a été envoyé à votre email. Il expirera dans 10 minutes.";
    }

    /**
     * Vérifier le code et modifier le mot de passe
     */
    public String modifierPassword(String email, String code, String nouveauPassword) {
        Optional<Utilisateur> utilisateurOptional = utilisateurRepository.findByEmail(email);
        if (utilisateurOptional.isEmpty()) {
            throw new EntityNotFoundException("Email incorrect.");
        }

        CodeInfo codeInfo = codes.get(email);

        if (codeInfo == null) {
            return "Aucun code trouvé. Veuillez recommencer la procédure.";
        }

        // Vérifier l’expiration
        if (LocalDateTime.now().isAfter(codeInfo.expiration)) {
            codes.remove(email);
            return "Le code est expiré. Veuillez redemander un nouveau code.";
        }

        // Vérifier que le code est correct
        if (!codeInfo.code.equals(code)) {
            System.out.println(codeInfo.code);
            return "Code incorrect.";
        }

        // Modifier le mot de passe
        Utilisateur utilisateur = utilisateurOptional.get();
        //verifier si son compte a été validé
        if(utilisateur.getEtat() == Etat.EN_ATTENTE)
            throw  new IllegalAccessError("Votre n'est pas encore validé");
        utilisateur.setMotDePasse(passwordEncoder.encode(nouveauPassword));
        utilisateurRepository.save(utilisateur);

        // Supprimer le code après utilisation
        codes.remove(email);

        return "Mot de passe modifié avec succès";
    }





}
