package com.example.repartir_backend.controllers;

import com.example.repartir_backend.dto.LogoutRequest;
import com.example.repartir_backend.services.AuthService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/logout")
@RequiredArgsConstructor
public class LogoutControllers {
    private final AuthService authService;
    @DeleteMapping
    public ResponseEntity<?> logout(@RequestBody LogoutRequest request)
    {
        try {
            authService.logout(request.getEmail());
            return ResponseEntity.ok("Deconnecté");
        }catch (EntityNotFoundException e)
        {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Une erreur s'est produite");
        }

    }
}
