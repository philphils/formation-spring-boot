# TP : Sécurisation d'une API Spring Boot avec Keycloak

--------------------

## Partie 1 : Configuration de Keycloak local

## Objectif

Mettre en place un serveur d'identité Keycloak pour authentifier des
utilisateurs, générer des tokens JWT et préparer la sécurisation d'une
API Spring Boot.

------------------------------------------------------------------------

## 1. Lancement de Keycloak avec Docker

Nous utilisons le mode `start-dev` pour ignorer les contraintes HTTPS en
local.

``` bash
docker run -d --name keycloak-tp \
  -p 8081:8080 \
  -e KEYCLOAK_ADMIN=admin \
  -e KEYCLOAK_ADMIN_PASSWORD=admin \
  quay.io/keycloak/keycloak:latest \
  start-dev
```

-   Accès : http://localhost:8081\
-   Identifiants : admin / admin

------------------------------------------------------------------------

## 2. Création du Realm

Le Realm est l'espace isolé qui contiendra vos utilisateurs et vos
configurations.

-   Dans le menu latéral gauche, cliquez sur la liste déroulante en haut
    (Master)
-   Cliquez sur **Create Realm**
-   Realm name : `formation-spring-boot`
-   Cliquez sur **Create**

------------------------------------------------------------------------

## 3. Configuration du Client (Spring API)

Le client représente votre application Spring Boot côté Keycloak.

-   Menu latéral : **Clients**
-   Bouton : **Create client**

### Paramètres

-   Client ID : `spring-api` → Next

### Capability Config

-   ✔ Standard flow\
-   ✔ Direct access grants

→ Next

### Login settings

-   Root URL : http://localhost:8080
-   Valid Redirect URIs : *
-   Web Origins : \* (important pour éviter les erreurs CORS)

→ Save

------------------------------------------------------------------------

## 4. Création des Rôles

-   Menu latéral : **Realm roles**
-   Cliquez sur **Create role**

Créer les rôles :

-   USER
-   GESTIONNAIRE
-   ADMIN

------------------------------------------------------------------------

## 5. Création d'un Utilisateur

-   Menu latéral : **Users**
-   Cliquez sur **Add user**

### Paramètres

-   Username : `user1`
-   (Optionnel mais recommandé) :
    -   Email
    -   First name
    -   Last name

→ Create

### Credentials

-   Onglet : **Credentials**
-   Set password :
    -   Password : `user1`
    -   Temporary : ❌ OFF (très important)

### Role mapping

-   Onglet : **Role mapping**
-   Assign role :
    -   USER

(Optionnel : créer `admin1` avec le rôle `ADMIN`)

------------------------------------------------------------------------

## 6. Vérification du Point de Terminaison

http://localhost:8081/realms/formation-spring-boot/.well-known/openid-configuration

Cette URL doit retourner un JSON.

------------------------------------------------------------------------

## 7. Récupération d'un Token (Test cURL)

``` bash
curl -X POST "http://localhost:8081/realms/formation-spring-boot/protocol/openid-connect/token" \
 -H "Content-Type: application/x-www-form-urlencoded" \
 -d "client_id=spring-api" \
 -d "username=user1" \
 -d "password=user1" \
 -d "grant_type=password"
```

------------------------------------------------------------------------

## 8. Analyse du Token

1.  Copier la valeur du champ `access_token`
2.  Aller sur https://jwt.io
3.  Coller le token

### Vérification attendue

``` json
"realm_access": {
  "roles": ["USER", "offline_access", ...]
}
```

------------------------------------------------------------------------

## ⚠️ Pièges fréquents

-   **Temporary Password**\
    → Doit être OFF sinon erreur "Account is not fully set up"

-   **Client ID**\
    → Doit correspondre exactement à celui utilisé dans Spring

-   **Web Origins**\
    → Obligatoire pour les appels frontend (CORS)

-   **Profil utilisateur incomplet**\
    → Renseigner email / prénom / nom si erreur "Account is not fully
    set up"

------------------------------------------------------------------------

## Partie 2 : Sécurisation de l'API Spring Boot avec Keycloak

## 1. Ajouter les dépendances Maven

``` xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-oauth2-resource-server</artifactId>
</dependency>
```

------------------------------------------------------------------------

## 2. Configuration Spring (`application-dev.properties`)

``` properties
spring.security.oauth2.resourceserver.jwt.issuer-uri=http://localhost:8081/realms/formation-spring-boot
spring.security.oauth2.resourceserver.jwt.authorities-claim-name=realm_access.roles
spring.security.oauth2.resourceserver.jwt.authority-prefix=ROLE_
```

------------------------------------------------------------------------

## 3. Configuration de la sécurité

Créer une classe `SecurityConfig.java`.

### À implémenter :

-   Activer la sécurité (annotation `@EnableMethodSecurity`)
-   Désactiver CSRF (API REST)
-   Configurer une API **stateless** (pas de session)
-   Autoriser les requêtes OPTIONS (CORS)

### Règles d'accès :

-   Accès libre :
    -   `/actuator/health`
    -   `/actuator/info`
-   Accès réservé aux ADMIN :
    -   `/actuator/env`
    -   `/actuator/metrics`
    -   `/actuator/beans`
    -   `/actuator/loggers`
    -   `/datasource/info`
    -   `/check-database`
-   Toutes les autres requêtes nécessitent une authentification

------------------------------------------------------------------------

## 4. Sécurisation avec `@PreAuthorize`

Utiliser les annotations Spring Security sur les contrôleurs.

### Règles métier :

-   Endpoints en lecture :
    -   accessibles aux utilisateurs authentifiés (`ROLE_USER` minimum)
-   Endpoints en écriture :
    -   `UniteLegaleController`
    -   `EtablissementController`

    👉 accessibles uniquement aux utilisateurs avec le rôle
    `GESTIONNAIRE`

------------------------------------------------------------------------

## 5. Configuration de Postman (OAuth2)

Postman permet de récupérer automatiquement un token JWT.

### Étapes :

1.  Ouvrir une requête ou collection
2.  Onglet **Authorization**
3.  Type : **OAuth 2.0** :

Configuration :
```text
    Token Name                     Keycloak Token

    Grant Type                     Password Credentials

    Access Token URL               http://localhost:8081/realms/formation-spring-boot/protocol/openid-connect/token

    Client ID                      spring-api

    Username                       user1

    Password                       user1
```
4.  Cliquer sur **Get New Access Token**
5.  Cliquer sur **Use Token**

👉 Postman ajoutera automatiquement :

```text
Authorization: Bearer <token>
```

------------------------------------------------------------------------

## 6. Tests manuels

  Endpoint              Résultat attendu
  --------------------- ----------------------
  GET /unite-legales    OK avec USER
  POST /unite-legales   OK avec GESTIONNAIRE
  GET /actuator/env     OK avec ADMIN

------------------------------------------------------------------------

## ⚠️ Points d'attention

-   Vérifier que le token contient bien les rôles (`realm_access.roles`)
-   Vérifier que `ROLE_` est bien préfixé côté Spring
-   Vérifier que l'URL `issuer-uri` correspond exactement à Keycloak
-   Vérifier que l'application est bien démarrée avec le bon profil
    (`dev` si nécessaire)

------------------------------------------------------------------------

## 7. Tests unitaires

1. Ajouter la dépendance spring-security-test :
  ```xml
  <dependency>
      <groupId>org.springframework.security</groupId>
      <artifactId>spring-security-test</artifactId>
      <scope>test</scope>
  </dependency>
  ```
2. Sécurisation de l'Actuator

L'objectif est de vérifier que les endpoints sensibles de monitoring ne sont pas exposés à n'importe quel utilisateur.

Créer une classe `SecurityActuatorTest` pour valider l'accès à `/actuator/env`.

🛠️ Configuration
- Utilisez **`@SpringBootTest`** : l'Actuator nécessite le contexte complet pour être chargé.
- Utilisez **`@AutoConfigureMockMvc`** pour simuler les appels HTTP.
- **Le piège :** N'oubliez pas de mocker `JwtDecoder` avec `@MockBean` pour éviter que Spring ne tente de se connecter à un vrai serveur d'autorisation (Keycloak/Auth0) au démarrage du test.

🧪 Scénarios à implémenter
  - **Accès Interdit** : Simulez un utilisateur avec le rôle `USER` et vérifiez qu'il reçoit une **403 Forbidden**.
  - **Accès Autorisé** : Simulez un utilisateur avec le rôle `ADMIN` et vérifiez qu'il reçoit une **200 OK**.

3. Sécurisation du Controller Unité Légale

L'objectif est de valider les règles d'accès basées sur les rôles `USER` et `GESTIONNAIRE` définies sur vos points d'entrée API.

Créer une classe `SecurityUniteLegaleTest` pour valider les accès aux endpoints métier.

🛠️ Configuration
- Utilisez **`@WebMvcTest(UniteLegaleController.class)`** : c'est plus rapide et ciblé sur un seul contrôleur.
- Mockez tous les services nécessaires (`UniteLegaleService`, `EtablissementService`, `JwtDecoder`).
- Injectez l'**`ObjectMapper`** via `@Autowired` pour transformer vos objets en JSON.

🧪 Scénarios à implémenter
  - **Lecture (GET /api/unites-legales)** :
    - Un `USER` doit pouvoir accéder aux données (**200 OK**).
  - **Création (POST /api/unites-legales)** :
    - Un `USER` doit être rejeté (**403 Forbidden**).
    - Un `GESTIONNAIRE` doit être autorisé (**201 Created**).

⚠️ Le point critique (Le piège du CSRF)
Par défaut, Spring Security protège les requêtes `POST` contre les attaques CSRF. 
- Dans votre test `mockMvc.perform(post(...))`, vous devez impérativement ajouter `.with(csrf())` (import statique de `SecurityMockMvcRequestPostProcessors`).
- Sans cela, même avec le bon rôle, vous recevrez une **403**.

> [!IMPORTANT]
> Vérifiez bien que vous envoyez un corps de requête (JSON) valide lors du test `POST` pour éviter une erreur 400 qui masquerait votre résultat de sécurité.
