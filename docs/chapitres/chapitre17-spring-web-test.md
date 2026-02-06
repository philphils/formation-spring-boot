## Tester une API REST
# avec Spring Boot

--

### Introduction aux tests d'API REST

- Il est important de tester votre API pour vérifier le bon fonctionnement des endpoints.
- Les tests porteront sur les requêtes HTTP, les codes de statut, les données retournées et enfin les cas d'erreur.
- Spring Boot propose des outils et annotations pour faciliter ces tests.

--

### `@SpringBootTest` :
### Chargement complet du contexte Spring
   - Charge le contexte Spring complet
   - Utilisé pour les tests d'intégration.
   - Peut démarrer un serveur embarqué (Tomcat) si configuré.

--

### `@SpringBootTest` :
### Lancement d'un serveur embarqué
   - En configurant ``webEnvironment`` on lance un serveur embarqué
   - On peut alors utiliser ``TestRestTemplate`` pour faire des requêtes HTTP
   - On peut aussi utiliser ``@LocalServerPort`` pour récupérer le port du serveur
   - Pour tester les contrôleurs avec tout le contexte mais sans serveur on utilisera ``@AutoConfigureMockMvc``

--

### `@SpringBootTest` :
### Exemple de configuration

```java
@SpringBootTest(webEnvironment = 
    SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void testGetAllUsers() {
        ResponseEntity<List> response = restTemplate.getForEntity(
            "http://localhost:" + port + "/api/users",
            List.class
        );
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}
```

--

### `@WebMvcTest` : 
### Pour tester les contrôleurs
- Charge uniquement les composants nécessaires pour tester les contrôleurs (équivalent de ``@DataJpaTest``).
- Utilisé pour les tests unitaires des contrôleurs.
- On utilisera ``MockMvc`` pour simuler des requêtes HTTP.
- Utile pour tester de manière unitaire les contrôleurs sans démarrer un serveur.

--

### `@WebMvcTest` :
### Exemple de configuration

```java
@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    public void testGetAllUsers() throws Exception {
        List<User> users = Arrays.asList(
            new User(1L, "John Doe", "john.doe@example.com"),
            new User(2L, "Jane Doe", "jane.doe@example.com")
        );
        when(userService.findAll()).thenReturn(users);
        mockMvc.perform(get("/api/users"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)));
    }
}
```

--

### Bonnes pratiques pour les tests d'API REST

- **Tester les endpoints** : Vérifiez que les endpoints répondent bien avec les bons codes de statut.
- **Tester les validations** : Vérifier que la validations des données fonctionne correctement (``@Valid``)
- **Tester les exceptions** : Vérifiez que les exceptions sont gérées correctement et que les réponses d'erreur sont appropriées.
- **Test d'intégration** : Utilisez ``@SpringBootTest`` pour tester l'intégration de l'API avec le reste

--

# TP5 :  

Spring-Web

![](./img/diapo_formation_spring_boot_12.png)