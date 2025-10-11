package com.example.repartir_backend.repositories;


import com.example.repartir_backend.entities.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Integer> {
}
