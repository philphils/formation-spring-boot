# Formation Spring Boot : Démarrage Supersonic ! 🚀

Ce dépôt contient l'ensemble des ressources pédagogiques, supports de cours et travaux pratiques de la formation **Spring Boot 3.x**. L'objectif est de maîtriser l'écosystème Spring pour construire des applications Java modernes, configurables et sécurisées.

## 🎯 Objectifs de la formation
* **Industrialiser** : Créer des projets robustes avec Maven et Spring Initializr.
* **Comprendre** : Maîtriser l'auto-configuration et le rôle des *Starters*.
* **Configurer** : Gérer les environnements via les *Properties* et les *Profiles*.
* **Observer** : Monitorer l'état de santé de l'application avec *Actuator*.
* **Persister** : Manipuler les données simplement avec *Spring Data JPA*.
* **Sécuriser** : Protéger les points d'entrée de l'API avec *Spring Security*.

---

## 📑 Sommaire du cursus

### 1. Rappels autour du noyau Spring (IoC & DI)

- Principes et rôle de l'injection de dépendance
- Technique de création des beans (annotation/Java based)
- Rôle central du container
- Pattern de l'inversion de contrôle (IoC)

### 2. Spring Boot : Auto-configuration & Starters
- Le rôle du **Grand Architecte** : simplifier et standardiser la configuration manuelle.
- Le **Starter Parent** : gestion centralisée des versions de dépendances et de leur cohérence.
- **Spring Initializr** : générer un projet Spring Boot avec les dépendances nécessaires.
- Utilisation des outils : Spring Boot Dashboard et extensions VS Code.
- Structure d'un projet Spring Boot

**💡 TP1** : Initialisation d'un projet Spring Boot et découverte de la structure.

### 3. Gestion des Configurations & Profiles
- Externalisation de la configuration (`application.properties` / `.yml`).
- Centralisation des properties de différentes sources avec l'Environnement Abstraction.
- **Type-safe configuration** avec `@ConfigurationProperties`.
- Validation des configurations avec ``spring-boot-configuration-processor``.

### 4. Observabilité avec Spring Boot Actuator
- Introduction à l'**Actuator** : rôle et fonctionnalités clés.
- Configuration des endpoints (`/actuator`).
- Exposition des métriques de santé (`/health`) et monitoring (`/metrics`).
- Visualisation des variables d'environnement (`/env`) et des beans (`/beans`).
- Gestion dynamique des logs via `/loggers`.
- Actuator et enjeux de sécurité.
- Stratégies multi-environnements (dev, test, prod) via les **Profiles**.

**💡 TP2** : Configuration de l'Actuator et découverte des endpoints.
**💡 TP3** : Mise en place des Profiles et des Properties.

### 5. Persistance avec Spring Data JPA
- Mise en place de la couche de données avec Spring Data JPA.
- Configuration des ``Datasource`` et de JPA
- Génération de l'implémentation des méthodes d'accès aux données via les ``Repository``
- Génération par convention de nommage
- Gestion des volumes importants avec la pagination ou les ``Stream``
- Utilisation de ``@Query`` pour les requêtes complexes et des DTOs
- Configuration multi-datasources 

**💡 TP4** : Couche de données avec Spring Data JPA

### 6. Création d'une API REST avec Spring Web

- Principes de l'architecture REST et bonnes pratiques de conception
- Mise en place de Spring Web
- Création des contrôleurs REST avec ``@Controller`` et ``@RestController``
- Configuration fine des requêtes avec ``@RequestMapping`` et ``@GetMapping``, ``@PostMapping``, etc.
- Gestion des paramètres de requête et des corps de requête
- Sérialisation et désérialisation des objets avec Jackson
- Envoi de fichiers et streaming pour les gros volumes
- Gestion des erreurs et des exceptions avec ``@ControllerAdvice``
- Outils de tests avec ``@SpringBootTest`` et ``@WebMvcTest``

**💡 TP5** : Création d'une API REST

### 7. Sécurité Applicative 🔐
//TODO à compléter

---
## Support de cours

**Diapo accessible à :** https://philphils.github.io/formation-spring-boot/

---

## 🛠 Prérequis
- **Java 17+**
- **Maven 3.8+**
- VS Code avec le **Spring Boot Extension Pack** (recommandé) ou IntelliJ/STS.

---

## 🚀 Organisation des Travaux Pratiques
Le dépôt est structuré par branches pour permettre une progression étape par étape :

| Branche | Sujet du TP |
| :--- | :--- |
| `main` | Structure globale et documentation |
| `TP1` | Initialisation du projet et découverte de la structure |
| `TP2` | Configuration de l'Actuator et monitoring |
| `TP3` | Mise en place des Profiles et des Properties |
| `TP4` | Couche de données avec Spring Data JPA |
| `TP5` | Création d'une API REST |

---

## 📚 Ressources complémentaires
- [Documentation officielle Spring Boot](https://spring.io/projects/spring-boot)
- [Spring Boot Reference Guide](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/)
- [Spring Initializr](https://start.spring.io/)