# Créer son projet 
# Spring Boot 

--
# Créer un projet Spring-Boot

* Pour créer son projet\, Spring\-Boot met à disposition un outil : Spring Initializr 

* Simple d’utilisation via le site web (    [https://start\.spring\.io/](https://start.spring.io/)    ) 

* Possible de l’utiliser avec une commande curl 

* Ou encore avec une interface de création intégrée dans Spring Tool Suite (plugin Eclipse) 

* Il existe un outil nommé Spring Boot CLI : mais semble peu utilisé et en voie de déprécation 

--
# Spring Tool Suite

* Spring a développé un plugin pour Eclipse 

* Possible de l’ajouter à une instance d’Eclipse… 

* Ou de télécharger la version standalone basée sur une version d’Eclipse 

* Permet d’avoir accès à plusieurs fonctionnalité\, comme la création de projet Spring Boot 

* Autres fonctionnalités : écran de lancement d’appli\, fenêtre de recherche des beans… 



--
# Exemple création via STS

![](./img/diapo_formation_spring_boot_3.png) <!-- .element: class="image-large" -->



--
# Choix des caractéristiques

![](./img/diapo_formation_spring_boot_4.png) <!-- .element: class="image-large" -->



--
# Sélection des dépendances

![](./img/diapo_formation_spring_boot_5.png) <!-- .element: class="image-large" -->

--
# VS Code
# Spring Boot Extension Pack

* VS Code propose un ensemble d'extensions pratiques pour le développement Spring Boot.
* Le `Spring Boot Extension Pack` regroupe le support Spring, Spring Initializr, Boot Dashboard, Live Spring Boot, etc.
* Prérequis : `Java Extension Pack` (diagnostics, language server) et `Lombok` si utilisé.

--
# Installation et prérequis

* Ouvrir la vue **Extensions** et chercher `Spring Boot Extension Pack`.
* Installer l'extension et vérifier qu'un JDK est configuré dans VS Code (`java.home` ou via `settings.json`).
* Vérifier le `PATH` et préférer l'utilisation du wrapper `mvnw` / `gradlew` du projet.

--
# Créer un projet
# avec Spring Initializr

* Ouvrir la **Command Palette** (`Ctrl+Shift+P`) → `Spring Initializr: Generate a Maven Project`
* Suivre l'assistant : choisir le langage, version Spring Boot, Group, Artifact, packaging, Java version et les dépendances
* VS Code télécharge et génère le projet puis propose d'ouvrir le dossier

--
# Spring Boot Dashboard

![](./img/diapo_formation_spring_boot_4_1.png) <!-- .element: class="image-large" -->


--
# Spring Boot Dashboard

* Le **Spring Boot Dashboard** (icône Spring) liste les applications Spring Boot détectées dans l'espace de travail.
* Permet de démarrer/arrêter une application, d'afficher les logs et d'accéder aux endpoints exposés.
* Liste les beans et les endpoints détectés et permet de retrouver l'endroit où ils sont créés

--
# Spring Boot Dashboard

* Les properties actives sont listées avec leur origine
* Une fenêtre donne les infos concernant l'état de la JVM (heap, Garbage Collector etc.)
* Attention à activer au besoin la visibilités de tous les beans ou endpoints, et pas seulement ceux que vous avez défini (ex. pour accéder aux endpoints de l'actuator)

--
# Gestion des properties

* Éditer `src/main/resources/application.properties` ou `application.yml` dans VS Code
* `Ctrl + Espace` permet d'accéder à l'auto-completion pour retrouver plus facilement les chemins des properties
* Proposition d'ajouter des meta-données et disposer de l'auto-complétion pour les properties du projet

--
# Activation des profiles

* Pour activer un profile : `Clic-Droit` et `Run (ou Debug) with profile`
* Les profiles sont détectés avec les fichier application-(profile).properties présents
* Sélectionner le ou les profiles à activer
* Les properties correspondantes sont alors mises à jour