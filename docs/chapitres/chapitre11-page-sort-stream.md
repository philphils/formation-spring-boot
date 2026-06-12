# Spring-Data

## Paging, Sorting, Stream


--

## Les tris et limites

- On pourra appliquer un tri sur notre requête (portant sur n'importe quel champ) avec :
    ```java
    List<User> findByNameOrderByNameAsc(String start);
    ``` 
- On pourra aussi choisir de ne récupérer que les N premiers ou derniers résultats :
    ```java
    //ici N = 0
    User findFirstByNameOrderByNameDesc(String start);
    ```
    ```java
    //ici N = 3
    List<User> findTop3ByNameOrderByNameDesc(String start);
    ``` 

--

## Les tris et limites

- Enfin pour rendre la limite paramétrable, on pourra utiliser la classe `org.springframework.data.domain.Limit`:
    ```java
    List<User> findByNameOrderByNameAsc(Limit limit);
    ``` 
- Pour des besoin plus poussés, on utilisera les `Pageable` et les `Sort`...

--

## Les Pageable et les Sort

- On a souvent besoin de récupérer les résultats par paquets de taille définie.

- Exemple classique : Tableau paginé avec tri possible sur les champs

- Pour répondre à ce besoin, Spring-Data propose le mécanisme des `Pageable`

--

## Les Pageable et les Sort

- L'interface `JPARepository` hérite de l'interface `PagingAndSortingRepository`

- Il est possible d'ajouter sur n'importe quelle méthode un paramètre de type `Pageable`, et/ou un paramètre de type `Sort`

- L'implémentation permettra alors de récupérer les résultats sous forme de `Page` permettant de les récupérer par paquets de taille définie lors de l'appel

--

## Exemple de Pageable

- Pour les méthodes présentes par défaut, on dispose d'emblée d'une méthode avec un attribut `Pageable` et/ou `Sort`

- Exemple (extrait de l'interface `PagingAndSortingRepository`):
    ```java 
    Page<T> findAll(Pageable pageable);
    ``` 

- Pour les méthodes ajoutées au repository, il faut ajouter à l'interface une méthode contenant l'attribut `Pageable` et/ou `Sort`. Ex:
    ```java
    List<User> findByNameOrderByNameAsc(Pageable pageable);
    ``` 

--

## Exemple de Pageable

- Utilisation :
    ```java
    Pageable firstPageWithTwoElements = PageRequest.of(0, 5);

    Page<User> pageUser = 
        userRepository.findByNameOrderByNameAsc(firstPageWithFiveElements);

    for(User user: pageUser) { 
        ... //On itère sur les 5 éléments constituants la 1ère page
    }

    //Constitution du pageable suivant
    Pageable secondPageWithTwoElements = 
        firstPageWithTwoElements.nextPageable();

    //Récupération des résultats suivants
    Page<User> secondPageUser = 
        userRepository.findByNameOrderByNameAsc(secondPageWithTwoElements);
    ...

    ``` 

--

## Exemple de Sort

- Pour les tris on peut utiliser l'objet `Sort` soit de manière indépendante, soit en l'ajoutant à l'objet `Pageable`

- Exemple de tri sans `Pageable`:
    ```java
    List<User> users = userRepository.findAll(Sort.by("email"));
    ``` 

- Exemple au sein d'un pageable :
    ```java
    Pageable pageableSortedByName = 
        PageRequest.of(0, 3, Sort.by("name"));
    ``` 

--

## Conclusion Page et Sort

- Les `Pageable` et les `Sort` fournissent des outils très souples pour gérer la pagination et les tris

- Permettent de s'adapter au besoin selon le contexte sans avoir à réécrire des méthodes au sein des `Repository`

- Attention les requêtes sont réexécutées à chaque nouvelle page !

- Pour le parcours d'un grand nombre d'objets, on considerera l'utilisation des `Stream`...

--

## Le choix du type retour

- Spring-Data peut adapter l'implémentation au type retour choisi !

- Par exemple, on peut choisir de renvoyer un `Optional` :
    ```java
    Optional<User> findByEmail(String email);
    ```

- On pourra alors utiliser les méthodes `isPresent` pour s'assurer de la non-nullité

- Attention si plusieurs utilisateurs ont le même mail on aura une 
    <span style="color:red">IncorrectResultSizeDataAccessException</span>

--

## Le type Stream

- De même pour les méthodes renvoyant plusieurs objets, on pourra choisir le type parmi `List`, `Set`, `Collection` ou `Stream`

- Un cas particulièrement intéressant est celui du type `Stream`

- Avec ce type retour, Spring-Data nous renverra les données sous forme d'un flux...

--

## Le type Stream

- Mais surtout en arrière plan, il utilisera si la BDD le permet un curseur (Postgres ✅)

- La requête est alors exécuté une seule fois ➡️ gain de performance /à la pagination

- Les résultats sont conservés côté serveur et transmis par paquets ➡️ pas de lenteurs réseau

- Pour itérer sur un grand nombre d'objets sans besoin de pagination, c'est de loin la méthode la plus performante !

--

## Le type Stream : exemple

- Définition de la méthode renvoyant un `Stream` :
    ```java
    Stream<User> findByCountry(Country country);
    ``` 
- Utilisation :
    ```java
    Country countryFrance = countryRepository.findByName("France");
    userRepository.findByCountry(countryFrance)
        .map(user ->    "L'utilisateur " 
                        + user.getName() 
                        + " habite en France !")
        .forEach(string -> log.info(string));
    ``` 

--

## Le type Stream : exemple

- Attention : En cas d'appel d'une "opération terminale" sur le `Stream` (`forEach`, `collect`, `count`...), l'ensemble des objets sont collectés et le curseur est fermé !
    ```java
    Country countryFrance = countryRepository.findByName("France");
    userRepository.findByCountry(countryFrance) //Ouverture du curseur
        .map(user -> "L'utilisateur "
                     + user.getName()           //Curseur tjrs ouvert
                     + " habite en France !") 
        .forEach(string -> log.info(string));  //Fermeture du curseur
                                               //, récupération de toutes les lignes
    ```
- On perd alors le bénéfice de l'utilisation d'un curseur en termes de performances