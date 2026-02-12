package fr.insee.formation.service;

import java.util.List;
import java.util.Optional;

import fr.insee.formation.model.Etablissement;
import fr.insee.formation.model.UniteLegale;

public interface EtablissementService {
    
    /**
     * Récupère tous les établissements
     * @return la liste de tous les établissements
     */
    List<Etablissement> findAll();
    
    /**
     * Récupère un établissement par son identifiant
     * @param id l'identifiant de l'établissement
     * @return un Optional contenant l'établissement si il existe
     */
    Optional<Etablissement> findById(Long id);
    
    /**
     * Récupère un établissement par son SIRET
     * @param siret le numéro SIRET de l'établissement
     * @return un Optional contenant l'établissement si il existe
     */
    Optional<Etablissement> findBySiret(String siret);
    
    /**
     * Récupère tous les établissements d'une unité légale
     * @param uniteLegale l'unité légale concernée
     * @return la liste des établissements de cette unité légale
     */
    List<Etablissement> findByUniteLegale(UniteLegale uniteLegale);
    
    /**
     * Crée ou met à jour un établissement
     * @param etablissement l'établissement à sauvegarder
     * @return l'établissement sauvegardé
     */
    Etablissement save(Etablissement etablissement);
    
    /**
     * Supprime un établissement par son identifiant
     * @param id l'identifiant de l'établissement à supprimer
     */
    void deleteById(Long id);
    
}
