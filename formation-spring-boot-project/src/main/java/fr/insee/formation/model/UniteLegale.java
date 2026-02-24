package fr.insee.formation.model;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "unite_legale")
@Getter
@Setter
public class UniteLegale {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "unite_seq")
    @SequenceGenerator(name = "unite_seq", sequenceName = "unite_seq", allocationSize = 50)
    private Long id;

    @Column(nullable = false, unique = true)
    private String siren;

    @Column(nullable = false)
    private String denomination;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategorieJuridique categorieJuridique;

    @OneToMany(mappedBy = "uniteLegale", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    @Setter(value = lombok.AccessLevel.NONE)
    private Set<Etablissement> etablissements = new HashSet<>();

    // Les constructeurs, getters et setters sont générés par Lombok

    // Méthode pour ajouter un établissement à l'unité légale et maintenir la
    // relation bidirectionnelle
    public void addEtablissement(Etablissement etablissement) {
        etablissements.add(etablissement);
        if (etablissement.getUniteLegale() != this) {
            etablissement.setUniteLegale(this);
        }
    }

}