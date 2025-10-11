package com.example.repartir_backend.controllers;

import com.example.repartir_backend.services.MentorServices;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/mentorings")
@RequiredArgsConstructor
public class Mentoring {
    private final MentorServices mentorServices;
}
