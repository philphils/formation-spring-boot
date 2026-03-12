package fr.insee.formation.model;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "etablissement")
@Getter
@Setter
public class Etablissement {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "etablissement_seq")
    @SequenceGenerator(name = "etablissement_seq", sequenceName = "etablissement_seq", allocationSize = 50)
    private Long id;

    @Column(nullable = false, unique = true)
    @NotBlank(message = "Le SIRET est obligatoire")
    @Size(min = 14, max = 14, message = "Le SIRET doit contenir exactement 14 caractères")
    private String siret;

    @Column(nullable = false)
    @NotBlank(message = "Le NIC est obligatoire")
    @Size(min = 5, max = 5, message = "Le NIC doit contenir exactement 5 caractères")
    private String nic;

    @Column(nullable = false)
    @NotBlank(message = "L'adresse est obligatoire")
    private String adresse;

    @ManyToOne
    @JoinColumn(name = "unite_legale_id", nullable = false)
    @JsonBackReference
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