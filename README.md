# TP1 : Créer un projet Spring-Boot

## 1 : Récupérer Spring Tool Suite en version stand-alone, ou installer le plugin dans Eclipse :

Il est aussi possible d'utiliser Spring-Initializr directement si vous utilisez un autre IDE (https://start.spring.io/)

## 2 : Lancer la création du projet :

Faire New Project et sélectionner "Spring Boot -> Spring Starter Project"

## 3 : Configurer votre projet :

Choisir le gestionnaire de dépendances Maven, un packaging de type war, renseigner le nom du projet, son groupID maven, la version 21 de Java, ainsi que quelques mots de description  

Next  
                                                                   
## 4 : Sélectionner les dépendances :

Nous allons pour l'instant sélectionner les dépendances minimums puis nous ajouterons les dépendances au fur et à mesure des exos  

Pour l'instant sélectionner : Lombok, H2, et le starter Spring Web  
                                                                   
## 5 : Observer la structure de votre projet :

Sa classe Application, le fichier application.propertie, la classe de Test etc.   
                                                                   
## 6 : Lancer votre projet avec la fonction proposé par Spring STS

Run AS -> Spring Boot App

Constater dans la log les informations données par Spring Boot et le bon démarrage du serveur Tomcat embarqué  

Observer la fenêtre Boot Dashboard

Faire clic-droit sur votre application -> Open in Web Browser

Constater l'erreur "Whitelabel Error Page" pour l'adresse localhost:8080 (rien n'a été défini pour l'instant pour l'url d'accueil)  

## 7 : Ajouter une classe de controller :

Créer un package "controller"

Ajouter la classe suivante (nous expliquerons plus en détail dans la partie Spring Boot Web) :

	@RestController
	public class HelloController {
	
		@GetMapping("hello")
		public String hello() {
			return "Helloooo !!!";
		}
	
	}

Se connecter à l'adresse localhost:8080/hello et vérifier que le message s'affiche bien

Vous avez une application web prête à être développée !

## 8 : Ajouter les dev tools :

Faire clic-droit sur le projet -> Spring -> Add devtool

Redémarrer votre serveur

## 9 : Ajouter un 2ème controller sans redémarrer le serveur :

Sans relancer votre serveur créer une seconde classe de controller Hello2Controller avec le code suivant :

	@RestController
	public class Hello2Controller {
	
		@GetMapping("hello2")
		public String hello() {
			return "Helloooo 2 !!!";
		}
	
	}

Connecter vous à l'adresse localhost:8080/hello2

Vérifier que vous obtenez bien le message "Helloooo 2 !!!"

Vous pouvez donc modifier votre application sans avoir à redémarrer le serveur, vos changements (si ils ne sont pas trop impactants) sont pris en compte à la volée !

