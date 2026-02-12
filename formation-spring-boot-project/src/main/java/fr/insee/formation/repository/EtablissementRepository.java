package fr.insee.formation.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.insee.formation.model.Etablissement;
import fr.insee.formation.model.UniteLegale;

public interface EtablissementRepository extends JpaRepository<Etablissement, Long> {
    
    /**
     * Récupère un établissement par son numéro SIRET
     * @param siret le numéro SIRET de l'établissement
     * @return un Optional contenant l'établissement s'il existe
     */
    Optional<Etablissement> findBySiret(String siret);
    
    /**
     * Récupère tous les établissements d'une unité légale
     * @param uniteLegale l'unité légale concernée
     * @return la liste des établissements de cette unité légale
     */
    List<Etablissement> findByUniteLegale(UniteLegale uniteLegale);
    
}
