package com.example.repartir_backend.controllers;

import com.example.repartir_backend.services.MessageServices;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class MessageControllers {
    private final MessageServices messageServices;
}
