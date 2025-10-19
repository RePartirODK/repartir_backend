package com.example.repartir_backend.controllers;

import com.example.repartir_backend.dto.LogoutRequest;
import com.example.repartir_backend.dto.RegisterUtilisateur;
import com.example.repartir_backend.entities.Utilisateur;
import com.example.repartir_backend.services.UtilisateurServices;
import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.*;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Utilisateurs", description = "Gestion des comptes utilisateurs : inscription, suppression et photo de profil")
public class UtilisateurControllers {
    UtilisateurServices utilisateurServices;
    public UtilisateurControllers(UtilisateurServices utilisateurServices){
        this.utilisateurServices = utilisateurServices;
    }

    //endpoint pour s'inscrire
    @Operation(
            summary = "Créer un nouveau compte utilisateur",
            description = "Permet à un utilisateur (jeune, parrain, centre de formation, etc.) de s’inscrire sur la plateforme.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Utilisateur créé avec succès",
                            content = @Content(schema = @Schema(implementation = Utilisateur.class))),
                    @ApiResponse(responseCode = "302", description = "Email déjà existant"),
                    @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
            }
    )
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
            // 👇 affichage clair pour déboguer
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Erreur inattendue : " + e.getClass().getSimpleName() + " - " + e.getMessage());
        }catch (IOException | MessagingException e)
        {
            return new ResponseEntity<>(
                    "Une erreur interne s'est produite, veillez reéssayer",
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }


    }


    //endpoint pour supprimer un compte
    @Operation(
            summary = "Supprimer un compte utilisateur",
            description = "Permet à un utilisateur de supprimer définitivement son compte à partir de son email.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Compte supprimé avec succès"),
                    @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé")
            }
    )
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
    @Operation(
            summary = "Mettre à jour la photo de profil",
            description = "Permet à un utilisateur d’ajouter ou de modifier sa photo de profil.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Photo enregistrée avec succès"),
                    @ApiResponse(responseCode = "400", description = "Format de fichier non supporté"),
                    @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé"),
                    @ApiResponse(responseCode = "413", description = "Fichier trop volumineux"),
                    @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
            }
    )
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
