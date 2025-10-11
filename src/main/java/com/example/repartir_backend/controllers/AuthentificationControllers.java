package com.example.repartir_backend.controllers;

import com.example.repartir_backend.dto.RequestUtilisateur;
import com.example.repartir_backend.security.JwtServices;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthentificationControllers {
    private final AuthenticationManager authenticationManager;
    private final JwtServices jwtServices;
    @PostMapping("/login")
    public ResponseEntity<?> Login(@RequestBody RequestUtilisateur credential) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(credential.getEmail()
                        , credential.getMotDePasse())
        );
        if(auth.isAuthenticated()){
            UserDetails userDetails = (UserDetails) auth.getPrincipal();
            //generer le token
            String token = jwtServices.genererToken(credential.getEmail());
            return new ResponseEntity<>(
                    new JwtResponse(token,userDetails.getUsername(),userDetails.getAuthorities()),
                    HttpStatus.OK
            );
        }
        else {
            return new ResponseEntity<>(
                    "Mot de passe ou email incorrecte",
                    HttpStatus.NOT_FOUND
            );
        }
    }
    public static record JwtResponse(String token, String email, Object roles) {}
}
