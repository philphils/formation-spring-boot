package fr.insee.formation.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.github.javafaker.Faker;

import fr.insee.formation.model.CategorieJuridique;
import fr.insee.formation.model.Etablissement;
import fr.insee.formation.model.UniteLegale;
import fr.insee.formation.repository.EtablissementRepository;
import fr.insee.formation.repository.UniteLegaleRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

/**
 * Initialiseur de données pour le profil d'intégration.
 * Génère un volume conséquent de données pour tester les performances.
 */
@Component
@Profile("integration")
public class SireneDataIntegrationInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(SireneDataIntegrationInitializer.class);

    private final UniteLegaleRepository uniteLegaleRepository;
    private final EtablissementRepository etablissementRepository;
    @PersistenceContext
    private EntityManager entityManager;

    private final Faker faker = new Faker();
    private final Random random = new Random();

    // Paramètres pour le volume de données
    private static final int NB_UNITES_LEGALES = 1000000;
    private static final int MIN_ETABLISSEMENTS_PER_UNITE = 1;
    private static final int MAX_ETABLISSEMENTS_PER_UNITE = 5;

    // Ensembles pour suivre les SIREN et SIRET déjà générés
    private final Set<String> generatedSirens = Collections.synchronizedSet(new HashSet<>());
    private final Set<String> generatedSirets = Collections.synchronizedSet(new HashSet<>());

    public SireneDataIntegrationInitializer(UniteLegaleRepository uniteLegaleRepository,
            EtablissementRepository etablissementRepository) {
        this.uniteLegaleRepository = uniteLegaleRepository;
        this.etablissementRepository = etablissementRepository;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (uniteLegaleRepository.count() > 0) {
            logger.info("Des données existent déjà. Initialisation annulée.");
            return;
        }

        logger.info("Début de l'initialisation des données pour le profil integration...");

        // Génération par lots pour éviter les problèmes de mémoire
        int batchSize = 1000;
        List<UniteLegale> batch = new ArrayList<>(batchSize);

        for (int i = 0; i < NB_UNITES_LEGALES; i++) {
            UniteLegale unite = createRandomUniteLegale();
            batch.add(unite);

            // Sauvegarde par lots
            if (batch.size() % batchSize == 0) {
                uniteLegaleRepository.saveAll(batch);
                entityManager.flush();
                entityManager.clear();
                batch.clear();
                logger.info("{} unités légales créées", i + 1);
            }

            // Création des établissements associés
            int nbEtablissements = MIN_ETABLISSEMENTS_PER_UNITE +
                    random.nextInt(MAX_ETABLISSEMENTS_PER_UNITE - MIN_ETABLISSEMENTS_PER_UNITE + 1);

            for (int j = 0; j < nbEtablissements; j++) {
                Etablissement etab = createRandomEtablissement(unite);
                unite.addEtablissement(etab);
                etab.setUniteLegale(unite);
            }
        }

        // Sauvegarde des dernières unités légales (et leurs établissements)
        if (!batch.isEmpty()) {
            uniteLegaleRepository.saveAll(batch);
            entityManager.flush();
            entityManager.clear();
        }

        logger.info("✓ Initialisation terminée");
        logger.info("✓ {} unités légales créées", uniteLegaleRepository.count());
        logger.info("✓ {} établissements créés", etablissementRepository.count());
    }

    private UniteLegale createRandomUniteLegale() {
        UniteLegale unite = new UniteLegale();
        unite.setSiren(generateUniqueSiren());
        unite.setDenomination(faker.company().name());
        unite.setCategorieJuridique(getRandomCategorieJuridique());
        return unite;
    }

    private Etablissement createRandomEtablissement(UniteLegale uniteLegale) {
        Etablissement etab = new Etablissement();
        etab.setSiret(generateUniqueSiret(uniteLegale.getSiren()));
        etab.setNic(generateNic());
        etab.setAdresse(faker.address().fullAddress());
        return etab;
    }

    /**
     * Génère un SIREN unique (9 chiffres)
     */
    private String generateUniqueSiren() {
        String siren;
        int attempts = 0;
        do {
            siren = generateSiren();
            attempts++;
            if (attempts > 1000) {
                throw new RuntimeException("Impossible de générer un SIREN unique après 1000 tentatives");
            }
        } while (generatedSirens.contains(siren));

        generatedSirens.add(siren);
        return siren;
    }

    /**
     * Génère un SIRET unique (14 chiffres) basé sur le SIREN de l'unité légale
     */
    private String generateUniqueSiret(String siren) {
        String siret;
        int attempts = 0;
        do {
            siret = siren + generateNic();
            attempts++;
            if (attempts > 1000) {
                throw new RuntimeException("Impossible de générer un SIRET unique après 1000 tentatives");
            }
        } while (generatedSirets.contains(siret));

        generatedSirets.add(siret);
        return siret;
    }

    private String generateSiren() {
        return IntStream.range(0, 9)
                .mapToObj(i -> String.valueOf(random.nextInt(10)))
                .collect(Collectors.joining());
    }

    private String generateNic() {
        return IntStream.range(0, 5)
                .mapToObj(i -> String.valueOf(random.nextInt(10)))
                .collect(Collectors.joining());
    }

    private CategorieJuridique getRandomCategorieJuridique() {
        CategorieJuridique[] categories = CategorieJuridique.values();
        return categories[random.nextInt(categories.length)];
    }
}