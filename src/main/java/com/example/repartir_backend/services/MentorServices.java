package com.example.repartir_backend.services;


import com.example.repartir_backend.entities.Mentor;
import com.example.repartir_backend.repositories.MentorRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MentorServices {
    MentorRepository mentorRepository;
    MentorServices(MentorRepository mentorRepository){
        this.mentorRepository = mentorRepository;
    }

    //lister tous les mentor
    public List<Mentor> getAllMentors() {
        return mentorRepository.findAll();
    }

    //recuper un mentor
    public Mentor getMentor(int idMentor){
        return mentorRepository.findById(idMentor).orElseThrow(
                ()-> new EntityNotFoundException("Mentor non trouvé à l'id"+ idMentor)
        );
    }

    //supprimer un mentor
    public void deleteMentor(int id) {
        mentorRepository.deleteById(id);
    }
}
