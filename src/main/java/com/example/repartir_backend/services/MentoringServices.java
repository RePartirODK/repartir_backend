package com.example.repartir_backend.services;

import com.example.repartir_backend.dto.RequestMentoring;
import com.example.repartir_backend.dto.ResponseMentoring;
import com.example.repartir_backend.entities.Jeune;
import com.example.repartir_backend.entities.Mentor;
import com.example.repartir_backend.entities.Mentoring;
import com.example.repartir_backend.enumerations.Etat;
import com.example.repartir_backend.repositories.JeuneRepository;
import com.example.repartir_backend.repositories.MentorRepository;
import com.example.repartir_backend.repositories.MentoringRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MentoringServices {
    private final MentoringRepository mentoringRepository;
    private final MentorRepository mentorRepository;
    private final JeuneRepository jeuneRepository;

    //creation d'un mentoring
    public ResponseMentoring creationMentoring(int idMentor, int idJeune,
                                               RequestMentoring requestMentoringre){
        // on recupère le mentor à travers son id
        Mentor mentor = mentorRepository.findById(idMentor).orElseThrow();
        //on recupère le jeune par son id
        Jeune jeune = jeuneRepository.findById(idJeune).orElseThrow();
        //on créer le mentoring
        Mentoring mentoring = new Mentoring();
        mentoring.setMentor(mentor);
        mentoring.setJeune(jeune);
        mentoring.setDate_debut(LocalDateTime.now());
        mentoring.setDescription(requestMentoringre.getDescription());
        mentoring.setObjectif(requestMentoringre.getObjectif());
        mentoring.setStatut(Etat.ENATTENTE);
        mentoring.setNoteJeune(0);
        mentoring.setNoteMentor(0);

        //on enregistre le mentoring
        return mentoringRepository.save(mentoring).toResponse();
    }

    //mentoring by idMentor
    public List<ResponseMentoring> getMentorAll(int idMentor){
        return mentoringRepository.findAllByMentor_Id(idMentor).stream().map(
                Mentoring::toResponse
        ).toList();
    }
    //mentoring by idJeune
    public List<ResponseMentoring> getJeuneAll(int idJeune)
    {
        return mentoringRepository.findAllByJeune_Id(idJeune).stream().map(
                Mentoring::toResponse
        ).toList();
    }

    //mentor note un mentoring
    public boolean attribuerNoteMentor(int idMentoring, int note)
    {
        //chercher le mentoring correspondant
        Mentoring mentoring = mentoringRepository.findById(idMentoring).orElseThrow();
        mentoring.setNoteMentor(note);
        mentoringRepository.save(mentoring);
        return true;
    }

    public boolean attribuerNoteJeune(int idMentoring, int note)
    {
        //chercher le mentoring correspondant
        Mentoring mentoring = mentoringRepository.findById(idMentoring).orElseThrow();
        mentoring.setNoteJeune(note);
        mentoringRepository.save(mentoring);
        return true;
    }
    public void deleteMentoring(int idMentoring){
        mentoringRepository.deleteById(idMentoring);
    }
}
