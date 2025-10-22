package com.example.repartir_backend.controllers;

import com.example.repartir_backend.entities.UserDomaine;
import com.example.repartir_backend.services.UserDomaineServices;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user-domaines")
@RequiredArgsConstructor
@Tag(name = "Utilisateur-Domaine", description = "Endpoints pour la gestion des associations entre utilisateurs et domaines")
public class UserDomaineControllers {

    private final UserDomaineServices userDomaineServices;

    @Operation(
            summary = "Associer un utilisateur à un domaine",
            description = """
                    Crée une association entre un utilisateur et un domaine.
                    Si l'utilisateur ou le domaine n'existe pas, une erreur est renvoyée.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Association créée avec succès",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserDomaine.class))),
            @ApiResponse(responseCode = "400", description = "Paramètres invalides"),
            @ApiResponse(responseCode = "404", description = "Utilisateur ou domaine introuvable")
    })
    @PostMapping("/utilisateur/{userId}/domaine/{domaineId}")
    public ResponseEntity<?> addUserToDomaine(
            @PathVariable int userId,
            @PathVariable int domaineId) {
        try {
            UserDomaine userDomaine = userDomaineServices.addUserToDomaine(userId, domaineId);
            return ResponseEntity.status(HttpStatus.CREATED).body(userDomaine);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Utilisateur ou domaine introuvable");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(
            summary = "Lister les domaines d’un utilisateur",
            description = "Retourne la liste de tous les domaines auxquels un utilisateur est associé."
    )
    @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès")
    @GetMapping("/utilisateur/{userId}")
    public ResponseEntity<List<UserDomaine>> getDomainesByUtilisateur(@PathVariable int userId) {
        List<UserDomaine> domaines = userDomaineServices.getDomainesByUtilisateur(userId);
        return ResponseEntity.ok(domaines);
    }

    @Operation(
            summary = "Lister les utilisateurs d’un domaine",
            description = "Retourne la liste de tous les utilisateurs associés à un domaine spécifique."
    )
    @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès")
    @GetMapping("/domaine/{domaineId}")
    public ResponseEntity<List<UserDomaine>> getUtilisateursByDomaine(@PathVariable int domaineId) {
        List<UserDomaine> utilisateurs = userDomaineServices.getUtilisateursByDomaine(domaineId);
        return ResponseEntity.ok(utilisateurs);
    }

    @Operation(
            summary = "Supprimer une association utilisateur–domaine",
            description = "Retire la liaison entre un utilisateur et un domaine donné."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Association supprimée avec succès"),
            @ApiResponse(responseCode = "404", description = "Association introuvable")
    })
    @DeleteMapping("/utilisateur/{userId}/domaine/{domaineId}")
    public ResponseEntity<?> removeUserFromDomaine(
            @PathVariable int userId,
            @PathVariable int domaineId) {
        try {
            userDomaineServices.removeUserFromDomaine(userId, domaineId);
            return ResponseEntity.ok("Association utilisateur–domaine supprimée avec succès");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Association introuvable");
        }
    }
}
