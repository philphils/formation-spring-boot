# Spring-Data

## Requêtes complexes, datasources multiples et DTO

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

--

## Projections DTO

- Spring Data JPA permet de sélectionner uniquement les champs nécessaires d'une entité, plutôt que de charger l'entité complète.
- Cela améliore les performances et réduit la consommation de mémoire, surtout lorsque vous n'avez besoin que d'un sous-ensemble des données.
- Efficace en particulier pour des tables avec de nombreuses colonnes

--

## Projections basées sur des interfaces

- Vous pouvez définir une interface avec les méthodes `getter` des champs que vous souhaitez récupérer.
- Spring Data JPA génère automatiquement l'implémentation de cette interface lors de l'exécution de la requête.

--

### Exemple

- Définition de l'interface :

    ```java
    public interface UserNameAndEmailProjection {
        String getName();
        String getEmail();
    }
    ```

- Utilisation dans le repository :

    ```java
    @Repository
    public interface UserRepository extends JpaRepository<User, Long> {
        List<UserNameAndEmailProjection> findByCountry(Country country);
    }
    ```

--

## Projections basées sur des classes DTO

- Vous pouvez définir une classe DTO avec les champs nécessaires.
- Utilisez l'annotation `@Query` pour spécifier les champs à récupérer.

--

### Exemple classe DTO

```java
public class UserNameAndEmailDTO {
    private String name;
    private String email;

    // Constructeur, getters et setters
    public UserNameAndEmailDTO(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }
}
```

--

## Utilisation dans le repository :

```java
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT 
        new com.example.UserNameAndEmailDTO(u.name, u.email)
        FROM User u WHERE u.country = :country")
    List<UserNameAndEmailDTO> findByCountry(
                @Param("country") Country country);
}
```

--

## Avantages des projections DTO

- **Performance** : Seuls les champs nécessaires sont chargés depuis la base de données.
- **Réduction de la consommation mémoire** : Moins de données sont transférées et stockées en mémoire.
- **Flexibilité** : Vous pouvez adapter les données retournées en fonction des besoins de votre application.
- **Sécurité** : Réduit les risques de fuites de données sensibles en ne retournant que les champs nécessaires.


--

# TP4 :  

Spring-Data 

![](./img/diapo_formation_spring_boot_12.png)