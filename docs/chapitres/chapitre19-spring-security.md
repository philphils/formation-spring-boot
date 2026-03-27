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
- L'utilisateur utilise ce token pour accéder à nos API
- L'API vérifie la validité du token et autorise l'accès

--

### JWT (JSON Web Token)
- Les token fournit par Keycloak sont des JWT
- Il s'agit d'un objet JSON muni d'une signature
- Les JWT comportent 3 parties :
   - Header : Algorithme de signature et type de token
   - Payload : Données utilisateur (claims, partie personnalisable par le développeur)
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
- Une fois l'ajout du starter, 3 éléments à définir :
  - Définition d'un bean ``SecurityFilterChain``  
  - Configuration des propriétés ``spring.security.oauth2.resourceserver.jwt``
  - Définition des rôles et permissions

--
### La définition du bean ``SecurityFilterChain``

- Le bean ``SecurityFilterChain`` est la brique permettant de définir les règles de sécurité de l'application.
- Il est défini dans une classe de configuration, souvent ``SecurityConfig``.
- Il contient souvent les règles générales, comme l'activation de la sécurité, la désactivation du CSRF, etc.

--

### Principales attaques sur les API

- CSRF (Cross-Site Request Forgery) : Attaque par usurpation de session (impossible dans le cas d'une API REST car authentification via token).
- XSS (Cross-Site Scripting) : Injection de code malveillant.
- SQL Injection : Injection de code SQL malveillant.
- DDoS (Distributed Denial of Service) : Attaque par saturation de la bande passante.

--

### Exemple de configuration de sécurité basique

```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    // Liste des URLs permises sans authentification
    List<String> listeUrlPermises = List.of(HEALTHCHECK, VERSION, SWAGGER /* ... */);

    // Configuration de la HttpSecurity
    return http
        // On désactive le CSRF car on authentifie via des tokens JWT
        // et non via des cookies de session
        .csrf(csrf -> csrf.disable())

        // L'API est stateless : Spring Security ne doit donc pas créer de session
        .sessionManagement(session -> session
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

        .authorizeHttpRequests(auth -> auth
            // Définition des URLs n'exigeant pas d'authentification
            .requestMatchers(listeUrlPermises.toArray(new String[0])).permitAll()

            // On autorise les requêtes OPTIONS utilisées pour les requêtes CORS preflight,
            // c-à-d permettant de vérifier si le site d'origine est autorisé à appeler l'API
            .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

            // On demande une authentification pour toutes les autres URLs
            .anyRequest().authenticated()
        )

        // On configure l'application comme OAuth2 Resource Server.
        // Spring utilise alors le JwtDecoder auto-configuré à partir
        // des propriétés spring.security.oauth2.resourceserver.jwt.*
        .oauth2ResourceServer(oauth2 -> oauth2.jwt())

        .build();
}
```

--

### Configuration des propriétés

- Spring propose quelques properties pour configurer la sécurité OAuth2 :
```properties
# Définit l'uri de l'identity provider
spring.security.oauth2.resourceserver.jwt.issuer-uri=https://keycloak/realms/myrealm
# Définit le nom du claim contenant les rôles dans le JWT
spring.security.oauth2.resourceserver.jwt.authorities-claim-name=realm_access.roles
# Définit un éventuel préfixe à ajouter aux rôles
spring.security.oauth2.resourceserver.jwt.authority-prefix=ROLE_
```

--

### Configuration des rôles et permissions

- En général on ne définit dans le bean ``SecurityFilterChain`` que les règles générales.
- Il est tout de même possible de définir des règles plus précises, ex :
   ```java
   .requestMatchers("/admin/**").hasRole("ADMIN")
   ```
- On préfera souvent définir les règles dans les controllers via les annotations Spring Security

--
### Définition des rôles et permissions

- On active les annotations Spring Security avec ``@EnableMethodSecurity`` dans la classe de configuration
- On peut ensiute définir les rôles et permissions dans les controllers avec ``@PreAuthorize``, ex:
   ```java
   @PreAuthorize("hasRole('ADMIN')")
   @GetMapping("/admin")
   public String admin() {
      return "admin";
   }
   ```
- Cette annotation couvre la majorité des cas d'utilisation.

--
### Rôles et permissions: conditions complexes

- Il est aussi possible de définir des règles plus complexes :
  - Conditions logiques :
    ```java
    @PreAuthorize("hasRole('ADMIN') and hasRole('USER')")
    ```
  - Conditions sur les paramètres :
    ```java
    @PreAuthorize("#id == authentication.principal.id")
    ```
  - Appel aux méthodes d'un service :
    ```java
    @PreAuthorize("@securityService.hasPermission(#id)")
    ```
--

### Rôles et permissions: condition post-exécution

- L'annotation ``@PostAuthorize`` permet de définir des règles en fonction des données retournées par la méthode
- Exemple :
  ```java
  //on vérifie ici que l'id de l'utilisateur retourné 
  //par la méthode est bien l'id de l'utilisateur authentifié
  @PostAuthorize("returnObject.id == authentication.principal.id")
  @GetMapping("/user/{id}")
  public User getUser(@PathVariable Long id) {
    return userService.getUser(id);
  }
  ```

--

### Récupération du JWT

- On peut récupérer directement le JWT dans le controller avec l'annotation ``@AuthenticationPrincipal``
- Exemple :
  ```java
  @GetMapping("/user")
  public String user(@AuthenticationPrincipal Jwt jwt) {
    return jwt.getClaim("preferred_username");
  }
  ```
- Utile pour récupérer des informations sur l'utilisateur authentifié.

--

### Exemple avec /me 

- Exemple classique de récupération des informations de l'utilisateur authentifié:
  ```java
  @GetMapping("/me")
  public Map<String, Object> me(@AuthenticationPrincipal Jwt jwt) {
      return Map.of(
          "username", jwt.getClaim("preferred_username"),
          "email", jwt.getClaim("email"),
          "subject", jwt.getSubject()
      );
  }  
  ```

--

### Récupération de l'ensemble
### du contexte de sécurité

- On peut aussi au besoin récupérer l'ensemble du contexte de sécurité avec l'annotation ``@CurrentSecurityContext``
- Exemple :
  ```java
  @GetMapping("/debug")
  public Object debug(
    @CurrentSecurityContext SecurityContext context) {
      return context;
  }
  ```

--

### Tester Spring Security

- Spring propose quelques outils pour tester les règles de sécurité
- Il faut importer :
```xml
<dependency>
     <groupId>org.springframework.security</groupId>
     <artifactId>spring-security-test</artifactId>
     <scope>test</scope>
 </dependency>
```

--
### @WithMockUser

- L'annotation ``@WithMockUser`` permet de définir un utilisateur mocké pour les tests
- On peut alors tester la logique de sécurité de l'application
- Exemple :
  ```java
  @Test
  @WithMockUser(roles = "ADMIN")
  void testAdmin() {
    // ...
  }
  ```

--
### Le piège du CSRF

- Spring Security active par défaut le CSRF
- Il faut donc ajouter un jeton csrf pour les tests
- Exemple :
```java
  mockMvc.perform(post("/api/unites-legales")
    .with(SecurityMockMvcRequestPostProcessors.csrf())
    //...
```
--
### Indications

- Pour tester la logique d'un controller, on pourra utiliser ``@WebMvcTest``
- Pour tester les règles globales définies dans ``SecurityConfig``, on devra utiliser ``@SpringBootTest`` en important la classe de configuration 

--

# TP6 :  

Spring-Security

![](./img/diapo_formation_spring_boot_14.png)