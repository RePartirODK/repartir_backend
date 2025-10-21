package com.example.repartir_backend.controllers;

import com.example.repartir_backend.entities.UserDomaine;
import com.example.repartir_backend.services.UserDomaineServices;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user-domaines")
@RequiredArgsConstructor
public class UserDomaineControllers {
    private final UserDomaineServices userDomaineServices;


    /**
     * Associe un utilisateur à un domaine.
     */
    @PostMapping("/utilisateur/{userId}/domaine/{domaineId}")
    public ResponseEntity<UserDomaine> addUserToDomaine(
            @PathVariable int userId,
            @PathVariable int domaineId) {
        UserDomaine userDomaine = userDomaineServices.addUserToDomaine(userId, domaineId);
        return ResponseEntity.status(HttpStatus.CREATED).body(userDomaine);
    }

    /**
     * Liste des domaines d’un utilisateur.
     */
    @GetMapping("/utilisateur/{userId}")
    public ResponseEntity<List<UserDomaine>> getDomainesByUtilisateur(@PathVariable int userId) {
        List<UserDomaine> domaines = userDomaineServices.getDomainesByUtilisateur(userId);
        return ResponseEntity.ok(domaines);
    }

    /**
     * Liste des utilisateurs d’un domaine.
     */
    @GetMapping("/domaine/{domaineId}")
    public ResponseEntity<List<UserDomaine>> getUtilisateursByDomaine(@PathVariable int domaineId) {
        List<UserDomaine> utilisateurs = userDomaineServices.getUtilisateursByDomaine(domaineId);
        return ResponseEntity.ok(utilisateurs);
    }

    /**
     * Supprime une association utilisateur–domaine.
     */
    @DeleteMapping("/utilisateur/{userId}/domaine/{domaineId}")
    public ResponseEntity<Void> removeUserFromDomaine(
            @PathVariable int userId,
            @PathVariable int domaineId) {
        userDomaineServices.removeUserFromDomaine(userId, domaineId);
        return ResponseEntity.noContent().build();
    }
}
