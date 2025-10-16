package com.example.repartir_backend.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Configuration principale de la sécurité de l'application.
 * Gère l'authentification, les autorisations, la configuration de CORS et la politique de session.
 */
@Configuration
@EnableWebSecurity
// Ajout de @EnableMethodSecurity pour activer la sécurité au niveau des méthodes (ex: @PreAuthorize)
@EnableMethodSecurity
public class SecurityConfig {
    private final JwtAuthFilter jwtAuthFilter;
    private final UserDetailsService userDetailsService;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter, UserDetailsService userDetailsService) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.userDetailsService = userDetailsService;
    }

    /*
     * Principale configuration de la securité
     * Definition des règles d'accès des différentes endpoint
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity,
                                                   CorsConfigurationSource corsConfigurationSource) throws Exception{
        httpSecurity
                // Ajout explicite de la configuration CORS au début de la chaîne de filtres.
                // Cela résout les erreurs 403 Forbidden sur les endpoints publics en s'assurant
                // que les règles CORS sont appliquées avant les règles de sécurité.
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                //desactiver le csrf, pas besoin car jwt est sans etat
                .csrf(AbstractHttpConfigurer::disable)
                //configuration des authorisation des endpoints
                .authorizeHttpRequests(
                        auth -> auth.requestMatchers(
                                "/api/auth/login", "/api/utilisateurs/register", "/api/auth/refresh", "/ws/**"
                        ).permitAll()
                                // Correction du chemin pour les administrateurs et ajout de règles spécifiques
                                .requestMatchers("/administrateurs/**").hasRole("ADMIN")
                                .requestMatchers("/api/domaines/**").hasRole("ADMIN")
                                .requestMatchers("/api/entreprise/**").hasRole("ENTREPRISE")
                                   .requestMatchers("/api/parrains/**").hasAnyRole("PARRAIN"
                                ,"ADMIN")
                                .requestMatchers("/api/mentors/**").hasAnyRole("MENTOR", "JEUNE",
                                        "ADMIN")
                                .requestMatchers("/api/centres/**").hasAnyRole("CENTRE", "ADMIN")
                                .requestMatchers("/api/entreprises/**").hasAnyRole("ENTREPRISE",
                                        "ADMIN")
                                .requestMatchers("/api/inscription/**").hasAnyRole("CENTRE", "PARRAIN",
                                        "JEUNE", "ADMIN")
                                .requestMatchers("/api/jeunes/**").hasAnyRole("JEUNE", "PARRAIN",
                                        "ENTREPRISE", "CENTRE", "ADMIN")
                                .requestMatchers("/api/mentoring/**").hasAnyRole("MENTOR", "JEUNE",
                                        "ADMIN")
                                .requestMatchers("/api/messages/**").hasAnyRole( "MENTOR",
                                        "JEUNE","ADMIN")
                                // Seuls les JEUNES et les ADMINS peuvent voir toutes les offres.
                                // Les entreprises utilisent /api/entreprise/offres pour voir les leurs.
                                .requestMatchers("/api/offres/lister").hasAnyRole("JEUNE", "ADMIN")
                                .requestMatchers("/api/offres/**").hasAnyRole("ENTREPRISE","JEUNE",
                                        "ADMIN")
                                .requestMatchers("/api/paiements/**").hasAnyRole("ENTREPRISE",
                                        "PARRAIN", "JEUNE", "ADMIN")
                                .requestMatchers("/api/parrainage/**")
                                .hasAnyRole("PARRAIN", "JEUNE", "CENTRE", "ADMIN")
                                .requestMatchers("/api/userdomaines/**")
                                .hasAnyRole("ADMIN", "MENTOR", "CENTRE","ENTREPRISE",
                                        "PARRAIN", "JEUNE")
                                // Les CENTRES ne peuvent plus voir toutes les formations via cet endpoint.
                                // Ils utiliseront leur endpoint dédié.
                                .requestMatchers("/api/formations/**").hasAnyRole("ADMIN", "MENTOR",
                                        "PARRAIN", "JEUNE", "CENTRE")
                                .requestMatchers("/api/updatepassword/**")
                                .hasAnyRole("ADMIN", "MENTOR", "CENTRE","ENTREPRISE",
                                        "PARRAIN", "JEUNE")
                                .anyRequest()
                                .authenticated()





                )
                //.exceptionHandling(ex -> ex.authenticationEntryPoint(unauthorizedHandler))
                //Session sans etat (requis pour JWT)
                .sessionManagement(
                        sess -> sess.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS))
                //un provider d'authentification
                .authenticationProvider(authentificationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
                return httpSecurity.build();
    }

    /*
     * Bean pour encoder le mot de passe (utilisation du hachage BCrypt)
     * Necessaire pour stocker dans la base de données
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    /*
     * Configuration du provider d'authentification
     * Il lie le userdetails et passwordencoder
     */
    @Bean
    public AuthenticationProvider authentificationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(
                userDetailsService
        );
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    /*
     * Bean pour de gestion d'authentification
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
    /*
     * Configuration du cross site origin
     * Definition des origines qui peuvent appeler les endpoints
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource(){
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedMethods(List.of("POST", "GET", "PUT", "DELETE", "PATCH"));
        corsConfiguration.addAllowedHeader("*");
        corsConfiguration.setAllowedOrigins(List.of("http://localhost:8083", "http://localhost:4200"));
        corsConfiguration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);

        return source;
    }
}
