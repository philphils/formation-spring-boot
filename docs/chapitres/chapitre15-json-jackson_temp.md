# Gestion des objets JSON
# avec Jackson et Spring Web

--

## Introduction : Jackson et la sérialisation JSON

- **Jackson** est la librairie par défaut utilisée par Spring Boot pour convertir les objets Java en JSON et inversement.
- Elle est automatiquement incluse via `spring-boot-starter-web`.

--
## Introduction : Jackson et la sérialisation JSON

- Spring convertit automatiquement les réponses des contrôleurs REST en JSON grâce à Jackson.

```java
@GetMapping("/users/{id}")
public User getUser(@PathVariable Long id) {
    // L'objet User est automatiquement sérialisé en JSON par Jackson
    return userService.findById(id);
}
```

--

## Sérialisation et désérialisation
### Désérialisation : JSON → Objet Java

**Processus** : Conversion automatique d'une requête HTTP JSON en objet Java

**Exemple avec `@PostMapping`** :
```java
@PostMapping("/users")
public User createUser(@RequestBody User user) {
    // Jackson convertit automatiquement le JSON en objet User
    return userService.save(user);
}
```

**Requête HTTP d'entrée** :
```http
POST /users
Content-Type: application/json

{
  "id": 1,
  "name": "Alice Dupont",
  "email": "alice.dupont@example.com",
  "createdAt": "2023-10-15T09:30:00"
}
```

**Ce qui se passe** :
1. Le client envoie un JSON dans le corps de la requête
2. Spring utilise Jackson pour désérialiser le JSON en objet `User`
3. L'objet est passé en paramètre de la méthode

--

## Sérialisation et désérialisation
### Sérialisation : Objet Java → JSON

**Processus** : Conversion automatique d'un objet Java en réponse HTTP JSON

**Exemple avec `@GetMapping`** :
```java
@GetMapping("/users/{id}")
public User getUser(@PathVariable Long id) {
    // Jackson convertit automatiquement l'objet en JSON
    return userService.findById(id);
}
```

**Réponse HTTP générée** :
```http
HTTP/1.1 200 OK
Content-Type: application/json

{
  "id": 1,
  "name": "Alice Dupont",
  "email": "alice.dupont@example.com",
  "createdAt": "2023-10-15T09:30:00"
}
```

**Ce qui se passe** :
1. La méthode retourne un objet `User`
2. Spring utilise Jackson pour sérialiser l'objet en JSON
3. Le JSON est envoyé au client dans le corps de la réponse

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

- **La meilleure solution** pour gérer les relations bidirectionnelles.

```java
// Côté Parent (celui qui "gère" la relation)
@Entity
public class UniteLegale {
    @OneToMany(mappedBy = "uniteLegale")
    @JsonManagedReference  // Inclure cette collection dans le JSON
    private Set<Etablissement> etablissements;
}
```

--

## Solution 2 : @JsonManagedReference / @JsonBackReference

- Côté enfant :

```java
// Côté Enfant (celui qui "référence" le parent)
@Entity
public class Etablissement {
    @ManyToOne
    @JsonBackReference  // Exclure cette référence pour casser la boucle
    private UniteLegale uniteLegale;
}
```

--

## Solution 2 : @JsonManagedReference / @JsonBackReference

- **Résultat JSON** :
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
      "adresse": "123 Rue Test"
      // uniteLegale n'est PAS inclus
    }
  ]
}
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

- **Utilisation :**

```java
// Utilisation dans le contrôleur
@RestController
@RequestMapping("/api/unites-legales")
public class UniteLegaleController {
    
    @Autowired
    private UniteLegaleMapper mapper;
    
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

## Pattern recommandé :
## Entity vs DTO

- **Pour les petites APIs** : Utiliser directement l'Entity avec @JsonBackReference et @JsonManagedReference
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

- Utiles si utilisation directe des entités dans l'API (option sans DTO)
- Configuration directement dans les entités avec les annotations Jackson
- Pattern moins "propre" mais utile pour les petites APIs

--

### @JsonProperty

- Renommer un champ dans le JSON (serialization et deserialization):

```java
@Entity
public class User {
    @JsonProperty("user_name")  
    // Dans le JSON : "user_name" au lieu de "userName"
    private String userName;
}
```

- **JSON** :
```json
{
"user_name": "john_doe"
}
```

--

### @JsonIgnore

- Ignorer un champ (ne pas le sérialiser) :

```java
@Entity
public class User {
    private String email;
    
    @JsonIgnore  // Ne pas inclure dans le JSON
    private String password;
}
```

--

### @JsonInclude

- Contrôler l'inclusion des valeurs null :

```java
@Entity
@JsonInclude(JsonInclude.Include.NON_NULL)  
// Ignorer les champs null
public class User {
    private String name;
    private String middleName; 
    // null → non inclus dans le JSON
}
```

--

### @JsonAlias

- Accepter plusieurs noms lors de la désérialisation :

```java
@Entity
public class User {
    @JsonAlias({"user_name", "username"})  
    // Accepter ces trois noms
    private String userName;
}
```

- **Fonctionne avec ces JSON** :
```json
{"userName": "john"} ou {"user_name": "john"} ou {"username": "john"}
```

--

## Gestion des types complexes

### LocalDate et LocalDateTime

- Par défaut, Jackson peut ne pas bien gérer les dates Java 8+ :

```java
@Entity
public class Event {
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate eventDate;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
}
```

--

**Configuration globale dans application.properties** :
```properties
spring.jackson.serialization.write-dates-as-timestamps=false
spring.jackson.time-zone=Europe/Paris
spring.jackson.locale=fr_FR
```

--

## Customisation fine de la sérialisation

### @JsonNaming

- Appliquer une stratégie de nommage globale pour alléger les annotations :

```java
@Entity
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class User {
    private String firstName;  // → "first_name" dans le JSON
    private String lastName;   // → "last_name" dans le JSON
}
```

--

## Configuration Jackson dans Spring Boot

### application.properties :

```properties
# Activer la sérialisation des dates en ISO-8601
spring.jackson.serialization.write-dates-as-timestamps=false

# Indenter le JSON pour la lisibilité
spring.jackson.serialization.indent-output=true

# Ignorer les propriétés inconnues lors de la désérialisation
spring.jackson.deserialization.fail-on-unknown-properties=false

# Enlever les champs null
spring.jackson.default-property-inclusion=non_null

# Utiliser snake_case par défaut
spring.jackson.property-naming-strategy=SNAKE_CASE
```

--

## Bonnes pratiques : Utilisation de `ResponseEntity`

- **Contrôle total sur la réponse HTTP** :
  - Permet de personnaliser le **code de statut** (ex: `200 OK`, `404 Not Found`, `201 Created`).
  - Permet d'ajouter des **en-têtes HTTP** personnalisés.

- **Flexibilité et maintenabilité** :
  - Facilite la gestion des erreurs et des réponses complexes.
  - Rend le code plus explicite et conforme aux conventions Spring.

--

## Bonnes pratiques : Utilisation de `ResponseEntity`


- **Exemple d'utilisation** :
  ```java
  @GetMapping("/users")
  public ResponseEntity<List<User>> getUsers() {
      return ResponseEntity.ok(userService.findAll());
  }
  ```

- **Cas d'usage avancé** :
  ```java
  @PostMapping("/users")
  public ResponseEntity<User> createUser(@RequestBody User user) {
      User createdUser = userService.save(user);
      return ResponseEntity
          .status(HttpStatus.CREATED)
          .header("Location", "/users/" + createdUser.getId())
          .body(createdUser);
  }
  ```

--

## Conclusion / Recommandations

- **Pour les petites APIs** : Mapping direct des entités avec @JsonManagedReference / @JsonBackReference pour gérer les relations bidirectionnelles, les annotations Jackson (@JsonProperty, @JsonFormat, etc.) pour ajuster finement la sérialisation/désérialisation
- **Pour les APIs complexes ou publiques** : Privilégiez les DTOs avec MapStruct pour un découplage total entre le modèle métier et l'API et un meilleur contrôle des informations envoyées