package com.example.repartir_backend.controllers;

import com.example.repartir_backend.services.UserDomaineServices;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/userdomaines")
@RequiredArgsConstructor
public class UserDomaineControllers {
    private final UserDomaineServices userDomaineServices;
}
