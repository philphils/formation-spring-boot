package fr.insee.formation.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.insee.formation.repository.EtablissementRepository;
import fr.insee.formation.repository.UniteLegaleRepository;

@RestController
public class DatabaseCheckController {

    @Autowired
    private UniteLegaleRepository uniteLegaleRepository;

    @Autowired
    private EtablissementRepository etablissementRepository;

    @GetMapping("/check-database")
    public String checkDatabase() {
        long uniteLegaleCount = uniteLegaleRepository.count();
        long etablissementCount = etablissementRepository.count();

        if (uniteLegaleCount == 5 && etablissementCount == 6) {
            return "Ok ! On a bien " + uniteLegaleCount + " unités légales et " + etablissementCount + " établissements.";
        } else {
            return "Erreur : Nombre de lignes incorrect. UniteLegale: " + uniteLegaleCount + ", Etablissement: " + etablissementCount;
        }
    }
}
