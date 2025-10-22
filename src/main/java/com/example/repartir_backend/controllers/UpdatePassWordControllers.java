package com.example.repartir_backend.controllers;

import com.example.repartir_backend.dto.UpdatePasswordRequest;
import com.example.repartir_backend.entities.UserDetailsImpl;
import com.example.repartir_backend.services.UpdatePassWordService;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/updatepassword")
@Tag(name = "Mot de passe", description = "Endpoints liés à la mise à jour du mot de passe")
public class UpdatePassWordControllers {
    private final UpdatePassWordService updatePassWordService;

    @Operation(
            summary = "Modifier le mot de passe d’un utilisateur",
            description = """
                    Cet endpoint permet à un utilisateur de modifier **son propre mot de passe**, 
                    ou à un **administrateur** de modifier le mot de passe de n’importe quel utilisateur.
                    L’utilisateur doit être authentifié.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Mot de passe modifié avec succès",
                    content = @Content(mediaType = "application/json", schema = @Schema(example = "\"Mot de passe modifié avec succès\""))),
            @ApiResponse(responseCode = "400", description = "Requête invalide (ex: utilisateur inexistant, mot de passe non valide)",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "403", description = "Accès refusé : seul le propriétaire ou un admin peut modifier le mot de passe",
                    content = @Content(mediaType = "application/json", schema = @Schema(example = "\"Accès refusé ! Vous ne pouvez modifier que votre mot de passe ou vous devez être admin.\""))),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur",
                    content = @Content(mediaType = "application/json"))
    })
    @PutMapping("/{id}")
    public ResponseEntity<String> changePassWord(@PathVariable int id,
                                                 @RequestBody UpdatePasswordRequest request,
                                                 @AuthenticationPrincipal UserDetailsImpl userDetails){

        /*
         * verifier que la personne voulant modifier le mot de passe
         * est bien celle connectée ou bien un admin
         */
        boolean isOwner = userDetails.getId() == id;
        boolean isAdmin = userDetails.getAuthorities()
                .contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
        if (!isAdmin && !isOwner) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Accès refusé ! Vous ne pouvez modifier que votre mot de passe ou vous devez être admin.");
        }
        try {
            updatePassWordService.updateMotDePasse(id, request);
            return ResponseEntity.ok("Mot de passe modifié avec succès");
        }catch (EntityNotFoundException | IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
