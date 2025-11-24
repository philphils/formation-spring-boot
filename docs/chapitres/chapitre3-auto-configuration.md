# L’auto\-configuration 

--
# Les starters

* Les briques principales de Spring\-Boot sont les « starters » 

* Il s’agit de librairies Maven (ou Gradle) 

* Leur ajout déclenche avec l’annotation `@SpringBootApplication` la création de beans et configuration d’éléments d’environnement 

* Il existe des starters pour tous les champs d’utilisation : web\, data\, batch\, test\, mail\.\.\. 

--
# Le starter « parent »

* Le premier starter est celui dont le projet doit hériter\, appelée « parent » 

* On a donc cette déclaration en début de pom :

```xml
<parent>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-parent</artifactId>
  <version>3.3.1</version>
  <relativePath/> <!-- lookup parent from repository -->
</parent>
```
* Ce starter détermine la version de Spring\-Boot que nous utiliserons… et donc les versions des autres dépendances \!

--
# Compatibilité des versions

* Une des fonctionnalités essentielles de Spring\-Boot est d’offrir un mécanisme de gestion des versions de librairies 

* De nombreuses versions de librairies et plugins sont définies au sein du parent Spring-Boot

* Les versions sont préconfigurées via la balise Maven `dependencyManagement` et     `pluginManagement`

* Le développeur ne précise donc pas la version de la librairie\, elle est récupérée dans le pom\-parent

--
# Centralisation des versions

* L’équipe Spring propose donc des jeux de version très larges de librairies (test\, web\, data\, plugin\.\.\.) 

* Un ensemble de tests et tests d’intégration sont effectués par Spring pour garantir au mieux de leur compatibilité 

* On changera donc la version du parent Spring seulement pour effectuer une mise à jour de nombreuses librairies 

* Mais les tests de l’application restent nécessaires pour s’assurer le plus possible de ne rien casser \! 

--
# Configuration des plugins

* Le même mécanisme permet la préconfiguration de plugins Maven 

* La version et la configuration est définie avec la balise     pluginManagement 

* Par ex\. les plugin maven de constitution du jar ou du war avec une application Spring\-Boot 

--
# Configuration des plugins

* De nombreuses propriétés sont définies et donc accessibles\, comme la version de Java utilisée par exemple 

* Les propriétés sont surchageables dans le pom du projet bien sûr 

--
# Les autres starters

* De nombreux starters existent concernant tous les domaines 

* Leur ajout déclenchent la constitution de beans et composants\, leur configuration\, et leur articulation 

* Par ex\. l’ajout du starter\-web permet de récupérer les librairies Spring\-Web… 

* Mais aussi la mise en place d’un Tomcat embarqué qui démarre avec le lancement de l’application \! 

* L’objectif est de disposer d’une application « clef en main » 
