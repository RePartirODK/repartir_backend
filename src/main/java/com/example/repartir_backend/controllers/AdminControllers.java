package com.example.repartir_backend.controllers;

import com.example.repartir_backend.dto.AdminDto;
import com.example.repartir_backend.dto.UtilisateurResponseDto;
import com.example.repartir_backend.entities.Admin;
import com.example.repartir_backend.entities.Utilisateur;
import com.example.repartir_backend.services.AdminServices;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

/**
 * Contrôleur pour les opérations liées aux administrateurs.
 */
@RestController
@RequestMapping("/administrateurs")
@RequiredArgsConstructor
public class AdminControllers {
    private final AdminServices adminServices;

    /**
     * Crée un nouvel administrateur.
     * @param adminDto Les informations du nouvel administrateur.
     * @return L'administrateur créé.
     */
    @PostMapping("/creer")
    public Admin creerAdmin(@RequestBody AdminDto adminDto){
        try {
            return adminServices.creerAdmin(adminDto);
        } catch (MessagingException | IOException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * Récupère la liste de tous les administrateurs.
     * @return Une liste d'administrateurs.
     */
    @GetMapping("/lister")
    public List<Admin> listerAdmins(){
        return adminServices.listerAdmins();
    }

    /**
     * Récupère la liste des comptes en attente de validation.
     * @return Une liste d'utilisateurs avec le statut "ATTENTE".
     */
    @GetMapping("/comptes-en-attente")
    public List<Utilisateur> listerComptesEnAttente() {
        return adminServices.listerComptesEnAttente();
    }

    /**
     * Approuve un compte utilisateur.
     * @param userId L'ID de l'utilisateur à approuver.
     * @return L'utilisateur avec le statut mis à jour.
     */
    @PutMapping("/valider-compte/{userId}")
    public ResponseEntity<?> validerCompte(@PathVariable Integer userId) {
        try {
            UtilisateurResponseDto utilisateurDto = adminServices.approuverCompte(userId);
            return ResponseEntity.ok(utilisateurDto);
        } catch (RuntimeException | MessagingException | IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Rejette un compte utilisateur.
     * @param userId L'ID de l'utilisateur à rejeter.
     * @return L'utilisateur avec le statut mis à jour.
     */
    @PutMapping("/refuser-compte/{userId}")
    public ResponseEntity<?> refuserCompte(@PathVariable Integer userId) {
        try {
            UtilisateurResponseDto utilisateurDto = adminServices.rejeterCompte(userId);
            return ResponseEntity.ok(utilisateurDto);
        } catch (RuntimeException | MessagingException | IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
