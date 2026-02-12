package fr.insee.formation.service;

import java.util.List;
import java.util.Optional;

import fr.insee.formation.model.CategorieJuridique;
import fr.insee.formation.model.UniteLegale;
import fr.insee.formation.repository.dto.UniteLegaleProjection;

public interface UniteLegaleService {
    
    /**
     * Récupère toutes les unités légales
     * @return la liste de toutes les unités légales
     */
    List<UniteLegale> findAll();
    
    /**
     * Récupère une unité légale par son identifiant
     * @param id l'identifiant de l'unité légale
     * @return un Optional contenant l'unité légale si elle existe
     */
    Optional<UniteLegale> findById(Long id);
    
    /**
     * Récupère une unité légale par son SIREN
     * @param siren le numéro SIREN de l'unité légale
     * @return un Optional contenant l'unité légale si elle existe
     */
    Optional<UniteLegale> findBySiren(String siren);
    
    /**
     * Récupère les unités légales dont la dénomination contient le texte donné
     * @param denomination le texte à rechercher dans la dénomination
     * @return la liste des unités légales correspondantes
     */
    List<UniteLegale> findByDenominationContaining(String denomination);
    
    /**
     * Récupère les unités légales d'une certaine catégorie juridique
     * @param categorieJuridique la catégorie juridique recherchée
     * @return la liste des projections des unités légales
     */
    List<UniteLegaleProjection> findByCategorieJuridique(CategorieJuridique categorieJuridique);
    
    /**
     * Crée ou met à jour une unité légale
     * @param uniteLegale l'unité légale à sauvegarder
     * @return l'unité légale sauvegardée
     */
    UniteLegale save(UniteLegale uniteLegale);
    
    /**
     * Supprime une unité légale par son identifiant
     * @param id l'identifiant de l'unité légale à supprimer
     */
    void deleteById(Long id);
    
}
