package com.example.repartir_backend.services;

import com.example.repartir_backend.entities.Domaine;
import com.example.repartir_backend.entities.UserDomaine;
import com.example.repartir_backend.entities.Utilisateur;
import com.example.repartir_backend.repositories.DomaineRepository;
import com.example.repartir_backend.repositories.UserDomaineRepository;
import com.example.repartir_backend.repositories.UtilisateurRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserDomaineServices {
    private final UserDomaineRepository userDomaineRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final DomaineRepository domaineRepository;

    /**
     * Associe un utilisateur à un domaine.
     */
    @Transactional
    public UserDomaine addUserToDomaine(int userId, int domaineId) {
        Utilisateur utilisateur = utilisateurRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé avec ID : " + userId));

        Domaine domaine = domaineRepository.findById(domaineId)
                .orElseThrow(() -> new EntityNotFoundException("Domaine non trouvé avec ID : " + domaineId));

        UserDomaine userDomaine = new UserDomaine();
        userDomaine.setUtilisateur(utilisateur);
        userDomaine.setDomaine(domaine);

        return userDomaineRepository.save(userDomaine);
    }

    /**
     * Récupère tous les domaines liés à un utilisateur.
     */
    @Transactional
    public List<UserDomaine> getDomainesByUtilisateur(int userId) {
        return userDomaineRepository.findByUtilisateurId(userId);
    }

    /**
     * Récupère tous les utilisateurs liés à un domaine.
     */
    @Transactional
    public List<UserDomaine> getUtilisateursByDomaine(int domaineId) {
        return userDomaineRepository.findByDomaineId(domaineId);
    }

    /**
     * Supprime un lien utilisateur–domaine.
     */
    @Transactional
    public void removeUserFromDomaine(int userId, int domaineId) {
        UserDomaine userDomaine = userDomaineRepository
                .findByUtilisateurIdAndDomaineId(userId, domaineId).orElseThrow(() -> new EntityNotFoundException("Association non trouvée entre l’utilisateur et le domaine."));

        userDomaineRepository.delete(userDomaine);
    }
}
