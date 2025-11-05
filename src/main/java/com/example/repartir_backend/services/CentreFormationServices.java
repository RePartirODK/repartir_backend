package com.example.repartir_backend.services;
import com.example.repartir_backend.dto.ResponseCentre;
import com.example.repartir_backend.entities.CentreFormation;
import com.example.repartir_backend.entities.Formation;
import com.example.repartir_backend.entities.Utilisateur;
import com.example.repartir_backend.repositories.CentreFormationRepository;
import com.example.repartir_backend.repositories.FormationRepository;
import com.example.repartir_backend.repositories.UtilisateurRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CentreFormationServices {
    private final CentreFormationRepository centreFormationRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final FormationRepository formationRepository;

    public CentreFormation getCentreById(int id) {
        return centreFormationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Centre introuvable avec l’ID : " + id));
    }


    public List<ResponseCentre> getAllCentres() {
        return centreFormationRepository.findAll().stream().map(
                CentreFormation::toResponse
        ).toList();
    }

    public List<ResponseCentre> getCentresActifs() {
        return centreFormationRepository.findAll()
                .stream()
                .filter(c -> c.getUtilisateur().isEstActive())
                .map(CentreFormation::toResponse)
                .toList();
    }

    public ResponseCentre getCentreByEmail(String email) {
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Aucun utilisateur avec cet email."));
        return centreFormationRepository.findByUtilisateur(utilisateur)
                .orElseThrow(() -> new EntityNotFoundException("Aucun centre associé à cet utilisateur."
                        + utilisateur.getEmail()))
                .toResponse();
    }

    @Transactional
    public CentreFormation updateCentre(int id, CentreFormation centreDetails) {
        CentreFormation existing = centreFormationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Centre introuvable"));

        existing.setAdresse(centreDetails.getAdresse());
        existing.setAgrement(centreDetails.getAgrement());

        return centreFormationRepository.save(existing);
    }

    @Transactional
    public ResponseCentre activerCentre(int idCentre) {
        CentreFormation centre = centreFormationRepository.findById(idCentre)
                .orElseThrow(() -> new EntityNotFoundException("Centre introuvable"));
        centre.getUtilisateur().setEstActive(true);
        return centreFormationRepository.save(centre).toResponse();
    }

    @Transactional
    public ResponseCentre desactiverCentre(int idCentre) {
        CentreFormation centre = centreFormationRepository.findById(idCentre)
                .orElseThrow(() -> new EntityNotFoundException("Centre introuvable"));
        centre.getUtilisateur().setEstActive(false);
        return centreFormationRepository.save(centre).toResponse();
    }

    @Transactional
    public void deleteCentre(int idCentre) {
        CentreFormation centre = centreFormationRepository.findById(idCentre)
                .orElseThrow(() -> new EntityNotFoundException("Centre introuvable"));
        centreFormationRepository.delete(centre);
    }

    public List<Formation> getFormationsByCentre(int idCentre) {
        CentreFormation centre = centreFormationRepository.findById(idCentre)
                .orElseThrow(() -> new EntityNotFoundException("Centre introuvable"));
        return formationRepository.findAllByCentreFormation_Id(centre.getId());
    }


}
