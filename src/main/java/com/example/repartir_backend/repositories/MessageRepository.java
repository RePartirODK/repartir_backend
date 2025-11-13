package com.example.repartir_backend.repositories;


import com.example.repartir_backend.entities.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Integer> {
    /**
     * Trouver tous les messages d'un mentoring, triés par date ASC
     */
    List<Message> findByMentoringIdOrderByDateAsc(int mentoringId);
}
