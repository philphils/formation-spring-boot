
# Rappel : Le noyau Spring 

--
# Au début était la dépendance…

* Spring s’est d’abord construit autour d’un principe : l’injection de dépendances 

* L’objectif est d’améliorer la qualité du code applicatif 

* Rendre les différentes parties de l’application moins adhérantes entre elles 

* Avoir une application plus modulable\, et donc améliorer la maintenabilité 

--
# Injection de dépendance

* L’utilisation de Spring permet de préserver l’abstraction. Ex :

```java
public ReservationSalleServiceImpl() {
    this.reservationSalleDao = new ReservationSalleDaoImpl();
}
```

* Devient :

```java
public ReservationSalleServiceImpl(
    ReservationSalleDao reservationSalleDao) {
    this.reservationSalleDao = reservationSalleDao;
}
```
--
# Injection de dépendance

*  C’est Spring qui se charge de gérer la création des instances de composants et des liens interface/implémentation 
*  Il les injecte ensuite dans les classes qui les utilisent\, par exemple au niveau de l’argument des constructeurs 
*  On réduit ainsi l’adhérence entre les différentes couches applicatives 

--
# La création des beans

* Les instances des composants peuvent être créées via annotations sur des classes : 
```java
@Service
public class ReservationSalleServiceImpl 
      implements ReservationSalleService {...}

```

* Différentes annotations permettent de typer les composants :     `@Service`, `@Controller`, `@Repository`, `@Configuration`

--
# La création des beans

* Ou au niveau méthode : 
```java
@Bean
public IReservationSalleService reservationSalleService() { 
  …
  return (IReservationSalleService) reservationSalleServiceImpl;
}
```

* Les applications plus anciennes utilisent des fichiers XML 

--
# Le container

* Les instances de composants sont appelées des __« beans »__ 

* Elles sont stockés dans un objet appelé le __« container »__ IoC (Inversion of Control) 

* L’interface qui lui est dédié est l’`ApplicationContext`

* Le développeur peut configurer les beans : durée de vie (scope)\, configuration à la création\, à la destruction\.\.\. (cf. Formation Spring Initiation)

--
# Inversion of Control

* Le développeur « passe la main » à Spring pour le cycle de vie des composants 

* Spring définit un « cadre de travail » (framework) dans lequel s’insère le code applicatif 

* Spring peut alors modifier les composants à la création ou au runtime

--
# Inversion of Control

* De nombreuses fonctionnalités deviennent disponibles : gestion des properties\, transaction\, persistence\, API… 

* Une nouvelle galaxie est née \! 

![](./img/diapo_formation_spring_boot_1.png)



--
# Spring galaxy

![](./img/diapo_formation_spring_boot_2.png) <!-- .element: class="image-large" -->