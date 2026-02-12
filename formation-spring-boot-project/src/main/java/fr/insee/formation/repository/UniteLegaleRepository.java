package fr.insee.formation.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import fr.insee.formation.model.CategorieJuridique;
import fr.insee.formation.model.UniteLegale;
import fr.insee.formation.repository.dto.UniteLegaleProjection;

public interface UniteLegaleRepository extends JpaRepository<UniteLegale, Long> {
    public Optional<UniteLegale> findBySiren(String siren);

    public List<UniteLegale> findByDenominationContaining(String denomination);

    public List<UniteLegaleProjection> findByCategorieJuridique(CategorieJuridique categorieJuridique);

    @Query("SELECT ul FROM UniteLegale ul LEFT JOIN FETCH ul.etablissements WHERE ul.categorieJuridique = :categorieJuridique")
    public List<UniteLegale> findByCategorieJuridiqueWithEtablissements(@Param("categorieJuridique") CategorieJuridique categorieJuridique);
}
