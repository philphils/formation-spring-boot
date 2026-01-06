package fr.insee.formation.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.insee.formation.model.Etablissement;

public interface EtablissementRepository extends JpaRepository<Etablissement, Long> {

}
