package com.example.repartir_backend.services;

import com.example.repartir_backend.repositories.DomaineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.example.repartir_backend.entities.Domaine;
import com.example.repartir_backend.dto.DomaineDto;
import com.example.repartir_backend.dto.DomaineResponseDto;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DomaineServices {
    private final DomaineRepository domaineRepository;

    public Domaine creerDomaine(DomaineDto domaineDto) {
        Domaine domaine = new Domaine();
        domaine.setLibelle(domaineDto.libelle());
        return domaineRepository.save(domaine);
    }

    public List<DomaineResponseDto> listerDomaines() {
        List<Domaine> domaines = domaineRepository.findAll();
        return DomaineResponseDto.fromEntities(domaines);
    }
}
