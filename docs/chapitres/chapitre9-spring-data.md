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
    
    @Autowired
    private UserRepository userRepository;
      
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

--

## Convention de nommage

* Les méthodes implémentées par convention de nommage doivent respecter le pattern suivant :
    ```java
    findBy + <NomDuChamp> + [Opérateur] + [Condition(s)]
    ``` 
* On peut aussi au besoin remplacer `findBy` par :
  * `countBy` : renvoie un long
  * `existsBy`: renvoie un booléen
  * `deleteBy`: supprime selon critère

--

## Convention de nommage

- La méthode suivante permettra de récupérer la liste des `User` en filtrant sur l'attribut `name` (qui doit impérativement exister dans l'`Entity`) :
    ```java
    List<User> findByName(String name);
    ``` 
- On peut filtrer sur des attributs de type objet, JPA/Hibernate utiliseront le mapping pour déterminer les conditions à ajouter :
    ```java
    List<User> findByCountry(Country country);
    //l'id de l'objet country doit alors être renseigné
    ``` 


--

## Convention de nommage

- On pourra filtrer sur plusieurs champs par exemple ici sur l'attribut `name` et `email` :
    ```java
    List<User> findByNameAndEmail(String name, String email);
    ``` 
- Ou encore pour utiliser la disjonction (OU) :
    ```java
    List<User> findByNameOrEmail(String name, String email);
    ``` 

- On peut aussi passer une collection de valeurs :
    ```java
    List<User> findByNameIn(List<String> names);
    ``` 

--

## Les opérateurs

- On pourra ajouter des conditions plus élaborées en utilisant les opérateurs proposés. Par ex. pour générer une condition SQL de type `LIKE` : 
    ```java
    List<User> findByNameLike(String pattern);
    ``` 
- Ou encore pour vérifier qu'une chaîne de caractère commence par une autre :
    ```java
    List<User> findByNameStartingWith(String start);
    ``` 

--

## Les tris et limites

- On pourra appliquer un tri sur notre requête (portant sur n'importe quel champ) avec :
    ```java
    List<User> findByNameOrderByNameAsc(String start);
    ``` 
- On pourra aussi choisir de ne récupérer que les N premiers ou derniers résultats :
    ```java
    //ici N = 0
    User findFirstByNameOrderByNameDesc(String start);
    ```
    ```java
    //ici N = 3
    List<User> findTop3ByNameOrderByNameDesc(String start);
    ``` 

--

## Les tris et limites

- Enfin pour rendre la limite paramétrable, on pourra utiliser la classe `org.springframework.data.domain.Limit`:
    ```java
    List<User> findByNameOrderByNameAsc(Limit limit);
    ``` 
- Pour des besoin plus poussés, on utilisera les `Pageable` et les `Sort`...

--

## Les Pageable et les Sort

- On a souvent besoin de récupérer les résultats par paquets de taille définie.

- Exemple classique : Tableau paginé avec tri possible sur les champs

- Pour répondre à ce besoin, Spring-Data propose le mécanisme des `Pageable`

--

## Les Pageable et les Sort

- L'interface `JPARepository` hérite de l'interface `PagingAndSortingRepository`

- Il est possible d'ajouter sur n'importe quelle méthode un paramètre de type `Pageable`, et/ou un paramètre de type `Sort`

- L'implémentation permettra alors de récupérer les résultats sous forme de `Page` permettant de les récupérer par paquets de taille définie lors de l'appel

--

## Exemple de Pageable

- Pour les méthodes présentes par défaut, on dispose d'emblée d'une méthode avec un attribut `Pageable` et/ou `Sort`

- Exemple (extrait de l'interface `PagingAndSortingRepository`):
    ```java 
    Page<T> findAll(Pageable pageable);
    ``` 

- Pour les méthodes ajoutées au repository, il faut ajouter une définition contenant l'attribut `Pageable` et/ou `Sort` si besoin. Ex:
    ```java
    List<User> findByNameOrderByNameAsc(Pageable pageable);
    ``` 

--

## Exemple de Pageable

- Utilisation :
    ```java
    Pageable firstPageWithTwoElements = PageRequest.of(0, 5);

    Page<User> pageUser = 
        userRepository.findByNameOrderByNameAsc(firstPageWithFiveElements);

    for(User user: pageUser) { 
        ... //On itère sur les 5 éléments constituants la 1ère page
    }

    //Constitution du pageable suivant
    Pageable secondPageWithTwoElements = 
        firstPageWithTwoElements.nextPageable();

    //Récupération des résultats suivants
    Page<User> secondPageUser = 
        userRepository.findByNameOrderByNameAsc(secondPageWithTwoElements);
    ...

    ``` 

--

## Exemple de Sort

- Pour les tris on peut utiliser l'objet `Sort` soit de manière indépendante, soit en l'ajoutant à l'objet `Pageable`

- Exemple de tri sans `Pageable`:
    ```java
    List<User> users = userRepository.findAll(Sort.by("email"));
    ``` 

- Exemple au sein d'un pageable :
    ```java
    Pageable pageableSortedByName = 
        PageRequest.of(0, 3, Sort.by("name"));
    ``` 

--

## Conclusion Page et Sort

- Les `Pageable` et les `Sort` fournissent des outils très souples pour gérer la pagination et les tris

- Permettent de s'adapter au besoin selon le contexte sans avoir à réécrire des méthodes au sein des `Repository`

- Attention les requêtes sont réexécutées à chaque nouvelle page !

- Pour le parcours d'un grand nombre d'objets, on considerera l'utilisation des `Stream`...

--

## Le choix du type retour

- Spring-Data peut adapter l'implémentation au type retour choisi !

- Par exemple, on peut choisir de renvoyer un `Optional` :
    ```java
    Optional<User> findByEmail(String email);
    ```

- On pourra alors utiliser les méthodes `isPresent` pour s'assurer de la non-nullité

- Attention si plusieurs utilisateurs ont le même mail on aura une 
    <span style="color:red">IncorrectResultSizeDataAccessException</span>

--

## Le type Stream

- De même pour les méthodes renvoyant plusieurs objets, on pourra choisir le type parmi `List`, `Set`, `Collection` ou `Stream`

- Un cas particulièrement intéressant est celui du type `Stream`

- Avec ce type retour, Spring-Data nous renverra les données sous forme d'un flux...

--

## Le type Stream

- Mais surtout en arrière plan, il utilisera si la BDD le permet un curseur (Postgres ✅)

- La requête est alors exécuté une seule fois ➡️ gain de performance /à la pagination

- Les résultats sont conservés côté serveur et transmis par paquets ➡️ pas de lenteurs réseau

- Pour itérer sur un grand nombre d'objets sans besoin de pagination, c'est de loin la méthode la plus performante !

--

## Le type Stream : exemple

- Définition de la méthode renvoyant un `Stream` :
    ```java
    Stream<User> findByCountry(Country country);
    ``` 
- Utilisation :
    ```java
    Country countryFrance = countryRepository.findByName("France");
    userRepository.findByCountry(countryFrance)
        .map(user ->    "L'utilisateur " 
                        + user.getName() 
                        + " habite en France !")
        .forEach(string -> log.info(string));
    ``` 

--

## Le type Stream : exemple

- Attention : En cas d'appel d'une "opération terminale" sur le `Stream` (`forEach`, `collect`, `count`...), l'ensemble des objets sont collectés et le curseur est fermé !
    ```java
    Country countryFrance = countryRepository.findByName("France");
    userRepository.findByCountry(countryFrance) //Ouverture du curseur
        .map(user -> "L'utilisateur "
                     + user.getName()           //Curseur tjrs ouvert
                     + " habite en France !") 
        .forEach(string -> log.info(string));  //Fermeture du curseur
                                               //, récupération de toutes les lignes
    ``` 

--

## @Query

- Pour des requêtes assez complexes, l'implémentation par convention de nommage devient illisible ou impossible

- Spring-Data propose alors un mécanisme simple pour définir la requête JPQL à utiliser avec l'annotation `@Query`

- Exemple :
    ```java    
    @Query("SELECT u FROM User u WHERE u.country.name = :countryName")
    List<User> findByCountryName(
        @Param("countryName") String countryName);
    ```

--

## @Query

- On pourra typiquement utiliser ce mécanisme pour utiliser les jointure de type `FETCH` permettant d'éviter le problème du `SELECT N+1` :
    ```java    
    @Query("SELECT u FROM User u 
                JOIN FETCH u.country country")
    List<User> fetchAllUsersWithCountry();
    ```

- On pourra ajouter aussi des configurations plus fines avec `@QueryHint` (ex. activation cache):

    ```java
    @Query("SELECT u FROM User u WHERE u.name = :name")
    @QueryHints({
        @QueryHint(name = "org.hibernate.cacheable", value = "true"),
    })
    List<User> findByNameCached(@Param("name") String name);
    ```
--

## Multiples datasources

- Dans le cas de **plusieurs datasources**, l'Autoconfiguration ne suffit plus

- Il faut configurer manuellement les beans nécessaires à JPA/Hibernate, à savoir :
    1. Les DataSources
    2. L'EntityManager
    3. Le TransactionManager
    4. Marquer une datasource comme **@Primary**

--

## Datasource principale

- On pourra utiliser l'annotation `@ConfigurationProperties` pour confiurer les beans `DataSource`:

```java
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
    basePackages = "com.example.user.repository",
    entityManagerFactoryRef = "userEntityManagerFactory",
    transactionManagerRef = "userTransactionManager"
)
public class DataSourceUserConfig {
    
    // ===== DATASOURCE PRINCIPALE =====
    
    @Primary //l'annotation `@Primary` rend les beans prioritaires lors de l'injection de dépendances ! Attention au conflit
    @Bean(name = "userDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.user")
    public DataSource userDataSource() {
        return DataSourceBuilder.create().build();
    }
    
    @Primary //l'annotation `@Primary` rend les beans prioritaires lors de l'injection de dépendances ! Attention au conflit
    @Bean(name = "userEntityManager")
    public LocalContainerEntityManagerFactoryBean userEntityManager(
            @Qualifier("userDataSource") DataSource dataSource) {
        
        LocalContainerEntityManagerFactoryBean em = 
            new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource);
        em.setPackagesToScan("fr.insee.formation.entities.user");
        em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        return em;
    }
    
    @Primary //l'annotation `@Primary` rend les beans prioritaires lors de l'injection de dépendances ! Attention au conflit
    @Bean(name = "userTransactionManager")
    public PlatformTransactionManager userTransactionManager(
            @Qualifier("userEntityManager") 
            LocalContainerEntityManagerFactoryBean entityManager) {
        return new JpaTransactionManager(entityManager.getObject());
    }
    
}
```

--
## Datasource secondaire


```java
@Configuration
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
    basePackages = "com.example.order.repository",
    entityManagerFactoryRef = "userEntityManagerFactory",
    transactionManagerRef = "userTransactionManager"
)
public class DataSourceOrderConfig {

    // ===== DATASOURCE SECONDAIRE =====
    
    @Bean(name = "orderDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.order")
    public DataSource orderDataSource() {
        return DataSourceBuilder.create().build();
    }
    
    @Bean(name = "orderEntityManager")
    public LocalContainerEntityManagerFactoryBean orderEntityManager(
            @Qualifier("orderDataSource") DataSource dataSource) {
        
        LocalContainerEntityManagerFactoryBean em = 
            new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource);
        em.setPackagesToScan("fr.insee.formation.entities.order");
        em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        return em;
    }
    
    @Bean(name = "orderTransactionManager")
    public PlatformTransactionManager orderTransactionManager(
            @Qualifier("orderEntityManager") 
            LocalContainerEntityManagerFactoryBean entityManager) {
        return new JpaTransactionManager(entityManager.getObject());
    }
}
```

--

## Les properties

- Dans **application.properties** on a :

```properties
# DATASOURCE PRINCIPALE
spring.datasource.user.url=jdbc:mysql://localhost:3306/user_db
spring.datasource.user.username=root
spring.datasource.user.password=password
spring.datasource.user.driver-class-name=com.mysql.cj.jdbc.Driver

# DATASOURCE SECONDAIRE
spring.datasource.order.url=jdbc:mysql://localhost:3306/order_db
spring.datasource.order.username=root
spring.datasource.order.password=password
spring.datasource.order.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA/Hibernate configuration globale
spring.jpa.hibernate.ddl-auto=update
...
```

--
### Les repositories

```java
// Pour la datasource PRINCIPALE
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}

// Pour la datasource SECONDAIRE
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
}
```

--

## 5. Bonnes pratiques

1. **Utiliser @Primary** : Obligatoire avec plusieurs datasources pour indiquer la source par défaut ➡️ Attention lors de l'injection à éviter les confusion avec `@Qualifier`
2. **Structurer les packages** : Organiser les entités et les repositories par datasource
3. **Nommer clairement** : Utiliser des noms explicites pour les beans (`primaryDataSource`, `secondaryDataSource`)