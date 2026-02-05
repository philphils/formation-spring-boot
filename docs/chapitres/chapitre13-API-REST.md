
# Principes d'architecture 
# des API REST

--

## Introduction : L'approche API "Headless"

- Dans une architecture avec **React**, Spring Boot ne sert pas de moteur de rendu (pas de JSP/Thymeleaf).
- L'application devient un **fournisseur de ressources** via des services REST.
- Les échanges se font exclusivement en **JSON**.
- Le protocole HTTP est utilisé de manière normalisée pour piloter les données.

--

## Pourquoi l'architecture REST ?

- Théorisée par Roy Fielding pour répondre aux défis de croissance du Web moderne :

    - **Scalabilité** (Montée en charge) : En étant "sans état" (Stateless), le serveur peut traiter des millions de requêtes sans saturer sa mémoire vive par des sessions utilisateurs.
    - **Standardisation** : Au lieu d'inventer des protocoles complexes (comme SOAP), REST réutilise les fondations du Web : le protocole HTTP, les URL et les codes de statut.

--

## Pourquoi l'architecture REST ?
-
    - **Indépendance Client/Serveur** : Permet de faire évoluer le backend (Spring) et le frontend (React) séparément, tant que le contrat de l'API (le JSON) ne change pas.
    - **Interopérabilité** : Le format JSON est universel. Une API REST peut être consommée aussi bien par un navigateur, une application mobile ou un autre serveur.

--

## Principes de l'API REST

- **Ressources** : Tout objet métier est une ressource accessible via une URI (ex: `/api/users`).
- **Verbes HTTP** : On utilise les 5 méthodes standards pour définir l'action :
    - `GET` : Récupérer une ressource ou une collection.
    - `POST` : Créer une nouvelle ressource.
    - `PUT` : Mettre à jour complètement.
    - `PATCH` : Mettre à jour partiellement.
    - `DELETE` : Supprimer.
- **Stateless** : Le serveur ne stocke pas l'état du client

--

## Les codes de retour HTTP
## dans une API REST

- Les codes de retour HTTP sont essentiels pour communiquer le résultat d'une requête dans une API REST
- Ils permettent aux clients de comprendre si la requête a réussi, échoué, ou nécessite une action supplémentaire.
- Les API REST réutilisent les codes retour définis par le protocole HTTP

--

### Codes de retour successfull

| Code | Description | Utilisation |
|------|-------------|-------------|
| **200 OK** | La requête a réussi | Réponse standard pour les requêtes GET réussies |
| **201 Created** | Ressource créée avec succès | Réponse pour les requêtes POST réussies |
| **204 No Content** | La requête a réussi, mais aucune donnée n'est retournée | Réponse pour les requêtes DELETE ou PUT réussies |

--

### Codes de retour d'échec

| Code | Description | Utilisation |
|------|-------------|-------------|
| **400 Bad Request** | La requête est malformée ou incomplète | Erreur due à des données invalides envoyées par le client |
| **401 Unauthorized** | Authentification requise | Le client doit s'authentifier pour accéder à la ressource |
| **403 Forbidden** | Accès refusé | Le client est authentifié mais n'a pas les droits nécessaires |
| **404 Not Found** | Ressource introuvable | La ressource demandée n'existe pas |
| **500 Internal Server Error** | Erreur interne du serveur | Une erreur inattendue s'est produite sur le serveur |


--

### Bonnes pratiques pour les codes de retour

- **Utilisez les codes de retour appropriés** : Choisissez le code qui correspond le mieux à la situation.
- **Documentez les codes de retour** : Indiquez dans la documentation de votre API les codes de retour possibles pour chaque endpoint.
- **Retournez des messages d'erreur clairs** : Accompagnez les codes d'erreur de messages explicites pour aider les clients à comprendre et corriger les problèmes.