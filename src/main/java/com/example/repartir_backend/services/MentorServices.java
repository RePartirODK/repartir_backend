package com.example.repartir_backend.services;


import com.example.repartir_backend.dto.MentorResponseDto;
import com.example.repartir_backend.dto.MentorUpdateDto;
import com.example.repartir_backend.entities.Mentor;
import com.example.repartir_backend.entities.Utilisateur;
import com.example.repartir_backend.repositories.MentorRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MentorServices {
    MentorRepository mentorRepository;
    MentorServices(MentorRepository mentorRepository){
        this.mentorRepository = mentorRepository;
    }

    //lister tous les mentor
    @Transactional(readOnly = true)
    public List<MentorResponseDto> getAllMentors() {
        List<Mentor> mentors = mentorRepository.findAll();
        return MentorResponseDto.fromEntities(mentors);
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

    //recuperer un mentor par email
    @Transactional(readOnly = true)
    public Mentor getMentorByEmail(String email) {
        return mentorRepository.findByUtilisateur_Email(email)
            .orElseThrow(() -> new EntityNotFoundException("Mentor non trouvé avec l'email: " + email));
    }

    //mettre à jour un mentor
    @Transactional
    public Mentor updateMentor(int idMentor, MentorUpdateDto updateDto) {
        Mentor mentor = mentorRepository.findById(idMentor)
            .orElseThrow(() -> new EntityNotFoundException("Mentor non trouvé avec l'id: " + idMentor));
        
        // Mettre à jour les champs du Mentor
        if (updateDto.getPrenom() != null && !updateDto.getPrenom().isEmpty()) {
            mentor.setPrenom(updateDto.getPrenom());
        }
        
        if (updateDto.getProfession() != null && !updateDto.getProfession().isEmpty()) {
            mentor.setProfession(updateDto.getProfession());
        }
        
        if (updateDto.getAnnee_experience() > 0) {
            mentor.setAnnee_experience(updateDto.getAnnee_experience());
        }
        
        if (updateDto.getA_propos() != null) {
            mentor.setA_propos(updateDto.getA_propos());
        }
        
        // Mettre à jour nom et téléphone dans Utilisateur
        Utilisateur utilisateur = mentor.getUtilisateur();
        
        if (updateDto.getNom() != null && !updateDto.getNom().isEmpty()) {
            utilisateur.setNom(updateDto.getNom());
        }
        
        if (updateDto.getTelephone() != null && !updateDto.getTelephone().isEmpty()) {
            utilisateur.setTelephone(updateDto.getTelephone());
        }
        
        return mentorRepository.save(mentor);
    }
}
