package com.example.repartir_backend.dto;

import com.example.repartir_backend.entities.Mentor;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
public class MentorResponseDto {
    private int id;
    private String nomComplet;
    private String email;
    private int annee_experience;
    private String a_propos;
    private String profession;
    private String urlPhoto;

    public static MentorResponseDto fromEntity(Mentor mentor) {
        return MentorResponseDto.builder()
                .id(mentor.getId())
                .nomComplet(mentor.getPrenom() + " " + mentor.getUtilisateur().getNom())
                .email(mentor.getUtilisateur().getEmail())
                .annee_experience(mentor.getAnnee_experience())
                .a_propos(mentor.getA_propos())
                .profession(mentor.getProfession())
                .urlPhoto(mentor.getUtilisateur().getUrlPhoto())
                .build();
    }

    public static List<MentorResponseDto> fromEntities(List<Mentor> mentors) {
        return mentors.stream()
                .map(MentorResponseDto::fromEntity)
                .collect(Collectors.toList());
    }
}
