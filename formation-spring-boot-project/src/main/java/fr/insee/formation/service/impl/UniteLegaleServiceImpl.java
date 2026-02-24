package fr.insee.formation.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.insee.formation.model.CategorieJuridique;
import fr.insee.formation.model.UniteLegale;
import fr.insee.formation.repository.UniteLegaleRepository;
import fr.insee.formation.repository.dto.UniteLegaleProjection;
import fr.insee.formation.service.UniteLegaleService;

@Service
public class UniteLegaleServiceImpl implements UniteLegaleService {

    private final UniteLegaleRepository uniteLegaleRepository;

    public UniteLegaleServiceImpl(UniteLegaleRepository uniteLegaleRepository) {
        this.uniteLegaleRepository = uniteLegaleRepository;
    }

    @Override
    public List<UniteLegale> findAll() {
        return uniteLegaleRepository.findAll();
    }

    @Override
    public Optional<UniteLegale> findById(Long id) {
        return uniteLegaleRepository.findById(id);
    }

    @Override
    public Optional<UniteLegale> findBySiren(String siren) {
        return uniteLegaleRepository.findBySiren(siren);
    }

    @Override
    public List<UniteLegale> findByDenominationContaining(String denomination) {
        return uniteLegaleRepository.findByDenominationContaining(denomination);
    }

    @Override
    public List<UniteLegaleProjection> findByCategorieJuridique(CategorieJuridique categorieJuridique) {
        return uniteLegaleRepository.findByCategorieJuridique(categorieJuridique);
    }

    @Override
    public UniteLegale save(UniteLegale uniteLegale) {
        return uniteLegaleRepository.save(uniteLegale);
    }

    @Override
    public void deleteById(Long id) {
        uniteLegaleRepository.deleteById(id);
    }

    @Override
    public String generateCsv(List<UniteLegale> unitesLegales) {
        StringBuilder csv = new StringBuilder();
        csv.append("ID,SIREN,Dénomination,Catégorie Juridique\n");

        for (UniteLegale unite : unitesLegales) {
            csv.append(String.format("%d,%s,%s,%s\n",
                    unite.getId(),
                    unite.getSiren(),
                    unite.getDenomination(),
                    unite.getCategorieJuridique()));
        }

        return csv.toString();
    }

    @Override
    @Transactional
    public Stream<UniteLegale> streamAllUnitesLegales() {
        return uniteLegaleRepository.streamAllBy();
    }

}