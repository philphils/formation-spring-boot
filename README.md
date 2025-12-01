### TP3 : Profiles

**Objectif** : Maîtriser les profiles en créant plusieurs configurations, testant différentes méthodes d'activation et créant des beans conditionnels. Vérifier aussi quel profile est actif via l'Actuator.

#### Partie 1 : Créer les fichiers de configuration par profile

1. **Créer `src/main/resources/application-dev.properties`** :
   ```properties
   app.environment=DEVELOPMENT
   logging.level.root=DEBUG
   logging.level.fr.insee.formation=DEBUG
   
   spring.datasource.url=jdbc:h2:mem:devdb
   spring.datasource.username=sa
   spring.datasource.password=
   spring.h2.console.enabled=true

   management.endpoint.env.show-values=ALWAYS
   ```

2. **Créer `src/main/resources/application-integration.properties`** :
   ```properties
   app.environment=INTEGRATION
   logging.level.root=WARN
   logging.level.fr.insee.formation=INFO
   
   spring.datasource.url=jdbc:h2:file:./data/integrationdb;MODE=MySQL
   spring.datasource.username=user_integration
   spring.datasource.password=pwd_integration
   spring.h2.console.enabled=false
   ```

3. **Compléter `src/main/resources/application.properties`** (profil par défaut) :
   ```properties
   app.name=Formation Spring Boot
   app.version=1.0.0
   app.environment=DEFAULT
   
   logging.level.root=WARN
   ```

#### Partie 2 : Créer des beans liés aux profiles

1. **Créer une interface de service** :
   ```java
   package fr.insee.formation.service;

   public interface DataSourceService {
       String getInfo();
   }
   ```

2. **Implémenter le service pour le profil `dev`** :
   ```java
   package fr.insee.formation.service.impl;

   import org.springframework.context.annotation.Profile;
   import org.springframework.stereotype.Service;

   import fr.insee.formation.service.DataSourceService;

   @Service
   @Profile("dev")
   public class DevDataSourceService implements DataSourceService {
       @Override
       public String getInfo() {
           return "Using DEVELOPMENT in-memory H2 database (dev profile)";
       }
   }
   ```

3. **Implémenter le service pour le profil `integration`** :
   ```java
   package fr.insee.formation.service.impl;

   import org.springframework.context.annotation.Profile;
   import org.springframework.stereotype.Service;

   import fr.insee.formation.service.DataSourceService;

   @Service
   @Profile("integration")
   public class IntegrationDataSourceService implements DataSourceService {
       @Override
       public String getInfo() {
         return "Using INTEGRATION H2 database with file (integration profile)";
       }
   }
   ```

4. **Implémenter le service par défaut** (quand aucun profil spécifique n'est actif) :
   ```java
   package fr.insee.formation.service.impl;

   import org.springframework.context.annotation.Profile;
   import org.springframework.stereotype.Service;

   import fr.insee.formation.service.DataSourceService;

   @Service
   @Profile("!dev & !integration")
   public class DefaultDataSourceService implements DataSourceService {
       @Override
       public String getInfo() {
          return "Using DEFAULT : no specific database configuration (default profile)";
       }
   }
   ```

#### Partie 3 : Créer des endpoints pour tester les profiles

1. **Créer `EnvironmentController`** pour afficher l'environnement actif :
   ```java
   package fr.insee.formation.controller;

   import org.springframework.beans.factory.annotation.Value;
   import org.springframework.web.bind.annotation.GetMapping;
   import org.springframework.web.bind.annotation.RestController;
   import java.util.HashMap;
   import java.util.Map;

   @RestController
   public class EnvironmentController {
       @Value("${app.environment}")
       private String environment;
   
       @Value("${spring.datasource.url}")
       private String dbUrl;
   
       @GetMapping("/environment")
       public Map<String, Object> getEnvironment() {
           Map<String, Object> result = new HashMap<>();
           result.put("environment", environment);
           result.put("databaseUrl", dbUrl);
           return result;
       }
   }
   ```

2. **Créer `DataSourceController`** pour vérifier quel bean de service est actif :
   ```java
   package fr.insee.formation.controller;

   import org.springframework.beans.factory.annotation.Autowired;
   import org.springframework.web.bind.annotation.GetMapping;
   import org.springframework.web.bind.annotation.RestController;
   import fr.insee.formation.service.DataSourceService;
   import java.util.HashMap;
   import java.util.Map;

   @RestController
   public class DataSourceController {
       @Autowired(required = false)
       private DataSourceService dataSourceService;

       @GetMapping("/datasource/info")
       public Map<String, Object> getDataSourceInfo() {
           Map<String, Object> result = new HashMap<>();
           if (dataSourceService != null) {
               result.put("status", "FOUND");
               result.put("service_class", dataSourceService.getClass().getSimpleName());
               result.put("message", dataSourceService.getInfo());
           } else {
               result.put("status", "NOT_FOUND");
               result.put("message", "Aucun DataSourceService disponible pour ce profil");
           }
           return result;
       }
   }
   ```

#### Partie 4 : Tester les différentes méthodes d'activation de profiles

- **Attention :** Certaines valeurs affichées par l'Actuator sont masqués par défaut par Spring-Boot. C'est pourquoi en dev cette ligne a été ajoutée au fichier de properties : `management.endpoint.env.show-values=ALWAYS`

1. **Méthode A : Via `application.properties`** :
   - Modifier `src/main/resources/application.properties` et ajouter :
     ```properties
     spring.profiles.active=dev
     ```
   - Redémarrer l'application et tester en affichant :
     ```bash
     http://localhost:8080/environment
     http://localhost:8080/datasource/info
     http://localhost:8080/actuator/env
     ```
   - Vérifier que `"environment":"DEVELOPMENT"` s'affiche
   - Avec http://localhost:8080/actuator/beans vérifier que les beans `datasource` liés au profil sont bien créés
   - Changer en `spring.profiles.active=integration` et redémarrer
   - Vérifier le changement

2. **Méthode B : Via argument de lancement** :
   - Supprimer ou commenter `spring.profiles.active` dans `application.properties`
   - Lancer l'app avec le profil en argument :
     ```bash
     mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"
     ```
   - Redémarrer l'application et tester en affichant :
     ```bash
     http://localhost:8080/environment
     http://localhost:8080/datasource/info
     http://localhost:8080/actuator/env
     ```
   - Avec http://localhost:8080/actuator/beans vérifier que les beans `datasource` liés au profil sont bien créés
   - Relancer avec `integration` au lieu de `dev`

3. **Méthode C : Via variable d'environnement** :
   - Définir la variable et lancer l'app :
     ```bash
     export SPRING_PROFILES_ACTIVE=integration
     mvn spring-boot:run
     ```
   - Tester les endpoints
   - Changer le profil en `dev` et retester
   - Désaffecter la variable avec :
     ```bash
     unset SPRING_PROFILES_ACTIVE
     ```

4. **Méthode D : Via VS Code Spring Boot Dashboard** :
   - Ouvrir le Spring Boot Dashboard (icône Spring dans la barre latérale)
   - Clic-droit sur la classe main → `Run with profile`
   - Sélectionner le profil `dev` ou `integration`
   - Tester les endpoints : `/environment` et `/datasource/info`
   - Observer les logs et vérifier les profiles actifs via `/actuator/env`

#### Résumé du TP3

À la fin de ce TP, vous devez :
- Créer des fichiers de configuration par profile (`application-dev.properties`, `application-integration.properties`)
- Comprendre comment créer plusieurs configurations par profil
- Savoir activer les profiles de 4 manières différentes
- Comprendre l'annotation `@Profile` et son impact sur la création des beans
- Vérifier que les beans liés aux profiles ne sont créés que si le profil est actif
- Utiliser l'Actuator et le Spring Boot Dashboard pour observer et diagnostiquer les profiles actifs

---

- **Proxy d'entreprise** : Si vous rencontrez des erreurs "Cannot Connect" ou "Host Not Resolvable" lors de requêtes curl vers `localhost`, utilisez `curl --noproxy localhost <url>` pour contourner le proxy d'entreprise. Vous pouvez aussi définir `export no_proxy=localhost,127.0.0.1` de manière permanente dans votre profil bash.

