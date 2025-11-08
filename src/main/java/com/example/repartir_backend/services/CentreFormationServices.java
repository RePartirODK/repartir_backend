package com.example.repartir_backend.services;
import com.example.repartir_backend.dto.RegisterUtilisateur;
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


    @Transactional(readOnly = true)
    public List<ResponseCentre> getAllCentres() {
        return centreFormationRepository.findAll().stream()
                .map(CentreFormation::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ResponseCentre> getCentresActifs() {
        return centreFormationRepository.findAll()
                .stream()
                .filter(c -> c.getUtilisateur() != null && c.getUtilisateur().isEstActive())
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
    /**
     * Met à jour les informations d'un centre.
     */
    public ResponseCentre updateCentreV1(RegisterUtilisateur request) {
        // Chercher l'utilisateur lié au centre via l'email
        Utilisateur utilisateur = utilisateurRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé pour l'email : " + request.getEmail()));

        // Mettre à jour les champs modifiables
        utilisateur.setNom(request.getNom());
        utilisateur.setTelephone(request.getTelephone());
        // Pour le mot de passe, soit tu le modifies ici, soit via une page séparée
        // utilisateur.setMotDePasse(request.getMotDePasse());

        // Sauvegarder l'utilisateur
        utilisateurRepository.save(utilisateur);

        // Mettre à jour le centre (adresse, agrément)
        CentreFormation centre = centreFormationRepository.findByUtilisateur(utilisateur)
                .orElseThrow(() -> new EntityNotFoundException("Centre non trouvé pour l'utilisateur : " + utilisateur.getEmail()));

        centre.setAdresse(request.getAdresse());
        centre.setAgrement(request.getAgrement());

        // Sauvegarder le centre
        centreFormationRepository.save(centre);

        // Retourner la réponse
        return centre.toResponse();
    }

    @Transactional
    public ResponseCentre activerCentre(int idCentre) {
        CentreFormation centre = centreFormationRepository.findById(idCentre)
                .orElseThrow(() -> new EntityNotFoundException("Centre introuvable"));
        centre.getUtilisateur().setEstActive(true);
        CentreFormation saved = centreFormationRepository.save(centre);
        centreFormationRepository.flush();
        return saved.toResponse();
    }

    @Transactional
    public ResponseCentre desactiverCentre(int idCentre) {
        CentreFormation centre = centreFormationRepository.findById(idCentre)
                .orElseThrow(() -> new EntityNotFoundException("Centre introuvable"));
        centre.getUtilisateur().setEstActive(false);
        CentreFormation saved = centreFormationRepository.save(centre);
        centreFormationRepository.flush();
        return saved.toResponse();
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
