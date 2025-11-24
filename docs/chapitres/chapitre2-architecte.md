# Spring-Boot :  

# Le grand architecte 

--
# La configuration :
# un point sensible

* Avant Spring\-Boot\, chaque module Spring s’ajoutait et se configurait isolément 

* C’est le développeur qui devait gérer l’articulation entre eux 

* Pourtant la structure des applications se ressemble : BDD\, test\, API\, properties\, etc\.

--
# La configuration :
# un point sensible

* La création et configuration du projet a toujours été un sujet sensible 

* Crainte de faire des erreurs qui seraient difficiles à corriger une fois le projet avancé



--
# + d’applis  -  grosses

* Evolution vers la réduction de la taille des applications 

* Besoin de pouvoir démarrer rapidement un projet 

* Besoin de standardiser la configuration et la structure des projets 

* Besoin renforcé avec l’apparition de l’architecture en micro\-service \! 



--
# Le choix de l’implicite

* Spring Boot a pour objectif de configurer une application avec un code minimaliste 

* Un maximum de configurations sont déduites par défaut des dépendances ou des properties 

* Ce mécanisme se nomme l’auto\-configuration 

* Les configurations par défaut sont modifiables via un grand nombre de propriétés décrites dans la doc\. 