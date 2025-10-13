package com.example.repartir_backend.controllers;


import com.example.repartir_backend.entities.Mentor;
import com.example.repartir_backend.services.MentorServices;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/mentors")
public class MentorControllers {

    MentorServices mentorServices;
    public MentorControllers(MentorServices mentorServices){
        this.mentorServices = mentorServices;
    }
    @GetMapping
    public ResponseEntity<?> getAllMentors() {
        try {
            return ResponseEntity.ok(mentorServices.getAllMentors());
        } catch (Exception e) {
            return new ResponseEntity<>(
                    "Une erreur est survenue",
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }

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
}
