package com.example.repartir_backend.controllers;

import com.example.repartir_backend.dto.LogoutRequest;
import com.example.repartir_backend.dto.RegisterUtilisateur;
import com.example.repartir_backend.entities.Utilisateur;
import com.example.repartir_backend.services.UtilisateurServices;
import jakarta.mail.MessagingException;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jdk.jshell.execution.Util;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/utilisateurs")
public class UtilisateurControllers {
    UtilisateurServices utilisateurServices;
    public UtilisateurControllers(UtilisateurServices utilisateurServices){
        this.utilisateurServices = utilisateurServices;
    }

    //endpoint pour s'inscrire
    @PostMapping("/register")
    public ResponseEntity<?> creationCompte(@RequestBody RegisterUtilisateur registerUtilisateur)
    {
        try {
            Utilisateur utilisateursaved = utilisateurServices.register(registerUtilisateur);
            return new ResponseEntity<>(
                    "Compte crée",
                    HttpStatus.OK
            );
        }catch (EntityExistsException e)
        {
            return new ResponseEntity<>(
                    "Email existe déjà" + e.getMessage(),
                    HttpStatus.FOUND

            );
        } catch (RuntimeException e) {
           return new ResponseEntity<>(
                   "Une erreur s'est produite" + e.getMessage(),
                   HttpStatus.INTERNAL_SERVER_ERROR
           );
        }catch (IOException | MessagingException e)
        {
            return new ResponseEntity<>(
                    "Une erreur interne s'est produite, veillez reéssayer",
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }


    }


    //endpoint pour supprimer un compte
    @DeleteMapping("/supprimer")
    public ResponseEntity<?> supprimerCompte(@RequestBody LogoutRequest request)
    {
        try {
            utilisateurServices.supprimerCompte(request.getEmail());
            return ResponseEntity.ok("Suppression effectué");
        }catch (EntityNotFoundException e)
        {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }

    }
}
