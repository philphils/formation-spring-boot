 # Spring Boot :

![](./img/diapo_formation_spring_boot_0.png) <!-- .element: class="image-large" -->

# Démarrage supersonic \! 
--

- Tour de parole pour mesurer le niveau d'expérience
- Questions organisationnelles


--
# Plan

* [Introduction à Spring Boot](./#/0/5)
* [Rappel sur le noyau de Spring](./#/1)
* [Architecture d'une application Spring Boot](./#/2)
* [Auto-configuration](./#/3)
* [Création d'une application Spring Boot](./#/4)
* [Structure d'un projet Spring Boot](./#/5)
* [Gestion des propriétés](./#/6)
* [Actuator pour la supervision](./#/7)
* [Gestion des profils](./#/8)

--
# Plan

* [Spring Data pour l'accès aux données](./#/9)
* [Conventions de nommage](./#/10)
* [Pagination, tri et streaming](./#/11)
* [Requêtes complexes avec Spring Data](./#/12)
* [Création d'une API REST](./#/13)
* [Spring Web pour les applications web](./#/14)
* [Gestion du JSON avec Jackson](./#/15)
* [Envoi de fichiers](./#/16)

--
# Plan

* [Gestion des exceptions dans une application web](./#/17)
* [Tests pour les applications web](./#/18)
* [Sécurité avec Spring Security](./#/19)
* [Conclusion](./#/20)
--
# Introduction

* En 2 décennies\, Spring a révolutionné le paysage du développement Java 

* Spring commence avec la mise en œuvre de l’injection de dépendances (Spring\-Core) 

* Ajout de couches successives : Spring\-Data\, Spring REST\, Spring Security… 

* Au final Spring est présent dans toutes les couches 

* On parle de la la « galaxy Spring » 



--
# Spring Boot : l’aboutissement

* Nouvelle extension qui chapeaute toutes les autres 

* Permet la configuration et le démarrage d’application avec un code minimal 

* Fonctionnalité de déploiement avec un Tomcat embarqué

* Choix des composants avec Spring Initializer

--
# Spring Boot : l’aboutissement

* Ajout de composant facile avec les starters 

* Permet d’avoir des applications prête à l’emploi très simplement 

* Très utile par exemple dans une logique micro\-service 

--
# Objectifs de la formation

* Créer un projet Spring\-Boot 

* Comprendre la structure du projet 

* Connaître les principaux starters 

--
# Objectifs de la formation

* Configurer son projet avec différents environnements 

* Créer une couche de persistence 

* Créer une API 

* Sécuriser son API