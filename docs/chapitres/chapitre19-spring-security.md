# Spring Security
## Sécuriser une API REST

--

### Pourquoi sécuriser une API ?
- Pour authentifier un utilisateur
- Pour lui permettre d'accéder à certaines ressources
- Pour lui interdire d'accéder à d'autres ressources
- Pour empêcher les attaques

--

### Pourquoi la sécurité ?

- La sécurité est devenu un enjeu majeur.
- Nos données sont souvent sensibles.
- Les attaques sont de plus en plus fréquentes et sophistiquées.
- Les utilisateurs attendent des solutions sécurisées.

--

### Authentification vs Autorisation
- **Authentification** : Vérifier l'identité de l'utilisateur.
- **Autorisation** : Vérifier ce que l'utilisateur peut faire.

--

### Fonctionnement global

- Nos architectures utilisent Keycloak pour l'authentification.
- Keycloack est notre Identity Provider (IdP)
- C'est Keycloack qui fournit les tokens JWT, ie les jetons d'authentification.

--

### Le flux d'authentification

- L'utilisateur interroge une API
- Utilisateur non-authentifié -> Redirection vers Keycloak
- Keycloack vérifie les identifiants de l'utilisateur
- Identifiants valides -> Keycloak renvoie un token
- L'utilisateur utilise ce token pour accéder à nos API.

--

### JWT (JSON Web Token)
- Les token fournit par Keycloak sont des JWT
- Il s'agit d'un objet JSON muni d'une signature
- Les JWT comportent 3 parties :
   - Header : Algorithme de signature et type de token
   - Payload : Données utilisateur (claims)
   - Signature : Signature du token

--

### Exemple de JWT
```json
{
  "alg": "RS256",
  "typ": "JWT"
}
{
  "sub": "1234567890",
  "name": "John Doe",
  "realm_access": {
    "roles": [
      "USER",
      "ADMIN"
    ]
  },...
}
// Signature (binaire) : correspond à la concaténation
// du header et du payload chiffrés avec la clef privée de Keycloak
HMACSHA256(
  base64UrlEncode(header) + "." +
  base64UrlEncode(payload),
  secret
)
```

--
### Signature et clef publique

- La signature est calculée avec la clef privée de Keycloak
- L'algorithme utilisé (RSA) permet de s'assurer que seul Keycloak a pu la calculer
- Keycloack publie sa clef publique
- Les API peuvent vérifier la signature des JWT avec cette clef publique et s'assurer ainsi de la validité du token

--

### Schéma du flux d'authentification


![](./img/diapo_formation_spring_boot_13.png) <!-- .element: class="image-large" -->

_(Le téléchargement de la clef publique par l'API n'est effectué qu'une fois)_

--
### Mise en place de Spring-Security
- Ajout du starter :
   ```xml
    <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-oauth2-resource-server</artifactId>
    </dependency>
   ```
- Définition d'un bean ``SecurityFilterChain``  
- Ajout des properties permettant de cibler le realm et de configurer la récupération des rôles

--
### La définition du bean ``SecurityFilterChain``

//TODO définir le bean SecurityFilterChain de base avec OAuth2 puis ajouter les règles de sécurité
//TODO désactiver csrf
//TODO autoriser HTTP OPTIONS
//TODO Expliquer @PreAuthorize

--

### Concepts clés
- `SecurityFilterChain` : Chaîne de filtres de sécurité.
- `HttpSecurity` : Configuration des règles de sécurité.
- `authorizeHttpRequests` : Autorisation des requêtes.

--

### JWT dans Spring Security
- Bearer token : Utilisé dans le header `Authorization`.
- `JwtDecoder` : Vérification de la signature et expiration.

--

### Spring vérifie le token localement
- Pas besoin de contacter Keycloak à chaque requête.
- Vérification de la signature et expiration.

--

### Autorisation
1. Par URL :
   ```java
   .requestMatchers("/admin/**").hasRole("ADMIN")
   ```
2. Par méthode (recommandé) :
   ```java
   @PreAuthorize("hasRole('ADMIN')")
   ```

--

### Rôles vs Authorities
- `ROLE_ADMIN` : Rôle administrateur.
- `ROLE_USER` : Rôle utilisateur.

--

### Authentification ≠ Autorisation
- Authentification : Qui est l'utilisateur ?
- Autorisation : Que peut-il faire ?

--

### Configuration API REST propre
- Désactiver CSRF :
  ```java
  .csrf(csrf -> csrf.disable())
  ```
- Stateless :
  ```java
  .sessionManagement(session -> session
      .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
  ```

--

### CORS
- Autoriser les requêtes OPTIONS :
  ```java
  .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
  ```

--

### API = Stateless
- Pas de session côté serveur.
- Utilisation de tokens JWT.