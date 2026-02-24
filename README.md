# TP5 : Créer une API REST avec Spring Web

## Objectifs
- Concevoir et implémenter une API REST pour exposer des données SIRENE (unités légales et établissements).
- Réfléchir au nommage des endpoints et aux verbes HTTP appropriés.
- Implémenter des fonctionnalités avancées comme la génération de fichiers et les streams.
- Utiliser Spring Boot Actuator pour monitorer les performances.
- Tester les endpoints et les fonctionnalités.

---

## Étapes du TP

### 0. Préparation du projet
**Vérifiez les dépendances** dans le `pom.xml` :
- Vérifier la présence du starter `spring-boot-starter-web`.
- Vérifier la présence du starter `spring-boot-starter-data-jpa`.
- Vérifier la présence du starter `spring-boot-starter-validation`.
- Vérifier la présence du starter `spring-boot-starter-actuator`.

---

### 2. Rappel sur la conception des endpoints REST
Réfléchissez à la conception des endpoints pour gérer les unités légales et les établissements. Voici quelques questions pour vous guider :
- **Quels verbes HTTP utiliser ?**
  - `GET` pour récupérer des données.
  - `POST` pour créer des données.
  - `PUT` pour mettre à jour des données.
  - `DELETE` pour supprimer des données.
- **Comment nommer les endpoints ?**
  - Exemples :
    - `/api/unites-legales` pour gérer les unités légales.
    - `/api/etablissements` pour gérer les établissements.
    - `/api/unites-legales/{id}/etablissements` pour récupérer les établissements d'une unité légale.
- **Comment gérer les erreurs ?**
  - Utilisez des codes HTTP appropriés (ex : `404` pour une ressource non trouvée).
  - Retournez des messages d'erreur clairs.

#### Codes de retour HTTP courants

| Code | Status | Utilisation | Exemple |
|------|--------|-------------|---------|
| **200** | OK | Requête réussie, données retournées | GET, PUT |
| **201** | Created | Ressource créée avec succès | POST |
| **204** | No Content | Requête réussie, aucune donnée retournée | DELETE |
| **400** | Bad Request | Requête malformée (données invalides) | POST avec données erronées |
| **404** | Not Found | Ressource non trouvée | GET /api/unites-legales/999 |
| **409** | Conflict | Conflit (ex: SIREN déjà existant) | POST avec doublon |
| **500** | Server Error | Erreur serveur | Erreur interne |

---

### 3. Implémentation des endpoints
Implémentez les endpoints suivants. Utilisez les services et repositories disponibles dans le projet (vous n'avez que les contrôleurs à créer) :
- **Unité légale** :
  - Récupérer toutes les unités légales.
  - Récupérer une unité légale par son SIREN.
  - Créer une nouvelle unité légale.
  - Mettre à jour une unité légale.
  - Supprimer une unité légale.
- **Établissement** :
  - Récupérer tous les établissements.
  - Récupérer un établissement par son SIRET.
  - Récupérer les établissements d'une unité légale.

Note : Pour spécifier les informations renvoyés nous vous invitons pour plus de simplicité à utiliser les annotations Jackson (@JsonIgnore, @JsonInclude, @JsonManagedReference, etc.). Mais vous pouvez aussi créer des DTOs.

#### Gestion des erreurs avec messages explicites

Pour améliorer l'expérience du client API, utilisez la classe `ErrorResponse` pour retourner des messages d'erreur clairs et structurés :

```java
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    private int status;
    private String message;
}
```

**Exemple d'utilisation dans un contrôleur** :
```java
@GetMapping("/{id}")
public ResponseEntity<Object> getUniteLegaleById(@PathVariable Long id) {
    Optional<UniteLegale> unite = uniteLegaleService.findById(id);
    if (unite.isPresent()) {
        return ResponseEntity.ok(unite.get());
    }
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(new ErrorResponse(404, "Aucune unité légale correspondant à l'identifiant: " + id));
}
```

**Réponse d'erreur JSON** :
```json
{
  "status": 404,
  "message": "Aucune unité légale correspondant à l'identifiant: 999"
}
```

#### Tester votre API

- Démarrer votre application avec le profil `dev` (avec le Spring Boot Dashboard ou bien avec la commande `mvn spring-boot:run -Dspring-boot.run.profiles=dev`).
- Le composant SireneDataDevInitializer va charger des données de test en base de données mémoire (H2)
- Avec Postman ou l'outil de votre choix, tester les endpoints que vous avez implémentés (lecture des ULs, modification, suppression, lecture des établissements, etc.).

---

### 4. Génération de fichiers
Ajoutez un endpoint pour exporter les données en CSV ou JSON. Réfléchissez à la manière de générer le fichier et de le renvoyer.
- **Exemple d'endpoint** :
  - `GET /api/unites-legales/export/csv` pour exporter les unités légales en CSV.
  - `GET /api/unites-legales/export/json` pour exporter les unités légales en JSON.
- **Rappel : Utilisez `ResponseEntity`** pour renvoyer le fichier :
  - `ResponseEntity<byte[]>` pour les fichiers générés en mémoire.
  - `ResponseEntity<Resource>` pour les fichiers existants (pas ici, mais utile à savoir).

- **Indications pour la génération de fichiers** : Utiliser les méthodes generateCsv des classes de services pour générer le fichier CSV.

---

### 5. Gestion des gros volumes de données avec `Stream`
1. **Passez en profil `integration`** :
   - Utilisez PostgreSQL pour gérer des données volumineuses.
2. **Chargez le dump dans votre base de données sous Podman** :
   - Placer vous dans le répertoire racine du projet et exécutez la commande suivante :
  ```bash
  # Charge un dump PostgreSQL au format "directory" en parallèle sur 4 threads
  pg_restore -U postgres -d sirene_db -j 4 -F directory ./src/main/resources/dumps/tp_5_integration_data.dump
  ```
3. **Implémentez un endpoint renvoyant la totalité des unités légales** :
   - Utilisez la méthode de service qui renvoie un `Stream` contenant les unités légales.
   - Créez un endpoint pour récupérer les données en flux continu.
   - Utilisez `StreamingResponseBody` pour envoyer les données.
   - Attention pour avoir un affichage au fur et à mesure dans le navigateur ne définissez pas de ``ContentDisposition`` mais renvoyer les données avec un ``Content-Type`` de ``text/plain``.
4. **Comparez les endpoints** :
   - Au sein de votre navigateur afficher les urls suivantes :
   - `GET /api/unites-legales/export/csv`.
   - `GET /api/unites-legales/stream`.
   - Remarque : le stream permet de récupérer les données au fur et à mesure de leur génération, pour des volumes de données très importants cette technique permet aussi d'éviter de saturer la mémoire et d'éviter une éventuelle ``OutOfMemoryError``.

---

### 8. Gestion des exceptions
Créez une classe `GlobalExceptionHandler` pour centraliser la gestion des exceptions.
- Utilisez `@RestControllerAdvice` pour gérer les exceptions globalement.
- Retournez des messages d'erreur clairs et des codes HTTP appropriés.

---

### 9. Validation des données
Ajoutez des validations dans les DTOs pour valider les données envoyées dans les requêtes.
- Utilisez `@Valid` pour valider les données.
- Utilisez des annotations comme `@NotNull`, `@Size`, etc.
- Vérifiez la taille du SIREN et le fait qu'il ne soit pas null pour l'endpoint : `GET /api/unites-legales/{siren}`.
- Vérifier que vous récupérer le bon code HTTP et le bon message d'erreur en cas de siren invalide.

---

### 10. Tests
Créez une classe de tests `UniteLegaleControllerTest` unitaires sans serveur embarqué avec `@WebMvcTest` pour tester le contrôleur `UniteLegaleController`.
- Vérifiez que le endpoint `GET /api/unites-legales/{siren}` renvoie le code HTTP 200 si le SIREN est valide.
- Vérifiez que vous récupérez l'exception `ResponseStatusException` avec le code HTTP 400 si le SIREN est null, vide ou pas de la bonne taille.

Créez une classe de tests d'intégration `UniteLegaleControllerIntegrationTest` avec `@SpringBootTest`.
- Créez quelques unités légales en base de données.
- Tester que vous pouvez les récupérer avec le endpoint `GET /api/unites-legales/stream`.

---

## Livrables
1. **Contrôleurs** :
   - `UniteLegaleController` avec les endpoints CRUD et les fonctionnalités de génération de fichiers et de streaming.
   - `EtablissementController` avec les endpoints CRUD.
2. **Gestion des exceptions** :
   - Classe `GlobalExceptionHandler` pour centraliser la gestion des exceptions.
3. **Validation des données** :
   - Utilisation de `@Valid` et annotations de validation dans les DTOs.
4. **Tests** :
   - Classe `UniteLegaleControllerTest` pour les tests unitaires.
   - Classe `UniteLegaleControllerIntegrationTest` pour les tests d'intégration.

---

Bonne chance pour ce TP ! Si vous avez des questions, n'hésitez pas à demander.