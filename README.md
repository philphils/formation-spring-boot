# TP4 : Spring Data JPA
**Objectif** : Maîtriser Spring Data JPA en créant des entités, des repositories avec différentes méthodes de requêtage, des projections DTO, et en configurant des bases de données H2 et PostgreSQL.

**Précision** : Pour ce TP nous n'utiliserons qu'une seule base de données par profil, donc pas de configuration multi-datasource.

---

## Partie 1 : Configuration du projet et des bases de données

### 1.1. Ajouter les dépendances Maven
Ajoutez les dépendances suivantes dans le `pom.xml` :
```xml
<dependencies>
    <!-- Spring Boot Starter Data JPA -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>

    <!-- Base de données H2 (pour le profil dev) -->
    <!-- (Dans notre projet cette dépendance est déjà présente) --> 
    <dependency>
        <groupId>com.h2database</groupId>
        <artifactId>h2</artifactId>
        <scope>runtime</scope>
    </dependency>

    <!-- Base de données PostgreSQL (pour le profil integration) -->
    <dependency>
        <groupId>org.postgresql</groupId>
        <artifactId>postgresql</artifactId>
        <scope>runtime</scope>
    </dependency>

    <!-- Spring Boot Starter Test -->
    <!-- (Dans notre projet cette dépendance est déjà présente) --> 
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```

### 1.2. Configurer les profils de bases de données
Modifier les fichiers de configuration suivants pour ajouter ou remplacer les properties ci-dessous :

#### `src/main/resources/application-dev.properties` (H2)
```properties
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.username=sa
spring.datasource.password=
spring.datasource.driver-class-name=org.h2.Driver
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.use_sql_comments=true
```

#### `src/main/resources/application-integration.properties` (PostgreSQL)
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/sirene_db
spring.datasource.username=postgres
spring.datasource.password=password
spring.datasource.driver-class-name=org.postgresql.Driver
spring.jpa.show-sql=false
spring.jpa.hibernate.ddl-auto=update

# Configuration de la requête de test de connexion
spring.datasource.hikari.connection-test-query=SELECT 1
```
---

## Partie 2 : Création des entités JPA

### 2.1. Entité `UniteLegale`
Créez la classe `UniteLegale` dans le package `fr.insee.formation.model` :

D'abord, créez l'énumération `CategorieJuridique` :
```java
package fr.insee.formation.model;

public enum CategorieJuridique {
    SA, // Société Anonyme
    SARL, // Société à Responsabilité Limitée
    SAS, // Société par Actions Simplifiée
    EI, // Entreprise Individuelle
    EURL, // Entreprise Unipersonnelle à Responsabilité Limitée
    ASSO // Association
}
```

Ensuite, utilisez cette énumération dans l'entité `UniteLegale` (utilisez les imports de la norme JPA présent dans le package Jakarta et  non ceux de la librairie propriétaire Hibernate) :

```java
@Entity
@Table(name = "unite_legale")
@Getter
@Setter
public class UniteLegale {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String siren;

    @Column(nullable = false)
    private String denomination;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategorieJuridique categorieJuridique;

    @OneToMany(mappedBy = "uniteLegale", cascade = CascadeType.ALL, orphanRemoval = true)
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
```

### 2.2. Entité `Etablissement`
Créez la classe `Etablissement` dans le package `fr.insee.formation.model` :

```java
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
```

---

## Partie 3 : Création des projections DTO

### 3.1. Projection `UniteLegaleProjection`
Créez l'interface `UniteLegaleProjection` dans le package `fr.insee.formation.dto`. Cette interface doit déclarer des méthodes pour récupérer les champs `siren`, `denomination`, et `categorieJuridique` de l'entité `UniteLegale`.

### 3.2. Projection `EtablissementProjection`
Créez l'interface `EtablissementProjection` dans le package `fr.insee.formation.dto`. Cette interface doit déclarer des méthodes pour récupérer les champs `siret` et `adresse` de l'entité `Etablissement`.

---

## Partie 4 : Création des repositories Spring Data

### 4.1. Repository `UniteLegaleRepository`
Créez l'interface `UniteLegaleRepository` dans le package `fr.insee.formation.repository` :
- Cette interface doit étendre `JpaRepository<UniteLegale, Long>`.
- Ajoutez des méthodes par convention de nommage pour rechercher :
    - une unité légale par `siren` (vous pouvez utiliser la classe Optional)
    - des unités légales dont la `denomination` contient une chaîne de caractère
    - des projections DTO des unités légales par `categorieJuridique`.
- Ajoutez une méthode personnalisée utilisant `@Query` pour récupérer des unités légales par `categorieJuridique` avec leurs établissements instanciés.

### 4.2. Repository `EtablissementRepository`
Créez l'interface `EtablissementRepository` dans le package `fr.insee.formation.repository` :
- Cette interface doit étendre `JpaRepository<Etablissement, Long>`.
- Ajoutez des méthodes par convention de nommage pour rechercher :
    -  des établissements par `siret`
    - des projections DTO des établissements par `uniteLegale`
    - des établissements dont l'`adresse` commence par une chaîne de caractère

---

## Partie 5 : Tests unitaires

### 5.1. Test `UniteLegaleRepositoryTest`
Créez la classe de test `UniteLegaleRepositoryTest` dans le package `fr.insee.formation.repository` :
```java
@DataJpaTest
@ActiveProfiles("dev")
public class UniteLegaleRepositoryTest {

    @Autowired
    private UniteLegaleRepository uniteLegaleRepository;

    @Autowired
    private EtablissementRepository etablissementRepository;

    @Test
    public void testFindBySiren() {
        UniteLegale uniteLegale = new UniteLegale();
        uniteLegale.setSiren("123456789");
        uniteLegale.setDenomination("Test Company");
        uniteLegale.setCategorieJuridique(CategorieJuridique.SA);
        uniteLegaleRepository.save(uniteLegale);

        Optional<UniteLegale> found = uniteLegaleRepository.findBySiren("123456789");
        assertTrue(found.isPresent());
        assertEquals("Test Company", found.get().getDenomination());
        assertEquals(CategorieJuridique.SA, found.get().getCategorieJuridique());
    }

    @Test
    public void testFindByDenominationContaining() {
        UniteLegale uniteLegale1 = new UniteLegale();
        uniteLegale1.setSiren("123456789");
        uniteLegale1.setDenomination("Test Company 1");
        uniteLegale1.setCategorieJuridique(CategorieJuridique.SA);
        uniteLegaleRepository.save(uniteLegale1);

        UniteLegale uniteLegale2 = new UniteLegale();
        uniteLegale2.setSiren("987654321");
        uniteLegale2.setDenomination("Test Company 2");
        uniteLegale2.setCategorieJuridique(CategorieJuridique.SARL);
        uniteLegaleRepository.save(uniteLegale2);

        List<UniteLegale> result = uniteLegaleRepository.findByDenominationContaining("Test Company");
        assertEquals(2, result.size());
    }

    @Test
    public void testFindByCategorieJuridique() {
        UniteLegale uniteLegale1 = new UniteLegale();
        uniteLegale1.setSiren("123456789");
        uniteLegale1.setDenomination("Test Company 1");
        uniteLegale1.setCategorieJuridique(CategorieJuridique.SA);
        uniteLegaleRepository.save(uniteLegale1);

        UniteLegale uniteLegale2 = new UniteLegale();
        uniteLegale2.setSiren("987654321");
        uniteLegale2.setDenomination("Test Company 2");
        uniteLegale2.setCategorieJuridique(CategorieJuridique.SA);
        uniteLegaleRepository.save(uniteLegale2);

        List<UniteLegaleProjection> result = uniteLegaleRepository.findByCategorieJuridique(CategorieJuridique.SA);
        assertEquals(2, result.size());
    }

    @Test
    public void testFindUniteLegaleByCategorieJuridiqueProjection() {
        UniteLegale uniteLegale1 = new UniteLegale();
        uniteLegale1.setSiren("123456789");
        uniteLegale1.setDenomination("Test Company 1");
        uniteLegale1.setCategorieJuridique(CategorieJuridique.SA);
        uniteLegaleRepository.save(uniteLegale1);

        UniteLegale uniteLegale2 = new UniteLegale();
        uniteLegale2.setSiren("987654321");
        uniteLegale2.setDenomination("Test Company 2");
        uniteLegale2.setCategorieJuridique(CategorieJuridique.SA);
        uniteLegaleRepository.save(uniteLegale2);

        List<UniteLegaleProjection> result = uniteLegaleRepository.findByCategorieJuridique(CategorieJuridique.SA);
        assertEquals(2, result.size());
        assertEquals("123456789", result.get(0).getSiren());
        assertEquals("Test Company 1", result.get(0).getDenomination());
        assertEquals(CategorieJuridique.SA, result.get(0).getCategorieJuridique());
    }

    @Test
    public void testFindByCategorieJuridiqueWithEtablissements() {
        // Créer une unité légale
        UniteLegale uniteLegale1 = new UniteLegale();
        uniteLegale1.setSiren("123456789");
        uniteLegale1.setDenomination("Test Company 1");
        uniteLegale1.setCategorieJuridique(CategorieJuridique.SA);
        uniteLegaleRepository.save(uniteLegale1);

        // Créer un établissement associé à l'unité légale
        Etablissement etablissement1 = new Etablissement();
        etablissement1.setSiret("12345678900001");
        etablissement1.setNic("00001");
        etablissement1.setAdresse("1 Rue de Test");
        etablissement1.setUniteLegale(uniteLegale1);
        etablissementRepository.save(etablissement1);

        // Créer une deuxième unité légale
        UniteLegale uniteLegale2 = new UniteLegale();
        uniteLegale2.setSiren("987654321");
        uniteLegale2.setDenomination("Test Company 2");
        uniteLegale2.setCategorieJuridique(CategorieJuridique.SA);
        uniteLegaleRepository.save(uniteLegale2);

        // Créer un établissement associé à la deuxième unité légale
        Etablissement etablissement2 = new Etablissement();
        etablissement2.setSiret("98765432100001");
        etablissement2.setNic("00001");
        etablissement2.setAdresse("2 Rue de Test");
        etablissement2.setUniteLegale(uniteLegale2);
        etablissementRepository.save(etablissement2);

        // Tester la méthode
        List<UniteLegale> result = uniteLegaleRepository.findByCategorieJuridiqueWithEtablissement(CategorieJuridique.SA);
        assertEquals(2, result.size());

        // Vérifier que les établissements sont instanciés et non vides
        assertNotNull(result.get(0).getEtablissements());
        assertNotNull(result.get(1).getEtablissements());
        assertEquals(1, result.get(0).getEtablissements().size());
        assertEquals(1, result.get(1).getEtablissements().size());
    }
}
```

### 5.2. Test `EtablissementRepositoryTest`
Créez la classe de test `EtablissementRepositoryTest` dans le package `fr.insee.formation.repository` :
```java
@DataJpaTest
@ActiveProfiles("dev")
public class EtablissementRepositoryTest {

    @Autowired
    private EtablissementRepository etablissementRepository;

    @Autowired
    private UniteLegaleRepository uniteLegaleRepository;

    @Test
    public void testFindBySiret() {
        UniteLegale uniteLegale = new UniteLegale();
        uniteLegale.setSiren("123456789");
        uniteLegale.setDenomination("Test Company");
        uniteLegale.setCategorieJuridique(CategorieJuridique.SA);
        uniteLegaleRepository.save(uniteLegale);

        Etablissement etablissement = new Etablissement();
        etablissement.setSiret("12345678900001");
        etablissement.setNic("00001");
        etablissement.setAdresse("1 Rue de Test");
        etablissement.setUniteLegale(uniteLegale);
        etablissementRepository.save(etablissement);

        Optional<Etablissement> found = etablissementRepository.findBySiret("12345678900001");
        assertTrue(found.isPresent());
        assertEquals("1 Rue de Test", found.get().getAdresse());
    }

    @Test
    public void testFindByUniteLegale() {
        UniteLegale uniteLegale = new UniteLegale();
        uniteLegale.setSiren("123456789");
        uniteLegale.setDenomination("Test Company");
        uniteLegale.setCategorieJuridique(CategorieJuridique.SA);
        uniteLegaleRepository.save(uniteLegale);

        Etablissement etablissement1 = new Etablissement();
        etablissement1.setSiret("12345678900001");
        etablissement1.setNic("00001");
        etablissement1.setAdresse("1 Rue de Test");
        etablissement1.setUniteLegale(uniteLegale);
        etablissementRepository.save(etablissement1);

        Etablissement etablissement2 = new Etablissement();
        etablissement2.setSiret("12345678900002");
        etablissement2.setNic("00002");
        etablissement2.setAdresse("2 Rue de Test");
        etablissement2.setUniteLegale(uniteLegale);
        etablissementRepository.save(etablissement2);

        List<EtablissementProjection> result = etablissementRepository.findByUniteLegale(uniteLegale);
        assertEquals(2, result.size());
    }

    @Test
    public void testFindByAdresseStartingWith() {
        UniteLegale uniteLegale = new UniteLegale();
        uniteLegale.setSiren("123456789");
        uniteLegale.setDenomination("Test Company");
        uniteLegale.setCategorieJuridique(CategorieJuridique.SA);
        uniteLegaleRepository.save(uniteLegale);

        Etablissement etablissement1 = new Etablissement();
        etablissement1.setSiret("12345678900001");
        etablissement1.setNic("00001");
        etablissement1.setAdresse("1 Rue de Test");
        etablissement1.setUniteLegale(uniteLegale);
        etablissementRepository.save(etablissement1);

        Etablissement etablissement2 = new Etablissement();
        etablissement2.setSiret("12345678900002");
        etablissement2.setNic("00002");
        etablissement2.setAdresse("2 Rue de Test");
        etablissement2.setUniteLegale(uniteLegale);
        etablissementRepository.save(etablissement2);

        List<Etablissement> result = etablissementRepository.findByAdresseStartingWith("1 Rue");
        assertEquals(1, result.size());
        assertEquals("1 Rue de Test", result.get(0).getAdresse());
    }

    @Test
    public void testFindEtablissementByUniteLegaleProjection() {
        UniteLegale uniteLegale = new UniteLegale();
        uniteLegale.setSiren("123456789");
        uniteLegale.setDenomination("Test Company");
        uniteLegale.setCategorieJuridique(CategorieJuridique.SA);
        uniteLegaleRepository.save(uniteLegale);

        Etablissement etablissement1 = new Etablissement();
        etablissement1.setSiret("12345678900001");
        etablissement1.setNic("00001");
        etablissement1.setAdresse("1 Rue de Test");
        etablissement1.setUniteLegale(uniteLegale);
        etablissementRepository.save(etablissement1);

        Etablissement etablissement2 = new Etablissement();
        etablissement2.setSiret("12345678900002");
        etablissement2.setNic("00002");
        etablissement2.setAdresse("2 Rue de Test");
        etablissement2.setUniteLegale(uniteLegale);
        etablissementRepository.save(etablissement2);

        List<EtablissementProjection> result = etablissementRepository.findByUniteLegale(uniteLegale);
        assertEquals(2, result.size());
        assertEquals("12345678900001", result.get(0).getSiret());
        assertEquals("1 Rue de Test", result.get(0).getAdresse());
    }
}
```

---

## Partie 6 : Exécution et validation

### 6.1. Lancer les tests
Exécutez les tests unitaires pour vérifier que les repositories fonctionnent correctement avec la base de données H2 :
```bash
mvn test
```

### 6.2. Tester avec PostgreSQL

#### 6.2.1. Créer un conteneur PostgreSQL avec Podman
Avant de lancer l'application avec le profil `integration`, créez un conteneur PostgreSQL avec Podman :

0. **Démarrer votre machine podman** :
   ```bash
   podman machine start
   ```

1. **Créer un conteneur PostgreSQL** :
   ```bash
   podman run --name sirene_db -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=password -e POSTGRES_DB=sirene_db -p 5432:5432 -d postgres:latest
   ```

2. **Vérifier que le conteneur est en cours d'exécution** :
   ```bash
   podman ps
   ```
   Vous devriez voir une ligne similaire à :
   ```
   CONTAINER ID  IMAGE                           COMMAND               CREATED        STATUS            PORTS                   NAMES
   a1b2c3d4e5f6  docker.io/library/postgres:latest  postgres -c ...      2 minutes ago  Up 2 minutes ago  0.0.0.0:5432->5432/tcp  sirene_db
   ```

#### 6.2.2. Vérifier la connexion avec DBeaver
1. **Ouvrir DBeaver (ou autre outil de gestion de base de données)** et créer une nouvelle connexion PostgreSQL.
2. **Configurer la connexion** avec les paramètres suivants :
   - **Hôte** : `localhost`
   - **Port** : `5432`
   - **Base de données** : `sirene_db`
   - **Utilisateur** : `postgres`
   - **Mot de passe** : `password`
3. **Tester la connexion** en cliquant sur le bouton "Tester la connexion".
4. **Exécuter le script d'initialisation** pour créer les tables et insérer des données de test.
Executez le script suivant dans DBeaver pour initialiser la base de données PostgreSQL :
```sql
-- Script d'initialisation pour la base de données PostgreSQL
-- Ce script crée les tables et insère des données de test

-- Création des tables
CREATE TABLE IF NOT EXISTS unite_legale (
    id SERIAL PRIMARY KEY,
    siren VARCHAR(9) NOT NULL UNIQUE,
    denomination VARCHAR(255) NOT NULL,
    categorie_juridique VARCHAR(20) NOT NULL
);

CREATE TABLE IF NOT EXISTS etablissement (
    id SERIAL PRIMARY KEY,
    siret VARCHAR(14) NOT NULL UNIQUE,
    nic VARCHAR(5) NOT NULL,
    adresse VARCHAR(255) NOT NULL,
    unite_legale_id INTEGER NOT NULL,
    FOREIGN KEY (unite_legale_id) REFERENCES unite_legale(id) ON DELETE CASCADE
);

-- Insertion de données de test pour unite_legale
INSERT INTO unite_legale (siren, denomination, categorie_juridique) VALUES
('123456789', 'Test Company 1', 'SA'),
('987654321', 'Test Company 2', 'SARL'),
('456123789', 'Test Company 3', 'SAS'),
('789321654', 'Test Company 4', 'EI'),
('321654987', 'Test Company 5', 'EURL');

-- Insertion de données de test pour etablissement
INSERT INTO etablissement (siret, nic, adresse, unite_legale_id) VALUES
('12345678900001', '00001', '1 Rue de Test', 1),
('12345678900002', '00002', '2 Rue de Test', 1),
('98765432100001', '00001', '3 Rue de Test', 2),
('45612378900001', '00001', '4 Rue de Test', 3),
('78932165400001', '00001', '5 Rue de Test', 4),
('32165498700001', '00001', '6 Rue de Test', 5);

```
4. **Explorer les tables** pour vérifier que les données ont été correctement insérées.

#### 6.2.3. Lancer l'application avec le profil `integration`
Une fois le conteneur PostgreSQL en cours d'exécution et les données initialisées, lancez l'application avec le profil `integration` :
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=integration
```

#### 6.2.4. Vérifier la connexion à la base de données avec Actuator
Une fois l'application démarrée, utilisez l'endpoint Actuator pour vérifier que la base de données est correctement configurée.
Ouvrez l'url http://localhost:8080/actuator/health dans votre navigateur ou via la commande curl :
```bash
curl http://localhost:8080/actuator/health
```

Vous devriez recevoir une réponse similaire à :
```json
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP",
      "details": {
        "database": "PostgreSQL",
        "result": 1,
        "validationQuery": "SELECT 1",
        "result": 1
      }
    },
    "diskSpace": {
      "status": "UP",
      "details": {
        "total": 250685575168,
        "free": 123456789012,
        "threshold": 10485760,
        ...
      }
    },
    "ping": {
      "status": "UP"
    }
  }
}
```

Si vous rencontrez des erreurs, vérifiez les logs de l'application et assurez-vous que le conteneur PostgreSQL est en cours d'exécution et accessible.

---
## Partie 7 : Endpoint de vérification de la base de données

### 7.1. Créer un endpoint pour vérifier la base de données
Créez un contrôleur `DatabaseCheckController` dans le package `fr.insee.formation.controller` :
```java
@RestController
public class DatabaseCheckController {

    private UniteLegaleRepository uniteLegaleRepository;

    private EtablissementRepository etablissementRepository;

    public DatabaseCheckController(UniteLegaleRepository uniteLegaleRepository, EtablissementRepository etablissementRepository){
        this.uniteLegaleRepository = uniteLegaleRepository;
        this.etablissementRepository = etablissementRepository;
    }

    @GetMapping("/check-database")
    public String checkDatabase() {
        long uniteLegaleCount = uniteLegaleRepository.count();
        long etablissementCount = etablissementRepository.count();

        if (uniteLegaleCount == 5 && etablissementCount == 6) {
            return "Ok ! On a bien 5 unités légales et 6 établissements.";
        } else {
            return "Erreur : Nombre de lignes incorrect. UniteLegale: " + uniteLegaleCount + ", Etablissement: " + etablissementCount;
        }
    }
}
```

### 7.2. Tester l'endpoint manuellement
Pour vérifier que l'endpoint fonctionne correctement, visitez http://localhost:8080/check-database (via navigateur ou curl). Vous devriez recevoir la réponse suivante :

```
Ok ! On a bien 5 unités légales et 6 établissements.
```

Si vous avez bien ajouté les spring-dev-tools dans votre pom.xml, il n'est pas nécessaire de redémarrer l'application.

---

## Résumé du TP4
À la fin de ce TP, vous devez :
- Configurer un projet Spring Boot avec Spring Data JPA.
- Implémenter des repositories avec des méthodes personnalisées.
- Utiliser des projections DTO pour optimiser les requêtes.
- Configurer et tester avec différentes bases de données (H2 et PostgreSQL).
- Vérifier la connexion à la base de données avec l'Actuator.
- Ajouter un endpoint pour vérifier le contenu de la base de données.

---

### **Proxy d'entreprise**
Si vous rencontrez des erreurs "Cannot Connect" ou "Host Not Resolvable" lors de requêtes curl vers `localhost`, utilisez `curl --noproxy localhost <url>` pour contourner le proxy d'entreprise. Vous pouvez aussi définir `export no_proxy=localhost,127.0.0.1` de manière permanente dans votre profil bash.
