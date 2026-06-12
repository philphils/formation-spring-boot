### TP2 : Monitorer avec Spring Boot Actuator (optionnel)

**Objectif** : Mettre en place l'Actuator pour monitorer et diagnostiquer l'application en temps réel.

**Étapes** :

1. **Ajouter la dépendance Actuator** :
   - Ajouter dans `pom.xml` :
     ```xml
     <dependency>
         <groupId>org.springframework.boot</groupId>
         <artifactId>spring-boot-starter-actuator</artifactId>
     </dependency>
     ```

2. **Configurer les endpoints Actuator** :
   - Dans `src/main/resources/application.properties`, ajouter :
     ```properties
     management.endpoints.web.exposure.include=health,info,env,metrics,beans,loggers
     management.endpoint.health.show-details=always
     ```

3. **Démarrer l'application** et explorer les endpoints Actuator :
   - Lister les endpoints disponibles : `http://localhost:8080/actuator`
   - Consulter la santé : `http://localhost:8080/actuator/health`
   - Voir les infos : `http://localhost:8080/actuator/info`
   - Explorer les metrics : `http://localhost:8080/actuator/metrics`
   - Lister les beans : `http://localhost:8080/actuator/beans`
   - Voir les variables d'environnement : `http://localhost:8080/actuator/env`

4. **Modifier les logs dynamiquement** :
   - Consulter le niveau de log courant : `http://localhost:8080/actuator/loggers`
   - Modifier le niveau pour un package :
     ```bash
     curl --noproxy localhost -X \
       POST http://localhost:8080/actuator/loggers/fr.insee.formation \
       -H "Content-Type: application/json" \
       -d '{"configuredLevel":"DEBUG"}'
     ```
     (Attention dans git bash on utilise l'option `--noproxy localhost` car les requêtes curl sont filtrées)
   - Observer les changements dans les logs de l'application en consultant `http://localhost:8080/actuator/loggers`

5. **Vérifier avec le Spring Boot Dashboard (VS Code)** :
   - Le Dashboard devrait afficher les endpoints Actuator
   - Cliquer sur les endpoints pour les consulter dans le navigateur
   - Observer les métriques JVM, les beans détectés, etc.
