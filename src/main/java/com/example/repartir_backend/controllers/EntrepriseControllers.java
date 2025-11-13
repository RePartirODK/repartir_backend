package com.example.repartir_backend.controllers;

import com.example.repartir_backend.dto.EntrepriseProfileResponse;
import com.example.repartir_backend.dto.EntrepriseResponseDto;
import com.example.repartir_backend.entities.Entreprise;
import com.example.repartir_backend.services.EntrepriseServices;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/entreprises")
@Tag(name = "Entreprises", description = "Gestion du profil et des actions des entreprises")
public class EntrepriseControllers {

    EntrepriseServices entrepriseServices;
    public EntrepriseControllers(EntrepriseServices entrepriseServices){
        this.entrepriseServices = entrepriseServices;
    }

    @GetMapping
    public ResponseEntity<List<EntrepriseResponseDto>> getAllEntreprises() {
        return ResponseEntity.ok(entrepriseServices.getAllEntreprises());
    }

    @Operation(
            summary = "Récupérer le profil de l'entreprise connectée",
            description = "Permet à l'entreprise connectée de récupérer ses informations de profil."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profil récupéré avec succès"),
            @ApiResponse(responseCode = "404", description = "Entreprise non trouvée"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    @GetMapping("/profile")
    @PreAuthorize("hasRole('ENTREPRISE')")
    public ResponseEntity<?> getProfile(Principal principal) {
        try {
            String email = principal.getName(); // Récupération de l'email depuis le JWT
            Entreprise entreprise = entrepriseServices.getEntrepriseByEmail(email);
            EntrepriseProfileResponse response = EntrepriseProfileResponse.fromEntity(entreprise);
            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Entreprise non trouvée");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la récupération du profil : " + e.getMessage());
        }
    }
}
