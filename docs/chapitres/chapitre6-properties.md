# Gestion des properties

--
# Premières fonctionnalités

* Les premières fonctionnalités Spring\-Boot concernent la gestion des properties 

* Permet de centraliser et rendre accessibles les properties de différentes sources 

* Outil de gestion des jeux de properties selon les différents environnements (profile) 

* D’autres fonctionnalités plus avancés comme le cryptage de properties 

--
# Centralisation des properties :
# L'objet `Environnement`

*  Spring\-Boot centralise les properties de différentes sources au sein de l’objet     Environment 
*  Les properties de différentes origines sont ainsi rendues accessibles à toute l’appli via `@Value`     ou encore `@ConfigurationProperties` 

--
# Priorisation des sources
# de properties

*  Un ordre défini la priorité des properties selon la source : 
   *  ligne de commande 
   *  variable d’environnement 
   *  fichier de profile 
   *  application.properties      
   *  `@PropertySource`
   *  `@ConfigurationProperties` 
   *  Propriétés par défaut ... 

--
# Gestion des properties

![](./img/diapo_formation_spring_boot_8.png) <!-- .element: class="image-large" -->

--
# Injection de properties

* La première technique pour récupérer une properties est 
```java
@Value("${chemin.de.ma.prop}")
private String cheminDeMaProp; //Le type peut être un autre type basique
```
* La synthaxe `${...}` permet de faire référence aux properties 
* Spring offre un language d’expression régulière puissant appelé SpEL (Spring Expr. Langage)

--
# SpEL: affiner
# l'injection de properties      

* On peut aussi définir plus finement ce que l’on souhaite injecter (valeurs par défaut\, valeurs conditionnelles\.\.\.). Ex :
```java
@Value("${ma.prop} : ‘defaultValueSiAbsent’" )
```
* On peut définir des expressions ternaires\, parcourir des collections\, exécuter du Java… 
* But stay KISS !!! 😜

--
# `@ConfigurationProperties`

* Spring Boot offre un autre mécanisme intéressant pour injecter un ensemble de propriétés dans un bean 

* Ce mécanisme permet de lier organiquement une classe Java (qui sera un bean Spring) avec un fichier de properties

* Il faut créer une classe annotée `@ConfigurationProperties` 

--
# `@ConfigurationProperties`

* Les noms des attributs de ce bean doivent correspondre au nom des properties 

* Les tirets\, underscores\, points doivent être remplacés par du camelCase 

* Ex : ma\-super\-property → maSuperProperty 



--
# `@ConfigurationProperties`

```java
@ConfigurationProperties(prefix="database")
public class DatabaseProperties {
   private String username; //Pointe vers database.username
   private Integer nbConnection; // Pointe vers database.nb-connection
...}
```

* Les attributs du bean sont alimentés automatiquement avec les properties disponibles 

* Le prefix permet de cibler un ensemble de properties lié à un domaine de l’appli (bdd\, batch…) 

--
# `@ConfigurationProperties` :
# utiliser les `record`

* Depuis Java 16+ on peut utiliser un `record` pour représenter les properties en immuable.
* Spring Boot (3.x) supporte le binding par constructeur — pratique avec les `record`.
* Avantages : immutabilité, moins de code, constructeur généré automatiquement.

--
# Exemple simple avec `record`

```java
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "database")
public record DatabaseProperties(
      String username, 
      Integer nbConnection) { }
```

`application.properties` :

```properties
database.username=sa
database.nb-connection=10
```

--
# Exemple avec propriétés 
# imbriquées (nested)

```java
@ConfigurationProperties(prefix = "app")
public record AppProperties(Database database) {
   public static record Database(String url, Credentials credentials) { }
   public static record Credentials(String user, String password) { }
}
```

`application.properties` :

```properties
app.database.url=jdbc:h2:mem:testdb
app.database.credentials.user=sa
app.database.credentials.password=secret
```

--
# Bonnes pratiques
# et points d'attention

* Les `record` bénéficient d'un binding par constructeur implicite sur Spring Boot 3.x.
* Attention aux types primitifs — préférer les wrappers (`Integer`, `Long`) pour gérer l'absence de valeur.

--
# Bonnes pratiques
# et points d'attention

* Pour d'importantes structures dynamiques, préférer `@ConfigurationProperties` sur des classes si vous avez besoin d'un comportement mutable ou d'API de construction personnalisée.
* Pensez à ajouter la dépendance `spring-boot-configuration-processor` pour générer les métadonnées (auto‑complétion dans l'IDE).

--
# Le module
# spring-boot-configuration-processor

*  Avec l’annotation     @ConfigurationProperties     on peut utiliser le module spring-boot-configuration-processor qui permet :
   *  auto\-complétion des fichiers properties (avec Eclipse version récente) 
   *  ajout de meta\-données sur les properties depuis les commentaires ou annotations Java 
   *  validation du contenu des properties 

--
# Auto-complétion et méta-données

*  Un fichier     `spring-configuration-metadata.json`     est  généré dans `target/.../classes/META-INF` qui contient les meta\-données 

* Ex avec : 
```java
/**
 * Nombre de lignes possible
 */
private Integer nbLignes = 10;
``` 

* On aura de l’auto\-complétion et des infos au survol (Attention : Faire un Maven clean + install)

--
# Validation des properties

* Ex : ![](./img/diapo_formation_spring_boot_10.png)

* Il est possible d’ajouter des contraintes de validation avec Jakarta. Ex : `@Min`, `@Max`, `@NotNull` ... 

* Il faut alors annoter la classe avec `@Validated` pour que la validation soit effectuée au démarrage du serveur 