package fr.insee.formation.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import fr.insee.formation.model.Etablissement;
import fr.insee.formation.model.UniteLegale;
import fr.insee.formation.repository.EtablissementRepository;
import fr.insee.formation.service.EtablissementService;

@Service
public class EtablissementServiceImpl implements EtablissementService {

    private final EtablissementRepository etablissementRepository;

    public EtablissementServiceImpl(EtablissementRepository etablissementRepository) {
        this.etablissementRepository = etablissementRepository;
    }

    @Override
    public List<Etablissement> findAll() {
        return etablissementRepository.findAll();
    }

    @Override
    public Optional<Etablissement> findById(Long id) {
        return etablissementRepository.findById(id);
    }

    @Override
    public Optional<Etablissement> findBySiret(String siret) {
        return etablissementRepository.findBySiret(siret);
    }

    @Override
    public List<Etablissement> findByUniteLegale(UniteLegale uniteLegale) {
        return etablissementRepository.findByUniteLegale(uniteLegale);
    }

    @Override
    public Etablissement save(Etablissement etablissement) {
        return etablissementRepository.save(etablissement);
    }

    @Override
    public void deleteById(Long id) {
        etablissementRepository.deleteById(id);
    }

    @Override
    public String generateCsv(List<Etablissement> etablissements) {
        StringBuilder csv = new StringBuilder();
        csv.append("ID,SIRET,NIC,Adresse,Unité Légale ID\n");

        for (Etablissement etab : etablissements) {
            csv.append(String.format("%d,%s,%s,%s,%d\n",
                    etab.getId(),
                    etab.getSiret(),
                    etab.getNic(),
                    etab.getAdresse(),
                    etab.getUniteLegale().getId()));
        }

        return csv.toString();
    }

}
