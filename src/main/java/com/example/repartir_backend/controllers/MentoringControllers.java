package com.example.repartir_backend.controllers;

import com.example.repartir_backend.dto.RequestMentoring;
import com.example.repartir_backend.dto.ResponseMentoring;
import com.example.repartir_backend.entities.Mentoring;
import com.example.repartir_backend.enumerations.Etat;
import com.example.repartir_backend.services.MentoringServices;
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
public class MentoringControllers {
   private final MentoringServices mentoringServices;

    @PostMapping("/create/{idMentor}/{idJeune}")
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
    @GetMapping("/mentor/{idMentor}")
    public ResponseEntity<List<ResponseMentoring>> getMentorAll(@PathVariable int idMentor) {
        return ResponseEntity.ok(mentoringServices.getMentorAll(idMentor));
    }
    @GetMapping("/jeune/{idJeune}")
    public ResponseEntity<List<ResponseMentoring>> getJeuneAll(@PathVariable int idJeune) {
        return ResponseEntity.ok(mentoringServices.getJeuneAll(idJeune));
    }
    @PutMapping("/note/mentor/{idMentoring}")
    public ResponseEntity<String> attribuerNoteMentor(
            @PathVariable int idMentoring,
            @RequestParam int note) {
        boolean result = mentoringServices.attribuerNoteMentor(idMentoring, note);
        return result
                ? ResponseEntity.ok("Note du mentor attribuée avec succès.")
                : ResponseEntity.badRequest().body("Erreur lors de l'attribution de la note.");
    }
    @PutMapping("/note/jeune/{idMentoring}")
    public ResponseEntity<String> attribuerNoteJeune(
            @PathVariable int idMentoring,
            @RequestParam int note) {
        boolean result = mentoringServices.attribuerNoteJeune(idMentoring, note);
        return result
                ? ResponseEntity.ok("Note du jeune attribuée avec succès.")
                : ResponseEntity.badRequest().body("Erreur lors de l'attribution de la note.");
    }
    @DeleteMapping("/{idMentoring}")
    public ResponseEntity<String> deleteMentoring(@PathVariable int idMentoring) {
        mentoringServices.deleteMentoring(idMentoring);
        return ResponseEntity.ok("Mentoring supprimé avec succès.");
    }

    @PatchMapping("/{idMentoring}/accepter")
    @PreAuthorize("hasRole('MENTOR')")
    public ResponseEntity<ResponseMentoring> accepterMentoring(@PathVariable int idMentoring) {
        Mentoring updatedMentoring = mentoringServices.accepterMentoring(idMentoring);
        return ResponseEntity.ok(updatedMentoring.toResponse());
    }

    @PatchMapping("/{idMentoring}/refuser")
    @PreAuthorize("hasRole('MENTOR')")
    public ResponseEntity<ResponseMentoring> refuserMentoring(@PathVariable int idMentoring) {
        Mentoring updatedMentoring = mentoringServices.refuserMentoring(idMentoring);
        return ResponseEntity.ok(updatedMentoring.toResponse());
    }
}
