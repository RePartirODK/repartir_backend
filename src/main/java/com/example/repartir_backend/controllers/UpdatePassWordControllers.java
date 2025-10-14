package com.example.repartir_backend.controllers;

import com.example.repartir_backend.dto.UpdatePasswordRequest;
import com.example.repartir_backend.entities.UserDetailsImpl;
import com.example.repartir_backend.services.UpdatePassWordService;
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
public class UpdatePassWordControllers {
    private final UpdatePassWordService updatePassWordService;
    @PutMapping("/{id}")
    public ResponseEntity<String> changePassWord(@PathVariable int id,
                                                 @RequestBody UpdatePasswordRequest request,
                                                 @AuthenticationPrincipal UserDetailsImpl userDetails){

        /*
         * verifier que la personne voulant modifier le mot de passe
         * Est bien celle connecté ou bien un admin
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
