# Gestion des objets JSON
# avec Jackson et Spring Web

--

## Introduction : Jackson et la sérialisation JSON

- **Jackson** est la librairie par défaut utilisée par Spring Boot pour convertir les objets Java en JSON et inversement.
- Elle est automatiquement incluse via `spring-boot-starter-web`.

--
## Serialisation et désérialisation

- Spring convertit automatiquement les réponses des contrôleurs REST en JSON grâce à Jackson
- Ce processus s'effectue en entrée ou en sortie de l'API
- Possibilité de configuration de la manière dont sont convertis les objets Java

--

## Jackson dans Spring Web

- Jackson est utilisé automatiquement par Spring Boot pour :
  - convertir les requêtes JSON en objets Java : `@RequestBody`
  - convertir les objets Java retournés par les contrôleurs en réponses JSON

--

## Exemple de sérialisation/désérialisation

```java
@PostMapping("/users")
public User create(@RequestBody User user) {
    return userService.save(user);
}
```
- Ici on a : 
  - JSON entrant → User Java → JSON sortant

--

## Pattern recommandé :
## Entity vs DTO

- **Pour les petites APIs** : Utiliser directement l'Entity avec les annotations Jacskon
- Plus simple et moins de code
- Possibilité de configuration fine de la sérialization/deserialization (cf suite)

--

## Pattern recommandé :
## Entity vs DTO

- **Pour les APIs complexes** : Créer des DTOs pour contrôler précisément la structure JSON
- Mieux pour les API publiques et évolutives
- Décorrélation des classes internes métiers/classes exposées via API

--

## Les annotations Jackson courantes

- Permettent de configurer la sérialisation/desérialisation au niveau des entités/DTO
- Indispensable si utilisation directe des entités dans l'API (option sans DTO)
- Configuration au niveau classes/attributs ou via configuration globale

--

## Annotations Jackson courantes

| Annotation | Usage |
|---|---|
| `@JsonProperty("user_name")` | renommer un champ JSON |
| `@JsonIgnore` | ignorer un champ connu |
| `@JsonIgnoreProperties(ignoreUnknown = true)` | ignorer les champs inconnus reçus |
| `@JsonInclude(NON_NULL)` | ne pas afficher les champs `null` |

--

## Annotations Jackson courantes

| Annotation | Usage |
|---|---|
| `@JsonAlias(...)` | accepter plusieurs noms en entrée |
| `@JsonFormat(...)` | formater une date |
| `@JsonNaming(...)` | appliquer une convention de nommage, par exemple `snake_case` |

--

## Exemple

```java
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UserDto {

    @JsonProperty("user_name")
    private String userName;

    @JsonIgnore
    private String password;

    @JsonAlias({"mail", "emailAddress"})
    private String email;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate;
}
```
--

## Configuration globale via application.properties

```properties title="application.properties"
# Activer la sérialisation des dates en ISO-8601
# équivalent @JsonFormat(pattern = "yyyy-MM-dd") sur un champ date
spring.jackson.serialization.write-dates-as-timestamps=false

# Indenter le JSON pour la lisibilité
spring.jackson.serialization.indent-output=true

# Ignorer les propriétés inconnues lors de la désérialisation
# équivalent global de @JsonIgnoreProperties(ignoreUnknown = true)
spring.jackson.deserialization.fail-on-unknown-properties=false

# Enlever les champs null
# équivalent global de @JsonInclude(JsonInclude.Include.NON_NULL)
spring.jackson.default-property-inclusion=non_null

# Utiliser snake_case par défaut
# équivalent global de @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
spring.jackson.property-naming-strategy=SNAKE_CASE
```

--

## Les dépendances circulaires

- Problème : Quand deux entités ont une **relation bidirectionnelle**, Jackson entre dans une boucle infinie de sérialisation :

```java
// Entité Parent
@Entity
public class UniteLegale {
    @OneToMany(mappedBy = "uniteLegale")
    private Set<Etablissement> etablissements;  // Référence vers enfants
}

// Entité Enfant
@Entity
public class Etablissement {
    @ManyToOne
    private UniteLegale uniteLegale;  // Référence vers parent
}
```

--

## Les dépendances circulaires

- Résultat : **StackOverflowError** ou réponse JSON gigantesque avec boucles infinies.

- Exemple :
```json
{
  "id": 1,
  "siren": "123456789",
  "denomination": "Acme Corp",
  "etablissements": [
    {
      "id": 10,
      "siret": "12345678901234",
      "nic": "00001",
      "adresse": "123 Rue Paris",
      "uniteLegale": {
        "id": 1,
        "siren": "123456789",
        "denomination": "Acme Corp",
        "etablissements": [
          {
            "id": 10,
            "siret": "12345678901234",
            "nic": "00001",
            "adresse": "123 Rue Paris",
            "uniteLegale": {
              "id": 1,
              "siren": "123456789",
              "denomination": "Acme Corp",
              "etablissements": [
                {
                  "id": 10,
                  "siret": "12345678901234",
                  "nic": "00001",
                  "adresse": "123 Rue Paris",
                  "uniteLegale": {
                    // ... La boucle continue infiniment ...
                    // StackOverflowError ou réponse géante non terminée
                  }
                }
              ]
            }
          }
        ]
      }
    }
  ]
}
```

--

## Solution 1 : @JsonIgnore

- Ignorer un champ lors de la sérialisation :

```java
@Entity
public class Etablissement {
    @ManyToOne
    @JsonIgnore  // Ne pas sérialiser la référence inverse
    private UniteLegale uniteLegale;
}
```

**Inconvénient** : On perd complètement l'information dans le JSON.

--

## Solution 2 : @JsonManagedReference / @JsonBackReference

- **La meilleure solution** pour les relations bidirectionnelles.
  - Côté parent : @JsonManagedReference
  ```java
  @OneToMany(mappedBy = "uniteLegale")
  @JsonManagedReference  // Inclure cette collection dans le JSON
  private Set<Etablissement> etablissements;
  ```
  - Côté enfant : @JsonBackReference
  ```java
  @ManyToOne
  @JsonBackReference  // Exclure cette référence pour casser la boucle
  private UniteLegale uniteLegale;
  ```

--

## Solution 3 : DTO (Data Transfer Object)

- **Pattern recommandé** pour une API propre et maintenable.

- Créer des classes DTO spécifiques pour les réponses API (séparation classes métier/API) :

```java
// DTOs imbriquées (sans références circulaires)
public class UniteLegaleDTO {
    private Long id;
    private String siren;
    private String denomination;
    private CategorieJuridique categorieJuridique;
    // On inclut EtablissementDTO pour contrôler le contenu
    private Set<EtablissementDTO> etablissements;
}
```

--

## Solution 3 : DTO (Data Transfer Object)


```java
public class EtablissementDTO {
    private Long id;
    private String siret;
    private String nic;
    private String adresse;
    // PAS de uniteLegale pour éviter la boucle
}
```

--

### Mapper avec MapStruct

- Utile pour l'option API propre / mise en place de DTOs
- **MapStruct** est la meilleure solution pour mapper Entity → DTO automatiquement
- Génère le code de mapping à la compilation (pas de réflexion)
- Très performant et type-safe

--

### Mapper avec MapStruct

- MapStruct génère l'implémentation du mapper à la compilation
- Se base sur la correspondance du noms des attributs

```java
// Mapper pour Etablissement
@Mapper(componentModel = "spring")
public interface EtablissementMapper {
    EtablissementDTO toDTO(Etablissement entity);
    Set<EtablissementDTO> toDTOSet(Set<Etablissement> entities);
}

// Mapper pour UniteLegale (utilise EtablissementMapper pour les enfants)
@Mapper(componentModel = "spring", uses = EtablissementMapper.class)
public interface UniteLegaleMapper {
    UniteLegaleDTO toDTO(UniteLegale entity);  // MapStruct mappe aussi etablissements automatiquement
    List<UniteLegaleDTO> toDTOList(List<UniteLegale> entities);
}
```

--

### Mapper avec MapStruct

```java
// Utilisation dans le contrôleur
@RestController
@RequestMapping("/api/unites-legales")
public class UniteLegaleController {
    
    public UniteLegaleController(UniteLegaleService service, UniteLegaleMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<UniteLegaleDTO> get(@PathVariable Long id) {
        UniteLegale unit = service.findById(id);
        return ResponseEntity.ok(mapper.toDTO(unit)); 
        // Conversion automatique (y compris établissements)
        // car on a définit "uses = EtablissementMapper.class"
    }
}
```

--

## Conclusion / Recommandations

- **Pour les petites APIs** : Mapping direct des entités avec @JsonManagedReference / @JsonBackReference pour gérer les relations bidirectionnelles, les annotations Jackson (@JsonProperty, @JsonFormat, etc.) pour ajuster finement la sérialisation/désérialisation

--
## Conclusion / Recommandations

- **Pour les APIs complexes ou publiques** : Privilégiez les DTOs avec MapStruct pour un découplage total entre le modèle métier et l'API et un meilleur contrôle des informations envoyées