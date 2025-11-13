package com.example.repartir_backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class StaticResourceConfiguration implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Servir les fichiers depuis le dossier uploads
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:C:/Users/DELL Latitude/Desktop/uploads/");
    }
}

