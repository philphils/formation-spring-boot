# TP1 : Créer un projet Spring-Boot

## 1 : Récupérer Spring Tool Suite en version stand-alone, installer le plugin dans Eclipse, ou bien installer Spring Extension Pack dans VS Code :

Il est aussi possible d'utiliser Spring-Initializr directement si vous utilisez un autre IDE (https://start.spring.io/)

### Avec Eclipse et Spring Tools 4 (STS Plugin) :
Deux options :
   - **Spring Tool Suite (stand-alone)** : version complète d'Eclipse avec Spring Tools préinstallés
   - **Plugin pour Eclipse existant** : dans Eclipse, aller à Help -> Eclipse Marketplace, chercher "Spring Tools 4" et l'installer

### Avec VS Code et Spring Extension Pack :
1. Ouvrir VS Code et aller dans l'onglet Extensions (Ctrl+Shift+X)
2. Rechercher "Spring Extension Pack" et l'installer
3. Cette extension pack inclut : Spring Boot Extension Pack, Spring Boot Dashboard, etc.

## 2 : Lancer la création du projet :

### Avec Eclipse et Spring Tools 4 :
1. Aller à File -> New -> Other
2. Chercher "Spring Starter Project" et le sélectionner
3. Cliquer sur Next pour accéder à l'assistant de création

### Avec VS Code et Spring Extension Pack :
1. Ouvrir la palette de commandes (Ctrl+Shift+P)
2. Taper "Spring Initializr: Create a maven project" et appuyer sur Entrée
3. Suivre l'assistant de création qui s'ouvre

## 3 : Configurer votre projet :

### Avec Eclipse et Spring Tools 4 :
1. **Name** : choisir un nom explicite (ex: `formation-spring-boot`)
2. **Group** : saisir le groupe Maven (ex: `com.example`)
3. **Artifact** : correspond généralement au nom du projet
4. **Package name** : auto-rempli à partir de Group et Artifact
5. **Type** : sélectionner "Maven"
6. **Packaging** : sélectionner "War"
7. **Java Version** : sélectionner "21"
8. **Language** : "Java"
9. **Description** : ajouter une description du projet
10. Cliquer sur "Next >"

### Avec VS Code et Spring Extension Pack :
1. **Nom du projet** : choisir un nom explicite (ex: `formation-spring-boot`)
2. **Group ID** : saisir le groupe Maven (ex: `com.example`)
3. **Artifact ID** : correspond généralement au nom du projet
4. **Language** : sélectionner "Java"
5. **Package type** : choisir "War"
6. **Packaging** : sélectionner "Maven"
7. **Java Version** : sélectionner "21"
8. **Description** : ajouter une description du projet
9. Valider et sélectionner le répertoire de destination  
                                                                   
## 4 : Sélectionner les dépendances :
                                                                 
### Avec Eclipse et Spring Tools 4 :
1. Sur la page "New Spring Starter Project", cocher les dépendances suivantes dans la liste :
   - **Lombok** : pour réduire le code boilerplate
   - **H2 Database** : pour la base de données embarquée
   - **Spring Web** : pour les fonctionnalités web de Spring Boot
2. Utiliser le champ de recherche en haut si nécessaire pour trouver les dépendances
3. Cliquer sur "Finish" pour créer le projet
4. Le projet s'ouvre automatiquement dans Eclipse

### Avec VS Code et Spring Extension Pack :
1. Lors de la création du projet, à l'étape "Select dependencies", cocher les dépendances suivantes :
   - **Lombok** : pour réduire le code boilerplate
   - **H2 Database** : pour la base de données embarquée
   - **Spring Web** : pour les fonctionnalités web de Spring Boot
2. Poursuivre la création du projet  
                                                                   
## 5 : Observer la structure de votre projet :
                                                                   
### Avec Eclipse et Spring Tools 4 :
1. Le projet s'ouvre automatiquement dans l'explorateur Eclipse (Package Explorer ou Project Explorer)
2. Explorez la structure du projet en cliquant sur les flèches pour développer les dossiers
3. Observez les fichiers :
   - `src/main/java/com/example` : contient la classe `Application.java`
   - `src/main/resources` : contient `application.properties`
   - `src/test/java` : contient les classes de test
   - `pom.xml` : fichier de configuration Maven
4. Double-cliquez sur `Application.java` pour l'ouvrir dans l'éditeur

### Avec VS Code et Spring Extension Pack :
1. Le projet s'ouvre automatiquement dans VS Code
2. Explorez les fichiers dans l'explorateur (Ctrl+B ou icône Explorer)
3. Observez la structure :
   - `src/main/java/com/example` : contient la classe `Application.java`
   - `src/main/resources` : contient `application.properties`
   - `src/test/java` : contient les classes de test
   - `pom.xml` : fichier de configuration Maven   
                                                                   
## 6 : Lancer votre projet avec la fonction proposé par Spring STS

### Avec Eclipse et Spring Tools 4 :
1. Clic-droit sur le nom du projet dans le Project Explorer
2. Sélectionner "Run As" -> "Spring Boot App"
3. L'application démarre et les logs s'affichent dans la console Eclipse
4. Vérifier le message `Tomcat started on port(s): 8080` dans la console
5. Observer le panel "Boot Dashboard" (onglet en bas) qui affiche votre application en cours d'exécution
6. Faire clic-droit sur l'application dans le Boot Dashboard -> "Open in Web Browser"
7. Accéder à http://localhost:8080
8. Constater l'erreur "Whitelabel Error Page" (comportement normal, aucune route définie)

### Avec VS Code et Spring Extension Pack :
1. Ouvrir le panel "Spring Boot Dashboard" dans la barre latérale (icône Spring)
2. Cliquer sur le bouton "Run" (play) à côté de votre application pour la démarrer
3. Vérifier dans le terminal intégré les logs de démarrage de Spring Boot
4. Constater le message `Tomcat started on port(s): 8080`
5. Ouvrir un navigateur et accéder à http://localhost:8080
6. Constater l'erreur "Whitelabel Error Page" (comportement normal, aucune route définie)  

## 7 : Ajouter une classe de controller :

### Avec Eclipse et Spring Tools 4 :
1. Dans le Project Explorer, faire clic-droit sur `src/main/java/com/example`
2. Sélectionner "New" -> "Package"
3. Nommer le package `com.example.controller` et cliquer "Finish"
4. Faire clic-droit sur le package `com.example.controller`
5. Sélectionner "New" -> "Class"
6. Nommer la classe `HelloController` et cliquer "Finish"
7. Ajouter le code suivant :
   ```java
   package com.example.controller;
   
   import org.springframework.web.bind.annotation.GetMapping;
   import org.springframework.web.bind.annotation.RestController;
   
   @RestController
   public class HelloController {
   
       @GetMapping("hello")
       public String hello() {
           return "Helloooo !!!";
       }
   
   }
   ```
8. Sauvegarder le fichier (Ctrl+S)
9. Grâce à Spring Reload, le serveur se redémarrera automatiquement
10. Accéder à http://localhost:8080/hello et vérifier que le message s'affiche

Vous avez une application web prête à être développée !

### Avec VS Code et Spring Extension Pack :
1. Dans l'explorateur, aller dans `src/main/java/com/example`
2. Créer un nouveau dossier "controller" (clic-droit -> New Folder)
3. Créer un nouveau fichier `HelloController.java` dans ce dossier
4. Ajouter le code suivant :
   ```java
   package com.example.controller;
   
   import org.springframework.web.bind.annotation.GetMapping;
   import org.springframework.web.bind.annotation.RestController;
   
   @RestController
   public class HelloController {
   
       @GetMapping("hello")
       public String hello() {
           return "Helloooo !!!";
       }
   
   }
   ```
5. Sauvegarder le fichier (Ctrl+S)
6. Grâce à Spring Reload, le serveur se redémarrera automatiquement
7. Accéder à http://localhost:8080/hello et vérifier que le message s'affiche

Vous avez une application web prête à être développée !

## 8 : Ajouter les dev tools :

### Avec Eclipse et Spring Tools 4 :
1. Clic-droit sur le projet dans le Project Explorer
2. Sélectionner "Spring" -> "Add Spring DevTools"
3. Les dépendances sont automatiquement ajoutées au `pom.xml`
4. Arrêter l'application : clic-droit dans le Boot Dashboard -> "Stop"
5. Redémarrer l'application : clic-droit dans le Boot Dashboard -> "Run" ou "Run As" -> "Spring Boot App"
6. Vous verrez dans la console : `LiveReload server is running on port 35729`

### Avec VS Code et Spring Extension Pack :
1. Ouvrir le fichier `pom.xml`
2. Trouver la section `<dependencies>` et ajouter manuellement la dépendance :
   ```xml
   <dependency>
       <groupId>org.springframework.boot</groupId>
       <artifactId>spring-boot-devtools</artifactId>
       <scope>runtime</scope>
       <optional>true</optional>
   </dependency>
   ```
3. Sauvegarder le fichier (Ctrl+S)
4. VS Code détectera les changements et les téléchargera automatiquement
5. Redémarrer manuellement le serveur ou attendre que la détection se fasse
   - Arrêter : clic sur le bouton "Stop" dans le Spring Boot Dashboard
   - Relancer : clic sur le bouton "Run"
  
**Note :** Pour des raisons de sécurité les devTools sont exclus par défaut du war produit par Spring-Boot. Laisser `<optional>true</optional>` pour plus de sécurité.

## 9 : Ajouter un 2ème controller sans redémarrer le serveur :

### Avec Eclipse et Spring Tools 4 :
1. Sans arrêter l'application, faire clic-droit sur le package `com.example.controller`
2. Sélectionner "New" -> "Class"
3. Nommer la classe `Hello2Controller` et cliquer "Finish"
4. Ajouter le code suivant :
   ```java
   package com.example.controller;
   
   import org.springframework.web.bind.annotation.GetMapping;
   import org.springframework.web.bind.annotation.RestController;
   
   @RestController
   public class Hello2Controller {
   
       @GetMapping("hello2")
       public String hello() {
           return "Helloooo 2 !!!";
       }
   
   }
   ```
5. Sauvegarder le fichier (Ctrl+S)
6. Observez dans la console le message de rechargement automatique
7. Accéder à http://localhost:8080/hello2 et vérifier que le message s'affiche correctement
8. Vous pouvez donc modifier votre application sans avoir à redémarrer le serveur manuellement, vos changements sont pris en compte à la volée (tant qu'ils ne sont pas trop impactants) !

### Avec VS Code et Spring Extension Pack :
1. Sans redémarrer le serveur, créer un nouveau fichier `Hello2Controller.java` dans le dossier `src/main/java/com/example/controller`
2. Ajouter le code suivant :
   ```java
   package com.example.controller;
   
   import org.springframework.web.bind.annotation.GetMapping;
   import org.springframework.web.bind.annotation.RestController;
   
   @RestController
   public class Hello2Controller {
   
       @GetMapping("hello2")
       public String hello() {
           return "Helloooo 2 !!!";
       }
   
   }
   ```
3. Sauvegarder le fichier (Ctrl+S)
4. Grâce aux DevTools et Spring Reload, le serveur se redémarrera automatiquement en arrière-plan
5. Accéder à http://localhost:8080/hello2 et vérifier que le message s'affiche correctement
6. Vous pouvez donc modifier votre application sans avoir à redémarrer le serveur manuellement, vos changements sont pris en compte à la volée (tant qu'ils ne sont pas trop impactants) !
