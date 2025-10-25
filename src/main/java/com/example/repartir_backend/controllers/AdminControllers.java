package com.example.repartir_backend.controllers;

import com.example.repartir_backend.dto.AdminDto;
import com.example.repartir_backend.dto.AdminResponseDto;
import com.example.repartir_backend.dto.UpdateAdminDto;
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
    public ResponseEntity<?> creerAdmin(@RequestBody AdminDto adminDto){
        try {
            AdminResponseDto adminResponseDto = adminServices.creerAdmin(adminDto);
            return ResponseEntity.ok(adminResponseDto);
        } catch (MessagingException | IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de la création de l'administrateur : " + e.getMessage());
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
     * Bloque un utilisateur (empêche sa connexion).
     * @param userId L'ID de l'utilisateur à bloquer.
     * @return L'utilisateur avec le statut bloqué.
     */
    @PutMapping("/bloquer-utilisateur/{userId}")
    public ResponseEntity<?> bloquerUtilisateur(@PathVariable Integer userId) {
        try {
            UtilisateurResponseDto utilisateurDto = adminServices.bloquerUtilisateur(userId);
            return ResponseEntity.ok(utilisateurDto);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     * Modifie les informations d'un administrateur existant.
     * @param adminId L'ID de l'administrateur à modifier.
     * @param updateAdminDto Les nouvelles données de l'administrateur.
     * @return L'administrateur mis à jour.
     */
    @PutMapping("/modifier/{adminId}")
    public ResponseEntity<?> modifierAdmin(@PathVariable Integer adminId, @RequestBody UpdateAdminDto updateAdminDto) {
        try {
            AdminResponseDto adminResponseDto = adminServices.modifierAdmin(adminId, updateAdminDto);
            return ResponseEntity.ok(adminResponseDto);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     * Débloque un utilisateur (autorise sa connexion).
     * @param userId L'ID de l'utilisateur à débloquer.
     * @return L'utilisateur avec le statut débloqué.
     */
    @PutMapping("/debloquer-utilisateur/{userId}")
    public ResponseEntity<?> debloquerUtilisateur(@PathVariable Integer userId) {
        try {
            UtilisateurResponseDto utilisateurDto = adminServices.debloquerUtilisateur(userId);
            return ResponseEntity.ok(utilisateurDto);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
