package com.example.repartir_backend.services;

import com.example.repartir_backend.dto.RequestFormation;
import com.example.repartir_backend.dto.ResponseFormation;
import com.example.repartir_backend.entities.CentreFormation;
import com.example.repartir_backend.entities.Formation;
import com.example.repartir_backend.enumerations.Etat;
import com.example.repartir_backend.repositories.CentreFormationRepository;
import com.example.repartir_backend.repositories.FormationRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FormationServices {
    private final FormationRepository formationRepository;
    private final CentreFormationRepository centreFormationRepository;

    //creation d'une formation
    public Formation createFormation(RequestFormation requestFormation, int centreId) {
        CentreFormation centre = centreFormationRepository.findById(centreId)
                .orElseThrow(() -> new EntityNotFoundException("Centre de formation introuvable"));
        Formation formation = new Formation().toFormation(requestFormation);
        formation.setCentreFormation(centre);
        formation.setStatut(Etat.EN_ATTENTE); // par défaut
        return formationRepository.save(formation);
    }
    //mettre à jour une formation
    // Mettre à jour une formation
    public ResponseFormation updateFormation(int id, RequestFormation requestFormation) {
        Formation formation = formationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Formation non trouvée"));

        if (requestFormation.getTitre() != null)
            formation.setTitre(requestFormation.getTitre());

        if (requestFormation.getDescription() != null)
            formation.setDescription(requestFormation.getDescription());

        if (requestFormation.getDate_debut() != null)
            formation.setDate_debut(requestFormation.getDate_debut());

        if (requestFormation.getDate_fin() != null)
            formation.setDate_fin(requestFormation.getDate_fin());

        if (requestFormation.getCout() != null)
            formation.setCout(requestFormation.getCout());

        if (requestFormation.getNbrePlace() != null)
            formation.setNbre_place(requestFormation.getNbrePlace());

        if (requestFormation.getFormat() != null)
            formation.setFormat(requestFormation.getFormat());

        if (requestFormation.getDuree() != null)
            formation.setDuree(requestFormation.getDuree());

        if (requestFormation.getUrlFormation() != null)
            formation.setUrlFormation(requestFormation.getUrlFormation());

        if (requestFormation.getUrlCertificat() != null)
            formation.setUrlCertificat(requestFormation.getUrlCertificat());

        if (requestFormation.getStatut() != null)
            formation.setStatut(requestFormation.getStatut());

        Formation updatedFormation = formationRepository.save(formation);
        return updatedFormation.toResponse();
    }

    //mettre à jour le statut d'une formation
    public Formation updateStatut(int id, Etat statut) {
        Formation formation = formationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Formation introuvable"));
        formation.setStatut(statut);
        return formationRepository.save(formation);
    }
    //supprimer une formation
    public void deleteFormation(int id) {
        if (!formationRepository.existsById(id)) {
            throw new EntityNotFoundException("Formation introuvable");
        }
        formationRepository.deleteById(id);
    }

    //recuperer toutes les formations
    public List<ResponseFormation> getAllFormations() {
        return formationRepository.findAll().stream().map(
                Formation::toResponse
        ).toList();
    }

    //recuperer les formations d'un centre
    public List<ResponseFormation> getFormationsByCentre(int centreId) {
        return formationRepository.findByCentreFormationId(centreId).stream().map(
                Formation::toResponse
        ).toList();
    }

    public ResponseFormation getFormationById(int id) {
        return formationRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("formation avec l'id non trouvée: " + id)
        ).toResponse();
    }
}
