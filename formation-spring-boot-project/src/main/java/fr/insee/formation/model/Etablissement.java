package fr.insee.formation.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "etablissement")
@Getter
@Setter
public class Etablissement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String siret;

    @Column(nullable = false)
    private String nic;

    @Column(nullable = false)
    private String adresse;

    @ManyToOne
    @JoinColumn(name = "unite_legale_id", nullable = false)
    @Setter(value = lombok.AccessLevel.NONE)
    private UniteLegale uniteLegale;

    // Les constructeurs, getters et setters sont générés par Lombok

    // Méthode pour gérer la relation bidirectionnelle
    public void setUniteLegale(UniteLegale uniteLegale) {
        this.uniteLegale = uniteLegale;
        if (uniteLegale != null && !uniteLegale.getEtablissements().contains(this)) {
            uniteLegale.addEtablissement(this);
        }
    }
}