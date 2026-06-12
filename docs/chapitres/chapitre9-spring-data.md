# Spring Data JPA

--

## Une sur-couche bien pratique

- Spring Data JPA : abstraction de haut niveau qui simplifie l'accès aux données avec JPA (Java Persistence API).
- Permet de réduire drastiquement le code boilerplate en générant automatiquement les implémentations des méthodes de base.
- Configure les composants utiles à JPA automatiquement à partir du `pom.xml` et des properties

--

## Spring-Data : Mise en place

- Pour utiliser Spring Data JPA, ajoutez la dépendance suivante au `pom.xml` :

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
```

--

## Dépendances

- Cette dépendance inclut :
  - **Hibernate** : implémentation JPA
  - **Spring Data JPA** : couche d'abstraction
  - Le driver JDBC approprié à la BDD (détecté d'après les dépendances ou l'url de connexion)

--

## Autoconfiguration

- Quand vous ajoutez cette dépendance, **Spring Boot configure automatiquement** :

    - **EntityManager** : gestion du contexte de persistance
    - **PlatformTransactionManager** : gestion des transactions
    - **DataSource** : connexion à la base de données
    - **JPA & Hibernate** : configuration par défaut

--

## Condition d'activation :
- Présence de la classe `JpaRepository` sur le classpath (via la dépendance)
- Configuration d'une datasource valide. Ex: 

    ```properties
    # Datasource
    spring.datasource.url=jdbc:mysql://localhost:3306/mydb
    spring.datasource.username=root
    spring.datasource.password=password
    spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
    ```
- Le `driver-class-name` peut être précisé mais il est renseigné automatiquement d'après l'URL sinon
--

## Autres properties JPA

- Les autres properties JPA/Hibernate sont alors configurables aussi. Ex:

```properties
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.use_sql_comments=true

# Logging
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE
```

--

## Propriétés JPA clés :

- Les propriétés les plus utiles sont :
    - `spring.jpa.hibernate.ddl-auto` : pour modifier le schéma au démérrage de l'application (valeurs `validate`, `update`, `create`, `create-drop`)
    - `spring.jpa.show-sql` : afficher les requêtes SQL
    - `spring.jpa.format-sql` : formatter les requêtes
    - `spring.jpa.properties.hibernate.*` : configuration fine d'Hibernate (fetchSize, batchSize...)
- De nombreuses properties accessibles avec l'auto-complétion (avec les extensions Spring)

--
## Utiliser Spring Data JPA

- Rappel : JPA permet de définir des classes Java à l'image des données stockées en BDD
- Une fois la configuration des Entités réalisées, JPA offrent de nombreux outils pour accéder aux données (cf. formation [Hibernate niveau 1](https://github.com/philphils/formation-hibernate-basique) )
- Spring-Data simplifie encore l'accès aux données en générant l'implémentation des méthodes pour y accéder

--

## Exemple : Entité User

```java
@Entity
@Table(name = "users")
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(unique = true)
    private String email;

    @ManyToOne
    private Country country;

}
```

--

## Création des repositories

- Avec Spring-Data et une fois les `Entity` créées, on peut créer des `Repository` qui permettront d'accéder aux données
- Les `Repository` sont des interfaces héritant de `JpaRepository` (ou classes dérivées)
- Spring-Data génère alors lui-même l'implémentation des méthodes y sont déclarées

--

## Exemple de repository

```java
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // Méthodes générées automatiquement
    // findAll(), findById(), save(), delete(), etc.
    
    // Méthodes personnalisées
    List<User> findByName(String name);
    Optional<User> findByEmail(String email);
}
```

--

## Utiliser le repository dans un service

- On pourra ensuite simplement injecter le repository pour utiliser ses méthodes :

```java
@Service
public class UserService {

    private UserRepository userRepository;

    public UserService(UserRepository userRepository){
        //Injection par constructeur gérée par Spring
        this.userRepository = userRepository;
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException("User not found"));
    }
       
    public List<User> getUsersByDepartment(String department) {
        return userRepository.findByDepartment(department);
    }
 
 ...

}
```

--

## Implémentation automatique

- Spring Data JPA génère automatiquement :
  - `save()`, `saveAll()`, `delete()`, `deleteAll()`
  - `findById()`, `findAll()`, `findAllById()`
  - `count()`, `exists()`
- D'autres méthodes peuvent être implémentées automatiquement si elles respectent les conventions de nommage
- Lorsque les convention de nommage ne suffisent pas, on définira les requêtes avec `@Query` ou encore manuellement (avec `Criteria` par ex.)