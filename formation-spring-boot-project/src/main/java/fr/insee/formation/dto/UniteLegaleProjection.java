package fr.insee.formation.dto;

import fr.insee.formation.model.CategorieJuridique;

public interface UniteLegaleProjection {
    String getSiren();
    String getDenomination();
    CategorieJuridique getCategorieJuridique();
}
