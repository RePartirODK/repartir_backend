package com.example.repartir_backend.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtServices jwtServices;
    private final UserDetailsService userDetailsService;

    public JwtAuthFilter(JwtServices jwtServices, UserDetailsService userDetailsService) {
        this.jwtServices = jwtServices;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String utilisateurEmail;

        // Si le header est absent ou mal formé, on passe au filtre suivant.
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7);

        try {
            utilisateurEmail = jwtServices.extractUsername(jwt);

            // Si on a un email et que l'utilisateur n'est pas déjà authentifié
            if (utilisateurEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(utilisateurEmail);

                // Si le token est valide, on authentifie l'utilisateur
                if (jwtServices.valideToken(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (ExpiredJwtException | MalformedJwtException | SignatureException e) {
            // En cas d'erreur de validation du token (expiré, malformé, etc.),
            // on ignore simplement et on continue la chaîne de filtres.
            // L'utilisateur ne sera pas authentifié, et la sécurité des endpoints
            // décidera s'il peut continuer.
        }

        filterChain.doFilter(request, response);
    }
}
