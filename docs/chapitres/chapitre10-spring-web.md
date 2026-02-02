# Créer une API REST
# avec Spring Web

--

## Introduction : L'approche API "Headless"

- Dans une architecture avec **React**, Spring Boot ne sert pas de moteur de rendu (pas de JSP/Thymeleaf).
- L'application devient un **fournisseur de ressources** via des services REST.
- Les échanges se font exclusivement en **JSON**.
- Le protocole HTTP est utilisé de manière normalisée pour piloter les données.

--

## Pourquoi l'architecture REST ?

- Théorisée par Roy Fielding pour répondre aux défis de croissance du Web moderne :

    - **Scalabilité** (Montée en charge) : En étant "sans état" (Stateless), le serveur peut traiter des millions de requêtes sans saturer sa mémoire vive par des sessions utilisateurs.
    - **Standardisation** : Au lieu d'inventer des protocoles complexes (comme SOAP), REST réutilise les fondations du Web : le protocole HTTP, les URL et les codes de statut.

--

## Pourquoi l'architecture REST ?
-
    - **Indépendance Client/Serveur** : Permet de faire évoluer le backend (Spring) et le frontend (React) séparément, tant que le contrat de l'API (le JSON) ne change pas.
    - **Interopérabilité** : Le format JSON est universel. Une API REST peut être consommée aussi bien par un navigateur, une application mobile ou un autre serveur.

--

## Principes de l'API REST

- **Ressources** : Tout objet métier est une ressource accessible via une URI (ex: `/api/users`).
- **Verbes HTTP** : On utilise les 5 méthodes standards pour définir l'action :
    - `GET` : Récupérer une ressource ou une collection.
    - `POST` : Créer une nouvelle ressource.
    - `PUT` : Mettre à jour complètement.
    - `PATCH` : Mettre à jour partiellement.
    - `DELETE` : Supprimer.
- **Stateless** : Le serveur ne stocke pas l'état du client

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

- On utilise l'annotation `@RestController`, combinaison :
    - `@Controller` : Indique que la classe gère les requêtes HTTP.
    - `@ResponseBody` : Indique que les méthodes renvoient directement des données qui seront sérialisées dans le corps de la réponse (en JSON).

--
# Le Controller REST : exemple

```java
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public List<User> getAll() {
        return userService.findAll();
    }
}
```
--
# Capturer les paramètres

- **@PathVariable** : Pour les segments d'URL variables.

    - GET /api/users/42 -> @PathVariable Long id

- **@RequestParam** : Pour les filtres ou la pagination.

    - GET /api/users?role=admin -> @RequestParam String role

- **@RequestBody** : Pour réceptionner un objet JSON (souvent un DTO).

    - POST /api/users -> @RequestBody UserDTO userDto

--
La réponse : ResponseEntity

    Pour un contrôle total (statut HTTP, headers), on utilise ResponseEntity<T>.

```java
@GetMapping("/{id}")
public ResponseEntity<User> getById(@PathVariable Long id) {
    return userRepository.findById(id)
        .map(user -> ResponseEntity.ok(user)) // 200 OK
        .orElse(ResponseEntity.notFound().build()); // 404 Not Found
}
```

Permet de renvoyer des codes précis : 201 Created ou 204 No Content.

--
Validation des données

    Nécessite la dépendance spring-boot-starter-validation.

    On annote les classes (DTO) avec @NotNull, @Size, @Email.

    On déclenche la validation dans le contrôleur avec @Valid.

```java
@PostMapping
public User create(@Valid @RequestBody UserDTO userDto) {
    return userService.save(userDto);
}
```

--
Gestion globale des exceptions

    Centralisation via @RestControllerAdvice et @ExceptionHandler.

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleNotFound(UserNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
}
```

--
Mode Stream et Production de fichier

    Comme vu avec Spring Data JPA, le type Stream<T> permet de traiter de gros volumes via un curseur BDD.

    Côté Web, on utilise StreamingResponseBody pour écrire la réponse progressivement sans charger toute la liste en mémoire.

--
Exemple : Export CSV Streamé

```java
@GetMapping("/export")
public ResponseEntity<StreamingResponseBody> exportCsv() {
    Stream<User> userStream = userRepository.findAllAsStream();

    StreamingResponseBody responseBody = response -> {
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(response))) {
            userStream.forEach(user -> {
                try {
                    writer.write(user.getName() + "," + user.getEmail() + "\n");
                    writer.flush();
                } catch (IOException e) { throw new UncheckedIOException(e); }
            });
        } finally {
            userStream.close();
        }
    };

    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=users.csv")
        .contentType(MediaType.parseMediaType("text/csv"))
        .body(responseBody);
}
```

--
Le problème du CORS

    Par défaut, un navigateur bloque les requêtes entre localhost:3000 (React) et localhost:8080 (API).

    Solution rapide via @CrossOrigin :

```java
@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api")
public class MyController { ... }
```

--
Conclusion

    Spring Web offre les outils pour exposer les données de manière performante (Streaming).

    Le respect des principes REST assure une bonne intégration avec React.

    Attention : L'API est actuellement ouverte à tous, la sécurité sera traitée au prochain chapitre.