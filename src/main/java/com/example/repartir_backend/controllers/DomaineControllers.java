package com.example.repartir_backend.controllers;

import com.example.repartir_backend.dto.DomaineDto;
import com.example.repartir_backend.dto.DomaineResponseDto;
import com.example.repartir_backend.entities.Domaine;
import com.example.repartir_backend.services.DomaineServices;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/domaines")
@Tag(name = "Domaines", description = "Endpoints pour la gestion des domaines de formation ou de compétences")

public class DomaineControllers {
    private final DomaineServices domaineServices;

    public DomaineControllers(DomaineServices domaineServices) {
        this.domaineServices = domaineServices;
    }

    @PostMapping("/creer")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Créer un nouveau domaine",
            description = "Permet d’ajouter un domaine (ex : Informatique, Électricité, Agriculture, etc.)"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Domaine créé avec succès",
                    content = @Content(schema = @Schema(implementation = Domaine.class))),
            @ApiResponse(responseCode = "400", description = "Erreur de validation du domaine", content = @Content),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content)
    })
    public Domaine creerDomaine(@RequestBody DomaineDto domaineDto) {
        return domaineServices.creerDomaine(domaineDto);
    }

    @Operation(
            summary = "Lister tous les domaines",
            description = "Renvoie la liste complète des domaines disponibles dans la base de données."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Liste des domaines récupérée avec succès",
                    content = @Content(schema = @Schema(implementation = DomaineResponseDto.class))),
            @ApiResponse(responseCode = "500", description = "Erreur interne lors de la récupération des domaines", content = @Content)
    })
    @GetMapping("/lister")
    public ResponseEntity<?> listerDomaines() {
        try {
            List<DomaineResponseDto> domaines = domaineServices.listerDomaines();
            return ResponseEntity.ok(domaines);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Une erreur est survenue lors de la récupération des domaines.");
        }
    }
}
