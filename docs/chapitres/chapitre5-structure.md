# Structure et configuration
# d’un projet Spring Boot 

--
# Structure type Spring Boot

![](./img/diapo_formation_spring_boot_6.png) <!-- .element: class="image-large" -->

--
# La classe Application

* On retrouve le découpage en couches classique : controller\, repository\, model… 

* La particularité est la classe `***Application` :

```java
@SpringBootApplication
public class DemoApplication {
      public static void main(String[] args) {
            SpringApplication.run(DemoApplication.class, args);
      }
}
```

--
# La classe Application

* Il s’agit d’un bean Spring permettant de lancer l’application 

* L’annotation     `@SpringBootApplication`     combine les annotations :     `@Configuration`    \,     `@EnableAutoConfiguration`     et     `@ComponentScan` 

--
# Les fichiers application-*.properties

* Les properties doivent suivre le pattern     application\-\*\.properties     pour être chargés automatiquement 

* Le fichier     application\.properties     est chargé par défaut 

* Les autres fichiers sont chargés ou non selon les profiles activés 

* On verra dans la partie suivante la gestion des profiles et properties 

--
# TP1 : Créer un projet Spring-Boot

![](./img/diapo_formation_spring_boot_7.png)

(Consignes dans le readme de la branche TP1)