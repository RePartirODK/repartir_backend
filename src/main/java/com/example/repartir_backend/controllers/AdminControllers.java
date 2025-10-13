package com.example.repartir_backend.controllers;

import com.example.repartir_backend.dto.AdminDto;
import com.example.repartir_backend.entities.Admin;
import com.example.repartir_backend.entities.Utilisateur;
import com.example.repartir_backend.services.AdminServices;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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
        return adminServices.creerAdmin(adminDto);
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


    /**
     * Approuve un compte utilisateur.
     * @param userId L'ID de l'utilisateur à approuver.
     * @return L'utilisateur avec le statut mis à jour.
     */


    /**
     * Rejette un compte utilisateur.
     * @param userId L'ID de l'utilisateur à rejeter.
     * @return L'utilisateur avec le statut mis à jour.
     */

}
