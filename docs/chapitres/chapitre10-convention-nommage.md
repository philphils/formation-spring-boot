# Spring-Data

## L'implémentation par convention de nommage

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
## Récapitulatif des opérateurs

| Opérateur          | Équivalent SQL                     |
|--------------------|------------------------------------|
| `And`              | `WHERE name = ? AND email = ?`     |
| `Or`               | `WHERE name = ? OR email = ?`      |
| `Between`          | `WHERE age BETWEEN ? AND ?`        |
| `LessThan`         | `WHERE age < ?`                    |
| `LessThanEqual`    | `WHERE age <= ?`                   |
| `GreaterThan`      | `WHERE age > ?`                    |
| `GreaterThanEqual` | `WHERE age >= ?`                   |
| `Is`, `Equals`     | `WHERE name = ?` (rendre l'égalité explicite) |


--
## Récapitulatif des opérateurs

| Opérateur          | Équivalent SQL                     |
|--------------------|------------------------------------|
| `After`            | `WHERE date > ?`                   |
| `Before`           | `WHERE date < ?`                   |
| `IsNull`           | `WHERE name IS NULL`               |
| `IsNotNull`, `NotNull` | `WHERE name IS NOT NULL`      |
| `Like`             | `WHERE name LIKE ?`                |
| `NotLike`          | `WHERE name NOT LIKE ?`            |
| `StartingWith`     | `WHERE name LIKE ?%`               |
| `EndingWith`       | `WHERE name LIKE %?`               |

--

## Récapitulatif des opérateurs

| Opérateur          | Équivalent SQL                     |
|--------------------|------------------------------------|
| `Containing`       | `WHERE name LIKE %?%`              |
| `OrderBy`          | `WHERE name = ? ORDER BY age ASC`  |
| `Not`              | `WHERE name <> ?`                  |
| `In`               | `WHERE name IN ?`                  |
| `NotIn`            | `WHERE name NOT IN ?`              |
| `True`             | `WHERE active = true`              |
| `False`            | `WHERE active = false`             |
| `IgnoreCase`       | `WHERE UPPER(name) = UPPER(?)`     |

