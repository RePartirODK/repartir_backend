package com.example.repartir_backend.controllers;

import com.example.repartir_backend.dto.RequestMentoring;
import com.example.repartir_backend.dto.ResponseMentoring;
import com.example.repartir_backend.entities.Mentoring;
import com.example.repartir_backend.enumerations.Etat;
import com.example.repartir_backend.services.MentoringServices;
import io.jsonwebtoken.io.IOException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/mentorings")
@RequiredArgsConstructor
@Tag(name = "Mentoring", description = "Endpoints pour la gestion des relations de mentorat")
public class MentoringControllers {
   private final MentoringServices mentoringServices;

    @PostMapping("/create/{idMentor}/{idJeune}")
    @Operation(summary = "Créer un mentoring", description = "Permet à un mentor et un jeune d'initier une relation de mentorat.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Mentoring créé avec succès."),
            @ApiResponse(responseCode = "404", description = "Mentor ou jeune non trouvé."),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur.")
    })
    public ResponseEntity<?> createMentoring(
            @PathVariable int idMentor,
            @PathVariable int idJeune,
            @RequestBody RequestMentoring requestMentoring) {
        try {
            ResponseMentoring mentoring = mentoringServices.creationMentoring(idMentor, idJeune, requestMentoring);
            return ResponseEntity.ok(mentoring);
        }catch (EntityNotFoundException e)
        {
            return new ResponseEntity<>(
                    "Mentor ou Jeune non trouvé",
                    HttpStatus.NOT_FOUND
            );
        } catch (RuntimeException e) {
            return  new ResponseEntity<>(
                    "Une erreur est servenue" + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }

    }
    @Operation(summary = "Lister les mentorings d’un mentor")
    @GetMapping("/mentor/{idMentor}")
    public ResponseEntity<List<ResponseMentoring>> getMentorAll(@PathVariable int idMentor) {
        return ResponseEntity.ok(mentoringServices.getMentorAll(idMentor));
    }
    @GetMapping("/jeune/{idJeune}")
    public ResponseEntity<List<ResponseMentoring>> getJeuneAll(@PathVariable int idJeune) {
        return ResponseEntity.ok(mentoringServices.getJeuneAll(idJeune));
    }
    @PutMapping("/note/mentor/{idMentoring}")
    @Operation(summary = "Attribuer une note à un jeune")
    public ResponseEntity<String> attribuerNoteMentor(
            @PathVariable int idMentoring,
            @RequestParam int note) {
        boolean result = mentoringServices.attribuerNoteMentor(idMentoring, note);
        return result
                ? ResponseEntity.ok("Note du mentor attribuée avec succès.")
                : ResponseEntity.badRequest().body("Erreur lors de l'attribution de la note.");
    }
    @PutMapping("/note/jeune/{idMentoring}")
    @Operation(summary = "Attribuer une note à un montor")
    public ResponseEntity<String> attribuerNoteJeune(
            @PathVariable int idMentoring,
            @RequestParam int note) {
        boolean result = mentoringServices.attribuerNoteJeune(idMentoring, note);
        return result
                ? ResponseEntity.ok("Note du jeune attribuée avec succès.")
                : ResponseEntity.badRequest().body("Erreur lors de l'attribution de la note.");
    }
    @DeleteMapping("/{idMentoring}")
    @Operation(summary = "Supprimer un mentoring")
    public ResponseEntity<String> deleteMentoring(@PathVariable int idMentoring) {
        mentoringServices.deleteMentoring(idMentoring);
        return ResponseEntity.ok("Mentoring supprimé avec succès.");
    }

    @PatchMapping("/{idMentoring}/accepter")
    @PreAuthorize("hasRole('MENTOR')")
    @Operation(summary = "Accepter un mentoring", description = "Permet à un mentor d'accepter une demande de mentorat.")

    public ResponseEntity<?> accepterMentoring(@PathVariable int idMentoring) {
        try {
            Mentoring updatedMentoring = mentoringServices.accepterMentoring(idMentoring);
            return ResponseEntity.ok(updatedMentoring.toResponse());
        }catch (MessagingException | IOException | java.io.IOException e)
        {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Une erreur interne s'est produit");
        }

    }

    @PatchMapping("/{idMentoring}/refuser")
    @PreAuthorize("hasRole('MENTOR')")
    @Operation(summary = "Refuser un mentoring", description = "Permet à un mentor de refuser une demande de mentorat.")

    public ResponseEntity<?> refuserMentoring(@PathVariable int idMentoring)  {
        try {
            Mentoring updatedMentoring = mentoringServices.refuserMentoring(idMentoring);
            return ResponseEntity.ok(updatedMentoring.toResponse());
        }catch (MessagingException | IOException | java.io.IOException e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Une erreur interne s'est produit");
        }

    }
}
