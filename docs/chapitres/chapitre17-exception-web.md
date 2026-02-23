# La gestion des exceptions
# avec Spring Web

--
## Validation des données

- Nécessite la dépendance ``spring-boot-starter-validation``.

- On annote les classes (DTO) avec @NotNull, @Size, @Email...

- On déclenche la validation dans le contrôleur avec @Valid. Ex :

```java
@PostMapping
public User create(@Valid @RequestBody UserDTO userDto) {
    return userService.save(userDto);
}
```

--

### Exemple de `UserDTO` avec validation

```java
import javax.validation.constraints.*;

public class UserDTO {

    @NotNull(message = "Le nom ne doit pas être vide")
    @Size(min = 2, max = 50, message = "Le nom doit contenir entre 2 et 50 caractères")
    private String name;

    @NotNull(message = "L'email ne doit pas être vide")
    @Email(message = "L'email doit être une adresse valide")
    private String email;

    @NotNull(message = "Le mot de passe ne doit pas être vide")
    @Size(min = 8, message = "Le mot de passe doit contenir au moins 8 caractères")
    private String password;

    @NotNull(message = "Le rôle ne doit pas être vide")
    private String role;

    // Constructeurs, getters et setters
}
```

--

### Exemple de réponse par défaut

```json
{
  "timestamp": "2023-10-01T12:00:00.000+00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "errors": [
    "Le nom ne doit pas être vide",
    "L'email doit être une adresse valide",
    "Le mot de passe doit contenir au moins 8 caractères"
  ]
}
```

--
## La gestion des exceptions

- **Objectif** : Centraliser la gestion des exceptions pour une API cohérente et maintenable.
- **Outils clés** :
  - `@RestControllerAdvice` : Pour appliquer la gestion des exceptions à tous les contrôleurs.
  - `@ExceptionHandler` : Pour définir comment gérer une exception spécifique.
- **Sécurité** : Attention à une gestion trop globale qui donnerait des infos sensibles...

--

### Exemple de gestion d'une exception particulière

```java
@RestControllerAdvice
public class SpecificExceptionHandler {
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleNotFound(
                                    UserNotFoundException ex) {
        // Ici le message est contrôlé donc on l'affiche
        // à l'utilisateur
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ex.getMessage());
    }
}
```

--

### Exemple de gestion des exceptions globales

```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex) {
        // Côté serveur on loggue l'erreur complète pour le débogage
        logger.error("Une erreur inattendue est survenue : ", ex);

        // On ne contrôle pas le message donc
        // on affiche un message générique 
        // (on suppose qu'on a définit un objet ErrorResponse)
        ErrorResponse errorResponse = new ErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Une erreur inattendue est survenue. Veuillez réessayer plus tard."
        );
        return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorResponse);
    }
}
```

--

## Gestion des erreurs de validation

- Pour formatter les erreurs de validation de manière claire pour le client, utiliser `@ExceptionHandler(`
    `MethodArgumentNotValidException.class)` pour structurer les erreurs.

--

### Exemple de gestion des erreurs de validation

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseEntity.badRequest().body(errors);
    }
}
```

--

### Exemple de réponse pour les erreurs de validation

```json
{
  "name": "Le nom ne doit pas être vide",
  "email": "L'email doit être une adresse valide",
  "password": "Le mot de passe doit contenir au moins 8 caractères"
}
```