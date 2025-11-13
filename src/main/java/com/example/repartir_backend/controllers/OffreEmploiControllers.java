package com.example.repartir_backend.controllers;

import com.example.repartir_backend.dto.OffreEmploiResponseDto;
import com.example.repartir_backend.services.OffreEmploiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/offres")
@RequiredArgsConstructor
public class OffreEmploiControllers {

    private final OffreEmploiService offreEmploiService;

    @GetMapping("/lister")
    @PreAuthorize("hasAnyRole('JEUNE', 'ADMIN')")
    public ResponseEntity<List<OffreEmploiResponseDto>> listerToutesLesOffres() {
        return ResponseEntity.ok(offreEmploiService.listerToutesLesOffres());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('JEUNE', 'ADMIN')")
    @Operation(
            summary = "Obtenir les détails d'une offre d'emploi",
            description = "Récupère les détails complets d'une offre d'emploi par son ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Offre trouvée"),
                    @ApiResponse(responseCode = "404", description = "Offre non trouvée", content = @Content)
            }
    )
    public ResponseEntity<?> getOffreById(@PathVariable int id) {
        try {
            OffreEmploiResponseDto offre = offreEmploiService.getOffreById(id);
            return ResponseEntity.ok(offre);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }
}
