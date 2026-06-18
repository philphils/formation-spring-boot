## Implémentation d'une API REST
# avec Spring Web

--

## Spring Web

- Spring-Web Simplifie la création d'API REST en fournissant des annotations et des outils puissants.
- Intègre des fonctionnalités essentielles comme la conversion automatique JSON, la gestion des requêtes HTTP, et la validation des données.

--

## Dépendance Spring Web

- Pour transformer votre application en serveur Web, ajoutez au `pom.xml` :

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

- Cette dépendance inclut :

    - Tomcat : Le serveur d'application embarqué.

    - Jackson : Pour la conversion automatique Java <-> JSON.

    - Spring MVC : Le framework de routage.

--
# Le Controller REST

- On utilise l'annotation `@RestController`, combinaison de :
    - `@Controller` : Indique que la classe gère les requêtes HTTP.
    - `@ResponseBody` : Indique que les méthodes renvoient directement des données qui seront sérialisées dans le corps de la réponse (en JSON).

--
# Le Controller REST : exemple

```java
@RestController
@RequestMapping("/api/users")
public class UserController {

    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<User> getAll() {
        return userService.findAll();
    }
}
```
--
## Le Controller REST : explications

- **Annotations clés** :
  - `@RequestMapping` : Définit le préfixe pour tous les endpoints du contrôleur.
  - `@GetMapping` : Spécialise `@RequestMapping` pour les requêtes avec le verbe GET
  - `@PostMapping`, `@PutMapping`, `@DeleteMapping`... : Autres méthodes HTTP disponibles.
  - Ces annotations peuvent éventuellement compléter le chemin de l'URL. Ex :
    ```java
    // Définition du endpoint /api/users/stats
    @GetMapping("/stats")
    public List<User> stats() {
    ...
    ```

--

## Le Controller REST : explications
- **Conversion automatique en JSON** :
  - Spring convertit automatiquement les objets Java en JSON grâce à la librairie Jackson
  - Pas besoin de sérialiser manuellement.
  - Nous aborderons plus loin la configuration de Jackson.
  
--

## Le Controller REST :
## Résultat attendu

- Pour une requête GET sur `/users`, le résultat attendu est un JSON représentant la liste des utilisateurs :

```json
[
  {
    "id": 1,
    "name": "John Doe",
    "email": "john.doe@example.com",
    "country": {
      "id": 1,
      "name": "France"
    }
  },...
]
```

--
# Capturer les paramètres

- **@PathVariable** : Pour les segments d'URL variables.

    - GET /api/users/**42** → @PathVariable Long id

```java
@GetMapping("/api/users/{id}")
public User getUserById(@PathVariable Long id) {
    return userService.findById(id);
}
```

--
# Capturer les paramètres

- **@RequestParam** : Pour les filtres ou la pagination.

    - GET /api/users?**role=admin** → @RequestParam String role

```java
@GetMapping("/api/users")
public List<User> getUsersByRole(@RequestParam String role) {
    return userService.findByRole(role);
}
```

- On peut rendre le paramètre optionnel avec `@RequestParam(required = false)`

--
# Capturer les paramètres

- **@RequestBody** : Pour les données envoyées dans le corps de la requête (POST, PUT).

    - POST /api/users → la requête contient un JSON qui sera converti en objet :
    ```java
    @PostMapping
    public User create(@RequestBody User user) {
        return userService.save(user);
    }
    ```
--

# La réponse : ResponseEntity

- Pour un contrôle total (statut HTTP, headers), on utilise l'objet ResponseEntity<T> :

```java
@GetMapping("/{id}")
public ResponseEntity<User> getById(@PathVariable Long id) {
    Optional<User> userOptional = userRepository.findById(id);
    if (userOptional.isPresent()) {
        return ResponseEntity.ok(userOptional.get()); // 200 OK
    } else {
        return ResponseEntity.notFound()
            .body("L'utilisateur avec l'ID " 
            + id + " n'existe pas."); // 404 Not Found
    }
}
```

Permet de renvoyer des codes précis : 201 Created ou 204 No Content.

--

## Ajouter des messages aux codes de retour HTTP

- **Possiblité d'ajouter des messages pour :**
  - Clarifier la raison d'une erreur ou d'un succès.
  - Faciliter le débogage pour les clients de l'API.

- **Comment ajouter un message ?**
  - Utiliser `ResponseEntity.body()` pour inclure un message personnalisé.
  - Solution plus aboutie : Utiliser des objets de réponse dédiés pour structurer les messages.

--

### Exemple avec un objet de réponse structuré

```java
public class ApiResponse<T> {
    private int status;
    private String message;
    private T data;
    // Constructeurs, getters et setters
}
```

--

### Exemple avec un objet de réponse structuré

```java
@PostMapping
public ResponseEntity<ApiResponse<User>> createUser(@RequestBody User user) {
    User createdUser = userService.save(user);
    ApiResponse<User> response = new ApiResponse<>(
        HttpStatus.CREATED.value(),
        "Utilisateur créé avec succès",
        createdUser
    );
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
}
```
**Éviter les détails sensibles** : Ne pas inclure d'informations sensibles dans les messages d'erreur.