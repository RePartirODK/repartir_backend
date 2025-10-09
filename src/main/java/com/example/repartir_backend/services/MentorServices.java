package com.example.repartir_backend.services;

import com.example.repartir_backend.repositories.MentorRepository;
import org.springframework.stereotype.Service;

@Service
public class MentorServices {
    MentorRepository mentorRepository;
    public MentorServices(MentorRepository mentorRepository){
        this.mentorRepository = mentorRepository;
    }
}
