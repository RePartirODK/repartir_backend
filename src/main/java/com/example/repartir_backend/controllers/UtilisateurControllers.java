package com.example.repartir_backend.controllers;

import com.example.repartir_backend.dto.LogoutRequest;
import com.example.repartir_backend.dto.RegisterUtilisateur;
import com.example.repartir_backend.entities.Utilisateur;
import com.example.repartir_backend.services.UtilisateurServices;
import jakarta.mail.MessagingException;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;

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
                    utilisateursaved,
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

    //modifier photo de profil
    @PostMapping("/photoprofil")
    public ResponseEntity<?> uploadPhoto(
            @RequestParam("file") MultipartFile file,
            @RequestParam("email") String email
    ) {
        try {
            // Vérifier que le fichier est une image valide
            if (!isValidImage(file)) {
                return ResponseEntity.badRequest()
                        .body("Format de fichier non supporté. Seules les images JPG, JPEG et PNG sont autorisées.");
            }

            // Enregistrer le fichier
            String savedFileName = utilisateurServices.uploadPhotoProfil(file, email);

            return ResponseEntity.ok("Photo enregistrée avec succès : " + savedFileName);

        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (MaxUploadSizeExceededException e) {
            // Ne sera pas capté ici directement, mais géré par la méthode ci-dessous
            return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                    .body("Fichier trop volumineux. Taille maximale autorisée dépassée !");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur interne : " + e.getMessage());
        }
    }

    // Gérer les fichiers trop volumineux (localement dans ce contrôleur)
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<String> handleMaxUploadSize(MaxUploadSizeExceededException ex) {
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                .body("Le fichier est trop volumineux. Taille maximale autorisée dépassée !");
    }

    //Vérification du format de fichier image
    private boolean isValidImage(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null &&
                (contentType.equalsIgnoreCase("image/jpeg")
                        || contentType.equalsIgnoreCase("image/png")
                        || contentType.equalsIgnoreCase("image/jpg"));
    }
}
