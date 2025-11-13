package com.example.repartir_backend.controllers;


import com.example.repartir_backend.dto.MentorResponseDto;
import com.example.repartir_backend.dto.MentorUpdateDto;
import com.example.repartir_backend.entities.Mentor;
import com.example.repartir_backend.services.MentorServices;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/mentors")
public class MentorControllers {

    MentorServices mentorServices;
    public MentorControllers(MentorServices mentorServices){
        this.mentorServices = mentorServices;
    }
    @GetMapping
    public ResponseEntity<List<MentorResponseDto>> getAllMentors() {
        return ResponseEntity.ok(mentorServices.getAllMentors());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getMentorById(@PathVariable int id) {
        try {
            Mentor mentor = mentorServices.getMentor(id);
            return ResponseEntity.ok(mentor);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(
                    e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMentor(@PathVariable int id) {
        mentorServices.deleteMentor(id);
        return ResponseEntity.ok("Mentor supprimé avec succès");
    }

    @GetMapping("/profile")
    @PreAuthorize("hasRole('MENTOR')")
    @Operation(summary = "Récupérer le profil du mentor connecté")
    public ResponseEntity<?> getMentorProfile(Principal principal) {
        try {
            String email = principal.getName(); // Email depuis le JWT
            Mentor mentor = mentorServices.getMentorByEmail(email);
            return ResponseEntity.ok(MentorResponseDto.fromEntity(mentor));
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(
                "Mentor non trouvé",
                HttpStatus.NOT_FOUND
            );
        } catch (RuntimeException e) {
            return new ResponseEntity<>(
                e.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('MENTOR')")
    @Operation(summary = "Mettre à jour le profil d'un mentor")
    public ResponseEntity<?> updateMentor(
            @PathVariable int id,
            @RequestBody MentorUpdateDto updateDto,
            Principal principal) {
        try {
            // Vérifier que le mentor modifie son propre profil
            String email = principal.getName();
            Mentor currentMentor = mentorServices.getMentorByEmail(email);
            
            if (currentMentor.getId() != id) {
                return new ResponseEntity<>(
                    "Vous ne pouvez modifier que votre propre profil",
                    HttpStatus.FORBIDDEN
                );
            }
            
            Mentor updatedMentor = mentorServices.updateMentor(id, updateDto);
            return ResponseEntity.ok(MentorResponseDto.fromEntity(updatedMentor));
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>("Mentor non trouvé", HttpStatus.NOT_FOUND);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
