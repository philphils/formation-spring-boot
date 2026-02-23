package fr.insee.formation.config;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.github.javafaker.Faker;

import fr.insee.formation.model.CategorieJuridique;
import fr.insee.formation.model.Etablissement;
import fr.insee.formation.model.UniteLegale;
import fr.insee.formation.service.EtablissementService;
import fr.insee.formation.service.UniteLegaleService;

/**
 * Initialise les données SIRENE pour le profil de développement.
 * Cette classe crée automatiquement quelques unités légales et établissements
 * au démarrage de l'application avec le profil "dev".
 */
@Component
@Profile("dev")
public class SireneDataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(SireneDataInitializer.class);

    private final UniteLegaleService uniteLegaleService;
    private final EtablissementService etablissementService;
    private final Faker faker = new Faker();
    private final Random random = new Random();

    private Set<String> generatedSirens = new HashSet<>();
    private Set<String> generatedSirets = new HashSet<>();

    public SireneDataInitializer(UniteLegaleService uniteLegaleService,
                                 EtablissementService etablissementService) {
        this.uniteLegaleService = uniteLegaleService;
        this.etablissementService = etablissementService;
    }

    @Override
    public void run(String... args) throws Exception {
        try {
            // Vérifier s'il y a déjà des données
            if (uniteLegaleService.findAll().isEmpty()) {
                logger.info("Initialisation des données SIRENE pour le profil dev...");
                initializeData();
                logger.info("✓ Données SIRENE initialisées avec succès");
            } else {
                logger.info("Les données sont déjà présentes en base de données. Initialisation ignorée.");
            }
        } catch (Exception e) {
            logger.error("Erreur lors de l'initialisation des données SIRENE", e);
        }
    }

    private void initializeData() {
        CategorieJuridique[] categories = CategorieJuridique.values();

        // Créer 5 unités légales
        for (int i = 1; i <= 5; i++) {
            UniteLegale uniteLegale = new UniteLegale();
            uniteLegale.setSiren(generateUniqueSiren());
            uniteLegale.setDenomination(faker.company().name());
            uniteLegale.setCategorieJuridique(categories[random.nextInt(categories.length)]);

            UniteLegale savedUnite = uniteLegaleService.save(uniteLegale);
            logger.debug("Unité légale créée: {} (SIREN: {})", savedUnite.getDenomination(), savedUnite.getSiren());

            // Créer 1-2 établissements pour chaque unité légale
            int nbEtablissements = random.nextInt(2) + 1; // 1 ou 2
            for (int j = 0; j < nbEtablissements; j++) {
                Etablissement etablissement = new Etablissement();
                etablissement.setSiret(generateUniqueSiret());
                etablissement.setNic(generateNic());
                etablissement.setAdresse(faker.address().fullAddress());
                etablissement.setUniteLegale(savedUnite);

                Etablissement savedEtab = etablissementService.save(etablissement);
                logger.debug("Établissement créé: {} (SIRET: {})", savedEtab.getAdresse(), savedEtab.getSiret());
            }
        }

        logger.info("✓ {} unités légales créées", uniteLegaleService.findAll().size());
        logger.info("✓ {} établissements créés", etablissementService.findAll().size());
    }

    /**
     * Génère un numéro SIREN unique (9 chiffres)
     * Vérifie l'unicité en base de données et dans la session courante
     */
    private String generateUniqueSiren() {
        String siren;
        do {
            siren = generateSiren();
        } while (generatedSirens.contains(siren) || uniteLegaleService.findBySiren(siren).isPresent());

        generatedSirens.add(siren);
        return siren;
    }

    /**
     * Génère un numéro SIRET unique (14 chiffres = 9 du SIREN + 5 du NIC)
     * Vérifie l'unicité en base de données et dans la session courante
     */
    private String generateUniqueSiret() {
        String siret;
        do {
            siret = generateSiret();
        } while (generatedSirets.contains(siret) || etablissementService.findBySiret(siret).isPresent());

        generatedSirets.add(siret);
        return siret;
    }

    /**
     * Génère un numéro SIREN fictif (9 chiffres)
     */
    private String generateSiren() {
        StringBuilder siren = new StringBuilder();
        for (int i = 0; i < 9; i++) {
            siren.append(random.nextInt(10));
        }
        return siren.toString();
    }

    /**
     * Génère un numéro SIRET fictif (14 chiffres = 9 du SIREN + 5 du NIC)
     */
    private String generateSiret() {
        return generateSiren() + generateNic();
    }

    /**
     * Génère un numéro NIC fictif (5 chiffres)
     */
    private String generateNic() {
        StringBuilder nic = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            nic.append(random.nextInt(10));
        }
        return nic.toString();
    }
}