package fr.insee.formation.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import fr.insee.formation.dto.UniteLegaleProjection;
import fr.insee.formation.model.CategorieJuridique;
import fr.insee.formation.model.Etablissement;
import fr.insee.formation.model.UniteLegale;

//@DataJpaTest Permet de construire les beans repository pour les tests
//et monte une base de données en mémoire H2
@DataJpaTest
@ActiveProfiles("dev")
public class UniteLegaleRepositoryTest {

    @Autowired
    private UniteLegaleRepository uniteLegaleRepository;

    @Autowired
    private EtablissementRepository etablissementRepository;

    @Test
    public void testFindBySiren() {
        UniteLegale uniteLegale = new UniteLegale();
        uniteLegale.setSiren("123456789");
        uniteLegale.setDenomination("Test Company");
        uniteLegale.setCategorieJuridique(CategorieJuridique.SA);
        uniteLegaleRepository.save(uniteLegale);

        Optional<UniteLegale> found = uniteLegaleRepository.findBySiren("123456789");
        assertTrue(found.isPresent());
        assertEquals("Test Company", found.get().getDenomination());
        assertEquals(CategorieJuridique.SA, found.get().getCategorieJuridique());
    }

    @Test
    public void testFindByDenominationContaining() {
        UniteLegale uniteLegale1 = new UniteLegale();
        uniteLegale1.setSiren("123456789");
        uniteLegale1.setDenomination("Test Company 1");
        uniteLegale1.setCategorieJuridique(CategorieJuridique.SA);
        uniteLegaleRepository.save(uniteLegale1);

        UniteLegale uniteLegale2 = new UniteLegale();
        uniteLegale2.setSiren("987654321");
        uniteLegale2.setDenomination("Test Company 2");
        uniteLegale2.setCategorieJuridique(CategorieJuridique.SARL);
        uniteLegaleRepository.save(uniteLegale2);

        List<UniteLegale> result = uniteLegaleRepository.findByDenominationContaining("Test Company");
        assertEquals(2, result.size());
    }

    @Test
    public void testFindUniteLegaleByCategorieJuridiqueProjection() {
        UniteLegale uniteLegale1 = new UniteLegale();
        uniteLegale1.setSiren("123456789");
        uniteLegale1.setDenomination("Test Company 1");
        uniteLegale1.setCategorieJuridique(CategorieJuridique.SA);
        uniteLegaleRepository.save(uniteLegale1);

        UniteLegale uniteLegale2 = new UniteLegale();
        uniteLegale2.setSiren("987654321");
        uniteLegale2.setDenomination("Test Company 2");
        uniteLegale2.setCategorieJuridique(CategorieJuridique.SA);
        uniteLegaleRepository.save(uniteLegale2);

        List<UniteLegaleProjection> result = uniteLegaleRepository.findByCategorieJuridique(CategorieJuridique.SA);
        assertEquals(2, result.size());
        assertEquals("123456789", result.get(0).getSiren());
        assertEquals("Test Company 1", result.get(0).getDenomination());
        assertEquals(CategorieJuridique.SA, result.get(0).getCategorieJuridique());
    }

    @Test
    public void testFindByCategorieJuridiqueWithEtablissements() {
        // Créer une unité légale
        UniteLegale uniteLegale1 = new UniteLegale();
        uniteLegale1.setSiren("123456789");
        uniteLegale1.setDenomination("Test Company 1");
        uniteLegale1.setCategorieJuridique(CategorieJuridique.SA);
        uniteLegaleRepository.save(uniteLegale1);

        // Créer un établissement associé à l'unité légale
        Etablissement etablissement1 = new Etablissement();
        etablissement1.setSiret("12345678900001");
        etablissement1.setNic("00001");
        etablissement1.setAdresse("1 Rue de Test");
        etablissement1.setUniteLegale(uniteLegale1);
        etablissementRepository.save(etablissement1);

        // Créer une deuxième unité légale
        UniteLegale uniteLegale2 = new UniteLegale();
        uniteLegale2.setSiren("987654321");
        uniteLegale2.setDenomination("Test Company 2");
        uniteLegale2.setCategorieJuridique(CategorieJuridique.SA);
        uniteLegaleRepository.save(uniteLegale2);

        // Créer un établissement associé à la deuxième unité légale
        Etablissement etablissement2 = new Etablissement();
        etablissement2.setSiret("98765432100001");
        etablissement2.setNic("00001");
        etablissement2.setAdresse("2 Rue de Test");
        etablissement2.setUniteLegale(uniteLegale2);
        etablissementRepository.save(etablissement2);

        // Tester la méthode
        List<UniteLegale> result = uniteLegaleRepository.findByCategorieJuridiqueWithEtablissements(CategorieJuridique.SA);
        assertEquals(2, result.size());

        // Vérifier que les établissements sont instanciés et non vides
        assertNotNull(result.get(0).getEtablissements());
        assertNotNull(result.get(1).getEtablissements());
        assertEquals(1, result.get(0).getEtablissements().size());
        assertEquals(1, result.get(1).getEtablissements().size());
    }
}