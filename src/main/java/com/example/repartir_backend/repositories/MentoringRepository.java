package com.example.repartir_backend.repositories;

import com.example.repartir_backend.entities.Mentoring;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MentoringRepository extends JpaRepository<Mentoring, Integer> {
    List<Mentoring> findAllByMentor_Id(int idMentor);

    List<Mentoring> findAllByJeune_Id(int idJeune);
}
