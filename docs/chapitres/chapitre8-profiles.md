# Les profiles :
# outils pour le 
# multi-environnement 

--
# A chaque environnement sa config !

* Pour une même application\, on a des environnements d’exécution différents : dev\, qualif\, prod ou test 

* Les configurations sont différentes\. Par ex\. les chaînes de connexion vont changer entre les environnements 

* On peut avoir carrément des composants différents selon les contextes d’exécution

* Ex : base mémoire embarquée pour les tests\, mock pour certaines API en dev et qualif etc\. 

--

# La solution :
# Les profiles

* Spring propose les Profiles pour gérer ces différents environnement et passer simplement de l’un à l’autre 

* Spring Boot va encore plus loin pour faciliter les configurations multi\-environnement 

--
# Des jeux de properties
# par profile

* On va donc définir un Profile pour chaque environnement d’exécution du code applicatif 

* Spring-Boot charge par défaut le fichier `application.properties` situé dans `/resources` 

* On pourra ensuite surcharger les properties par défaut ou en ajouter d’autres avec des fichiers `application-{profile}.properties` 

* Spring chargera le fichier properties correspondant à (ou aux) profile activé (cf suite) 

--
# Rattacher un bean à un `@Profile`

* On pourra rattacher un bean à un Profile avec l’annotation `@Profile( "nomDuProfile" )` 

* Cette annotation peut s’utiliser sur un bean annoté `@Component` ou dérivé, sur un bean     `@Configuration` ou `@ConfigurationProperties`, ou enfin sur une méthode `@Bean` qui produit un bean 

* Le bean ne sera créé que si le profile mentionné par l’annotation est activé 

--
# Activation des profiles

* Les IDE permettent d'activer les profiles en local simplement en général via le dashboard Spring-Boot

* Spring Boot permet d’activer les profiles avec la property : `spring.profiles.active` 

* On peut la définir dans un fichier properties (peu d’intérêt), ou encore en variable d’environnement ou en ligne de commande 

--
# Activation des profiles

* On peut activer plusieurs profiles en les séparant par une virgule… 

* But stay KISS !!! 😜

* Enfin, pour pouvoir tester ses profiles, ou activer un profile de test, on peut utiliser au sein d’une classe de test l’annotation : `@ActiveProfile( "test" )` 

--

# TP3 :  

Properties & Profiles 

![](./img/diapo_formation_spring_boot_12.png)
