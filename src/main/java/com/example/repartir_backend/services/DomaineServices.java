package com.example.repartir_backend.services;

import com.example.repartir_backend.repositories.DomaineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.repartir_backend.entities.Domaine;
import com.example.repartir_backend.dto.DomaineDto;
import com.example.repartir_backend.dto.DomaineResponseDto;
import jakarta.persistence.EntityNotFoundException;

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

    @Transactional
    public DomaineResponseDto modifierDomaine(int id, DomaineDto domaineDto) {
        Domaine domaine = domaineRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Domaine non trouvé avec l'ID : " + id));
        
        domaine.setLibelle(domaineDto.libelle());
        Domaine domaineSauvegarde = domaineRepository.save(domaine);
        
        return DomaineResponseDto.fromEntity(domaineSauvegarde);
    }

    @Transactional
    public void supprimerDomaine(int id) {
        if (!domaineRepository.existsById(id)) {
            throw new EntityNotFoundException("Domaine non trouvé avec l'ID : " + id);
        }
        domaineRepository.deleteById(id);
    }
}
