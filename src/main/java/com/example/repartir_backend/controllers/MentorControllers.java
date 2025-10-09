package com.example.repartir_backend.controllers;

import com.example.repartir_backend.services.MentorServices;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/mentors")
public class MentorControllers {
    MentorServices mentorServices;
    public MentorControllers(MentorServices mentorServices){
        this.mentorServices = mentorServices;
    }
}
