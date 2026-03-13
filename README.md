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
  - Retournez des messages d'erreur clairs mais sans donner d'informations critiques.

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
  - Ajouter un établissement à une unité légale (à partir de son identifiant)
- **Établissement** :
  - Récupérer tous les établissements.
  - Récupérer un établissement par son SIRET.
  - Récupérer les établissements d'une unité légale.

**Note :** Pour spécifier les informations renvoyés nous vous invitons pour plus de simplicité à utiliser les annotations Jackson (@JsonIgnore, @JsonInclude, @JsonManagedReference, etc.) directement dans les entités. Mais vous pouvez aussi créer des DTOs ce qui serait conseillé pour une réelle application.

#### Gestion des erreurs avec messages explicites

Pour améliorer l'expérience du client API, utilisez la classe `ErrorResponse` pour retourner des messages d'erreur clairs, structurés et dont vous contrôlez le contenu :

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

#### Tester manuellement votre API

- Démarrer votre application avec le profil `dev` (avec le Spring Boot Dashboard ou bien avec la commande `mvn spring-boot:run -Dspring-boot.run.profiles=dev`).
- Le composant SireneDataDevInitializer va charger des données de test en base de données mémoire (H2)
- Avec Postman ou l'outil de votre choix, tester les endpoints que vous avez implémentés (lecture des ULs, modification, suppression, lecture des établissements, etc.).

---
### 4. Génération de fichiers
Ajoutez un endpoint `GET /api/unites-legales/export/csv` pour exporter l'ensemble des unités légales.

- **Indications pour la génération de fichiers** : Utiliser les méthodes generateCsv des classes de services pour générer le fichier CSV.

- **Rappel : Utilisez `ResponseEntity`** pour renvoyer le fichier :
  - `ResponseEntity<byte[]>` pour les fichiers générés en mémoire.
  - `ResponseEntity<Resource>` pour les fichiers existants (pas ici, mais utile à savoir).

---

### 5. Gestion des gros volumes de données avec `Stream`
1. **Téléchargez et préparez le dump** :
   - Téléchargez le dump depuis cette adresse : [tp_5_integration_data.dump.zip](https://gitlab.insee.fr/formations-informatiques/cursus-nouveaux-arrivants-sndin/formation-spring-boot/-/blob/main/formation-spring-boot-project/src/main/resources/dumps/tp_5_integration_data.dump.zip?ref_type=heads)
   - Dézippez le fichier téléchargé et placez-le dans le répertoire `src/main/resources/dumps/` de votre projet.
2. **Chargez le dump dans votre base de données sous Podman** :
   - Placez-vous dans le répertoire racine du projet et exécutez la commande suivante :
   ```bash
    # Charge un dump PostgreSQL au format "directory" en parallèle sur 4 threads
    pg_restore -U postgres -d sirene_db -j 4 -F directory ./src/main/resources/dumps/tp_5_integration_data.dump
   ```
   - Vous venez de charger 1 million d'unités légales avec quelques établissements par unité dans votre base de données.

3. **Passez en profil `integration`** :
   - Démarrer votre application avec le profil `integration` (avec le Spring Boot Dashboard ou bien avec la commande `mvn spring-boot:run -Dspring-boot.run.profiles=integration`).
   - Consulter le endpoint `GET /api/unites-legales` avec l'outil de votre choix
   - Constater le temps de réponse (infini)

4. **Implémentez un endpoint en mode Stream renvoyant la totalité des unités légales** :
   - Utiliser la méthode de service qui renvoie un `Stream` contenant les unités légales .
   - Créer un endpoint ``/api/unites-legales/stream`` pour récupérer les données en flux continu.
   - Utiliser `StreamingResponseBody` pour envoyer les données.
   - Attention pour avoir un affichage au fur et à mesure dans le navigateur ne définissez pas de ``ContentDisposition`` et renvoyer les données avec un ``Content-Type`` de ``text/plain``.
   - Tester votre endpoint avec l'outil de votre choix et comparer

   - **Remarque :** le stream permet de récupérer les données au fur et à mesure de leur génération, pour des volumes de données très importants cette technique permet aussi d'éviter de saturer la mémoire et même d'éviter une éventuelle ``OutOfMemoryError``.

---

### 6. Gestion des exceptions

- Laisser votre serveur actif avec le profil intégration.
- Arrêter votre conteneur de base de données.
- Tester votre endpoint ``/api/unites-legales/stream`` avec l'outil de votre choix.
- Le message qui s'affiche vous semble-t-il approprié ? Quels problèmes l'affichage d'un tel message pose-t-il ?

Nous allons mettre en place une gestion des exceptions pour afficher un message plus explicite et éviter de donner des informations sensibles à l'utilisateur.
- Créez une classe `GlobalExceptionHandler` pour centraliser la gestion des exceptions (Utilisez `@RestControllerAdvice`).
- Ecrivez une première méthode pour gérer les exceptions globalement.
- Configurer un message par défaut pour les exceptions et logger le contenu de l'exception.
- Tester toujours avec la base de données arrêtée.
- **Indication :** Penser à réutiliser la classe `ErrorResponse` pour structurer la réponse d'erreur.

---

### 7. Validation des données

Nous allons ajouter des fonctionnalités de validation des données pour les endpoints ayant en entrée des objets (ici les unités légales et les établissements).
- Ajoutez des validations directement dans les entités ``UniteLegale`` et ``Etablissement`` pour valider les données envoyées dans les requêtes (dans une application réelle on privilégiera l'utilisation de DTOs).
- Utilisez des annotations comme `@NotNull`, `@Size`, etc.
- Utilisez `@Valid` pour déclencher la validation les données.
- Ajouter une méthode dans la classe `GlobalExceptionHandler` pour gérer les erreurs de validation et renvoyer un message d'erreur explicite (interception de l'exception ``MethodArgumentNotValidException``).
- Vérifier que vous récupérer le bon code HTTP et le bon message d'erreur, en cas de siren invalide par ex.

---

### 8. Tests
Créez une classe de tests `UniteLegaleControllerTest` unitaires sans serveur embarqué avec `@WebMvcTest` pour tester le contrôleur `UniteLegaleController`.
- Vérifiez que le endpoint `GET /api/unites-legales/{siren}` renvoie le code HTTP 200 si le SIREN est valide.
- Vérifiez que vous récupérez le code HTTP 404 si le SIREN ne correspond à aucune unité légale.

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
   - Classe ``ErrorResponse`` pour structurer les réponses d'erreur.
3. **Validation des données** :
   - Utilisation de `@Valid` et annotations de validation dans les entités.
4. **Tests** :
   - Classe `UniteLegaleControllerTest` pour les tests unitaires.
   - Classe `UniteLegaleControllerIntegrationTest` pour les tests d'intégration.

---

Bonne chance pour ce TP ! Si vous avez des questions, n'hésitez pas à demander.