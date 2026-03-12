package fr.insee.formation.model;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonManagedReference;

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
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
    @NotBlank(message = "Le SIREN est obligatoire")
    @Size(min = 9, max = 9, message = "Le SIREN doit contenir exactement 9 caractères")
    private String siren;

    @Column(nullable = false)
    @NotBlank(message = "La dénomination est obligatoire")
    private String denomination;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull(message = "La catégorie juridique est obligatoire")
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