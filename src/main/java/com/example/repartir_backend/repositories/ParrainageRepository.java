package com.example.repartir_backend.repositories;

import com.example.repartir_backend.dto.ResponseParrainage;
import com.example.repartir_backend.entities.Parrainage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ParrainageRepository extends JpaRepository<Parrainage, Integer> {
    boolean existsByJeune_IdAndParrain_IdAndFormation_Id(int idJeune, int idParrain, int idFormation);

    List<Parrainage> findAllByJeune_Id(int idJeune);
    List<Parrainage> findAllByParrain_Id(int idParrain);
    List<Parrainage> findAllByFormation_Id(int idFormation);

    boolean existsByJeune_IdAndParrainIsNullAndFormation_Id(int idJeune, int idFormation);
}
