package com.example.repartir_backend.entities;

import com.example.repartir_backend.enumerations.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Utilisateur {
    @Id
    private int id;
}
