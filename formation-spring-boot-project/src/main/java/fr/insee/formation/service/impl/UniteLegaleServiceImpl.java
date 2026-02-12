package fr.insee.formation.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

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

}
