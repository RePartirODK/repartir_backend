package com.example.repartir_backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Classe de configuration pour les beans de l'application.
 */
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI repartirApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("RePartir API")
                        .description("Documentation des endpoints RePartir (jeunes, parrains, formations...)")
                        .version("1.0.0"));
    }
}

