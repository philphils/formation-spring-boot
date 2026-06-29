# Nouveautés Spring Boot 4
## pour le développement Web

--

## Introduction : Spring Boot 4 et le Web

- Spring Boot 4 (basé sur Spring Framework 7 et Jakarta EE 11) introduit des **améliorations majeures** pour le développement d'API REST et la consommation de services HTTP. 

--

## Introduction : Spring Boot 4 et le Web

Ce chapitre présente **deux fonctionnalités clés** :
- Le **versioning d'API natif** pour gérer plusieurs versions d'un même endpoint
- Les **clients HTTP déclaratifs** pour simplifier les appels aux APIs externes

--

## Versioning d'API intégré

Pourquoi le versioning ?

- Permettre l'**évolution de l'API** sans casser les clients existants
- Gérer des **changements incompatibles** (modification de contrats, suppression de champs)
- Maintenir plusieurs versions **simultanément**

--

### Solution Spring Boot 4 : Support natif

- Spring Boot 4 ajoute un **mécanisme de versioning natif** pour Spring MVC et WebFlux.
- Configuration du versioning :

```properties
# Version par défaut si aucune n'est spécifiée
spring.mvc.apiversion.default=1.0.0

# Méthode de lecture de la version (header, query, media-type, path)
spring.mvc.apiversion.use.header=X-API-Version
```
--

### Options de résolution de version (`spring.mvc.apiversion.use`):
- `header` : Version dans un header HTTP (ex: `X-API-Version: 2.0`)
- `parameter` : Version dans un paramètre de requête (ex: `?api-version=2.0`)
- `media-type` : Version dans le `Accept` header (ex: `Accept: application/json;version=2.0`)
- `path-segment` : Version dans le chemin de l'URL (ex: `/v2/users`)

--

### Implémentation dans les contrôleurs

- Annoter les endpoints avec une version

```java
@RestController
@RequestMapping("/api/users")
public class UserController {
    
    // Version 1.0 (par défaut)
    @GetMapping(version = "1.0")
    public List<UserV1> getAllV1() {
        return userService.findAllV1();
    }
    
    // Version 2.0 et supérieures
    @GetMapping(version = "2.0+")
    public Page<UserV2> getAllV2(Pageable pageable) {
        return userService.findAllV2(pageable);
    }
    
    // Version exacte
    @GetMapping(value = "/{id}", version = "1.5")
    public UserV1 getByIdV1_5(@PathVariable Long id) {
        return userService.findByIdV1_5(id);
    }
}
```

--

### Version dans le chemin

- Si `spring.mvc.apiversion.use.path-segment` est configuré:

```java
@RestController
@RequestMapping("/{version}")
public class UserController {

    @GetMapping(path = "/users", version = "1")
    public List<UserV1> getAllV1() { ... }

    @GetMapping(path = "/users", version = "2")
    public List<UserV2> getAllV2() { ... }
}
```

--

## Exemple complet avec header

**Configuration** :
```properties
spring.mvc.apiversion.default=1.0
spring.mvc.apiversion.use.header=X-API-Version
```

**Contrôleur** :
```java
@RestController
@RequestMapping("/api/products")
public class ProductController {
    
    @GetMapping(version = "1.0")
    public List<ProductBasic> getAllBasic() {
        // Version 1.0 : réponse basique
        return productService.findAllBasic();
    }
    
    @GetMapping(version = "2.0+")
    public List<ProductDetailed> getAllDetailed() {
        // Version 2.0+ : réponse avec plus de détails
        return productService.findAllDetailed();
    }
}
```

--

## Exemple complet avec header
**Appels clients** :
```bash
# Appel version 1.0 (ou par défaut)
curl -H "X-API-Version: 1.0" http://localhost:8080/api/products

# Appel version 2.0
curl -H "X-API-Version: 2.0" http://localhost:8080/api/products
```

--

## Clients HTTP déclaratifs

- Le problème : Appels HTTP complexes...

- Auparavant, pour appeler une API externe, il fallait :
    - Utiliser `RestTemplate` (déprécié) ou `WebClient` (réactif)
    - Gérer manuellement les URLs, headers, timeouts
    - Écrire du code boilerplate pour chaque appel

--

### Solution Spring Boot 4 : Interfaces déclaratives

- Spring Boot 4 **auto-configure** les clients HTTP déclarés via des **interfaces annotées**.
- Étape 1 : Déclarer l'interface

```java
import org.springframework.web.service.annotation.*;

@HttpExchange("/users")  // Base path
public interface UserClient {
    
    // GET /users/{id}
    @GetExchange("/{id}")
    User getUserById(@PathVariable Long id);
    
    // GET /users?email={email}
    @GetExchange
    List<User> getUsersByEmail(@RequestParam String email);
    
    // POST /users
    @PostExchange
    User createUser(@RequestBody User user);
    
    // PUT /users/{id}
    @PutExchange("/{id}")
    User updateUser(@PathVariable Long id, @RequestBody User user);
    
    // DELETE /users/{id}
    @DeleteExchange("/{id}")
    void deleteUser(@PathVariable Long id);
}
```

--

### Solution Spring Boot 4 : Interfaces déclaratives


- Étape 2 : Configurer le client, dans `application.properties` :

```properties
# Configuration du client pour un service externe
spring.http.serviceclient.users.base-url=https://api.example.com
spring.http.serviceclient.users.connect-timeout=2s
spring.http.serviceclient.users.read-timeout=5s
```

- Et importer l'interface :

```java
@SpringBootApplication
@ImportHttpServices(group = "users", types = UserClient.class)
public class MyApplication {
}
```

--

### Solution Spring Boot 4 : Interfaces déclaratives

- Étape 3 : Utiliser le client

```java
@Service
public class UserService {
    
    private final UserClient userClient;
    
    // Injection automatique par Spring
    public UserService(UserClient userClient) {
        this.userClient = userClient;
    }
    
    public User getUserDetails(Long id) {
        // Appel transparent vers l'API externe
        return userClient.getUserById(id);
    }
    
    public User createUser(User user) {
        return userClient.createUser(user);
    }
}
```