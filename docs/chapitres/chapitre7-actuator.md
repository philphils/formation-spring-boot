# Spring Boot Actuator

--
# Qu'est-ce que l'Actuator ?

* Spring Boot Actuator est un module qui expose des informations et des métriques sur votre application en cours d'exécution.

* Permet de monitorer et de gérer l'application via des **endpoints HTTP** ou **JMX**.

* Utile pour : diagnostiquer les problèmes, collecter des métriques, vérifier la santé, consulter les logs et la configuration en temps réel.


--
# Endpoints disponibles

* **`/actuator/health`** — État de santé de l'application (base de données, disque, etc.).

* **`/actuator/metrics`** — Métriques : heap, CPU, nombre de requêtes HTTP, latence, etc.

* **`/actuator/env`** — Variables d'environnement et properties actives.

* **`/actuator/beans`** — Liste tous les beans Spring gérés par le conteneur.

--
# Endpoints disponibles

* **`/actuator/loggers`** — Permet de consulter et modifier les niveaux de log en temps réel.

* **`/actuator/info`** — Informations personnalisées sur l'application (version, description, etc.).

* **`/actuator/httptrace`** — Historique des dernières requêtes HTTP traitées.

--
# Configuration basique

* Ajouter la dépendance dans `pom.xml` :

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

* Dans `application.properties`, exposer les endpoints désirés :

```properties
management.endpoints.web.exposure.include=health,info,env,metrics,beans,loggers,httptrace
management.endpoint.health.show-details=always
```

* L'endpoint racine est accessible via `/actuator` qui liste tous les endpoints exposés.

--
# Endpoints : `/actuator/health`

* Affiche l'état de santé global de l'application.

* Composants courants vérifiés : base de données, disque, services externes.

* Réponse simplifiée (sans authentification) : `"status":"UP"` ou `"status":"DOWN"`.

--
# Endpoints : `/actuator/health`

* Avec `management.endpoint.health.show-details=always` : détails complets (composants, durée, cause d'erreur).

* Exemple de réponse détaillée :

```json
{
  "status":"UP",
  "components":{
    "db":{"status":"UP","details":{"database":"H2","validationQuery":"isValid()"}},
    "diskSpace":{"status":"UP","details":{"total":..., "free":...}}
  }
}
```

--
# Endpoints : `/actuator/metrics`

* Liste toutes les métriques disponibles (Micrometer).

* Accès à une métrique spécifique via `/actuator/metrics/{metric.name}`.

* Exemples : `jvm.memory.used`, `http.server.requests`, `process.cpu.usage`.

--
# Endpoints : `/actuator/metrics`


* Permet de surveiller performance, mémoire, nombre de requêtes, temps de réponse.

* Exporte vers des systèmes de monitoring (Prometheus, InfluxDB, etc.) via tags.

--
# Endpoints : `/actuator/env`

* Liste toutes les variables d'environnement et properties chargées.

* Filtrage par source : `application.properties`, variables système, `@PropertySource`, etc.

* Très utile pour déboguer : vérifier quelle valeur de property est effectivement utilisée et depuis quelle source.

* **Attention :** peut exposer des informations sensibles (connexions DB, tokens, etc.) — à sécuriser !

--
# Endpoints : `/actuator/beans`

* Liste tous les beans Spring enregistrés dans le conteneur IoC.

* Affiche : nom du bean, classe, scope, dépendances, création.

* Utile pour déboguer l'injection de dépendances ou vérifier qu'un bean est bien créé.

--
# Endpoints : `/actuator/loggers`

* Permet de consulter les niveaux de log actuels pour chaque logger (package).

* **POST** sur `/actuator/loggers/{name}` pour modifier le niveau dynamiquement :

```json
{"configuredLevel":"DEBUG"}
```

* Pratique en production pour augmenter les logs d'un composant sans redémarrer.

--
# Endpoints : `/actuator/info`

* Affiche des informations personnalisées sur l'application.

* Prérempli automatiquement depuis `pom.xml` (version, nom, description).

* Complément possible dans `application.properties`


--
# Endpoints : `/actuator/info`

* Ex :
```properties
info.app.name=Mon Application
info.app.version=1.0.0
info.company=INSEE
```

* Réponse : 
```json
{"app":
    {"name":"Mon Application","version":"1.0.0"},
    "build":{"version":"1.0.0-SNAPSHOT"},
...}
```

--
# Configuration avancée

* **Port dédié** — Exposer l'Actuator sur un port distinct (ex. 8081) :

```properties
management.server.port=8081
```

* **Chemin personnalisé** — Préfixer les endpoints (`/admin` au lieu de `/actuator`) :

```properties
management.endpoints.web.base-path=/admin
```
--
# Configuration avancée

* **Inclure/Exclure précisément** — Spécifier quels endpoints exposer/masquer :

```properties
management.endpoints.web.exposure.include=health,metrics,info
management.endpoints.web.exposure.exclude=beans,env
```

--
# Enjeux de sécurité

* **Exposition d'informations sensibles** — `/actuator/env`, `/actuator/beans`, `/actuator/httptrace` peuvent révéler secrets, configurations, chemins internes.

* **Pas d'authentification par défaut** — Les endpoints sont accessibles sans authentification.

* **Risque d'attaque par énumération** — Lister les beans ou les variables d'environnement peut aider un attaquant.

--
# Enjeux de sécurité

* **Recommandations rapides** :
  - En **développement** : exposer tous les endpoints pour faciliter le débogage.
  - En **production** : limiter à `health` et `metrics` (ou plus selon le besoin).
  - Toujours **sécuriser** via Spring Security (voir chapitre dédié).
  - Utiliser un **port dédié** et une **authentification forte** si nécessaire.

--
# Exemples d'utilisation (curl)

* Lister les endpoints disponibles :
```bash
curl http://localhost:8080/actuator
```

* Vérifier la santé :
```bash
curl http://localhost:8080/actuator/health
```

* Consulter l'info de l'app :
```bash
curl http://localhost:8080/actuator/info
```
--
# Exemples d'utilisation (curl)


* Récupérer une métrique spécifique :
```bash
curl http://localhost:8080/actuator/metrics/jvm.memory.used
```

* Modifier le niveau de log (avec authentification si sécurisé) :
```bash
curl -X POST http://localhost:8080/actuator/loggers/fr.insee.formation \
  -H "Content-Type: application/json" \
  -d '{"configuredLevel":"DEBUG"}'
```

--
# Intégration avec le Dashboard

* Le **Spring Boot Dashboard** (extension VS Code) affiche automatiquement les endpoints Actuator détectés.

* Clic sur un endpoint pour le consulter dans le navigateur.

* Affichage des beans, des métriques et de l'état en temps réel sans ligne de commande.

* **Point d'attention** : si les endpoints n'apparaissent pas, vérifier la valeur de `management.endpoints.web.exposure.include`

--
# TP2

Mettre en place l'Actuator

![](./img/diapo_formation_spring_boot_12.png)
