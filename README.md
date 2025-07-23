# Projet de Détection du Diabète - Mediscreen

Ce projet est une application web basée sur une architecture microservices conçue pour aider les praticiens à évaluer le risque de diabète de type 2 chez leurs patients.

## Table des Matières
1.  [Architecture](#1-architecture)
2.  [Technologies Utilisées](#2-technologies-utilisées)
3.  [Prérequis](#3-prérequis)
4.  [Guide de Démarrage Rapide](#4-guide-de-démarrage-rapide)
    - [Avec Docker Compose (Méthode recommandée)](#avec-docker-compose-méthode-recommandée)
    - [Manuellement](#manuellement)
5.  [Annexe : Recommandations Green Code](#5-annexe--recommandations-green-code)
    - [Qu'est-ce que le Green Code ?](#quest-ce-que-le-green-code-)
    - [Comment identifier les zones de surconsommation ?](#comment-identifier-les-zones-de-surconsommation-)
    - [Pistes d'amélioration "Green" pour ce projet](#pistes-damélioration-green-pour-ce-projet)

---

## 1. Architecture

L'application est décomposée en plusieurs microservices, chacun ayant une responsabilité unique :

-   **`gateway-service`** : Point d'entrée unique de l'application (API Gateway) basé sur Spring Cloud Gateway. Il redirige les requêtes vers les services appropriés.
-   **`patient-service`** : Microservice back-end gérant les informations des patients (CRUD). Il utilise une base de données relationnelle (MySQL) pour garantir l'intégrité des données.
-   **`notes-service`** : Microservice back-end pour la gestion de l'historique des notes des praticiens. Il utilise une base de données NoSQL (MongoDB) pour stocker de manière flexible des données textuelles non structurées.
-   **`report-service`** : Microservice back-end qui ne possède pas de base de données. Il orchestre les appels aux services `patient` et `notes` pour calculer et générer un rapport d'évaluation du risque de diabète.
-   **`frontend-ui-service`** : Microservice front-end qui fournit l'interface utilisateur web. Il est basé sur Spring Boot et Thymeleaf.

<!-- Vous pouvez générer un diagramme simple avec des outils comme diagrams.net et héberger l'image -->

---

## 2. Technologies Utilisées

-   **Langage** : Java 17
-   **Framework** : Spring Boot 3.x
-   **Gestion de projet** : Maven
-   **Microservices** :
    -   Spring Web
    -   Spring Cloud Gateway
    -   Spring Cloud OpenFeign
-   **Persistance des données** :
    -   Spring Data JPA (avec Hibernate)
    -   Spring Data MongoDB
    -   Base de données SQL : MySQL
    -   Base de données NoSQL : MongoDB
-   **Sécurité** : Spring Security (Authentification HTTP Basic)
-   **Front-end** : Thymeleaf, Bootstrap 5
-   **Conteneurisation** : Docker, Docker Compose

---

## 3. Prérequis

-   [JDK 17](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html) ou supérieur
-   [Maven 3.6+](https://maven.apache.org/download.cgi)
-   [Docker](https://www.docker.com/products/docker-desktop) et Docker Compose

---

## 4. Guide de Démarrage Rapide

### Avec Docker Compose (Méthode recommandée)
La méthode la plus simple pour lancer l'ensemble de l'écosystème, y compris les bases de données.

1.  **Clonez le repository :**
    ```bash
    git clone https://github.com/votre-nom/diabete-detection-app.git
    cd diabete-detection-app
    ```

2.  **Compilez tous les modules avec Maven :**
    Cette étape est nécessaire pour créer les fichiers `.jar` que Docker utilisera.
    ```bash
    mvn clean package
    ```

3.  **Lancez l'ensemble des services avec Docker Compose :**
    ```bash
    docker-compose up -d
    ```

4.  **Accédez à l'application :**
    -   **Interface Utilisateur** : [http://localhost:9000/patients](http://localhost:9000/patients)
    -   **phpMyAdmin (pour la BDD patient)** : [http://localhost:8088](http://localhost:8088)
    -   **Mongo Express (pour la BDD notes)** : [http://localhost:9091](http://localhost:9091)

### Manuellement
Si vous ne souhaitez pas utiliser Docker, vous pouvez lancer chaque service individuellement.

1.  Démarrez vos instances locales de MySQL et MongoDB.
2.  Exécutez les scripts SQL/JS pour peupler les bases de données.
3.  Lancez chaque microservice dans un terminal séparé depuis sa racine respective :

    Pour `patient-service` :
    ```bash
    cd patient-service
    mvn spring-boot:run
    ```
    Répétez pour `notes-service`, `report-service`, `gateway-service`, et `frontend-ui-service`.

---

## 5. Annexe : Recommandations Green Code

### Qu'est-ce que le Green Code ?

Le "Green Code" (ou écoconception logicielle) est une approche de développement qui vise à créer des applications minimisant leur consommation de ressources (CPU, RAM, réseau, stockage) tout au long de leur cycle de vie. L'objectif principal est de réduire l'empreinte environnementale du numérique. En pratique, cela se traduit souvent par des applications plus performantes, plus rapides et moins coûteuses à héberger et à maintenir.

### Comment identifier les zones de surconsommation ?

Avant d'optimiser, il faut mesurer. Les stratégies pour identifier les points chauds de consommation dans un projet comme celui-ci incluent :

1.  **Profilage de Performance (Profiling)** : Utiliser des outils comme **VisualVM** (inclus dans le JDK) ou **JProfiler** pour analyser l'utilisation du CPU et de la mémoire par chaque microservice. Cela permet de repérer les méthodes "gourmandes" et les fuites mémoire potentielles.
2.  **Analyse des Requêtes de Base de Données** : Activer les logs de Spring Data/Hibernate (`show-sql=true`) pour visualiser les requêtes générées. Des requêtes multiples inutiles (problème N+1) sont une source majeure de latence et de consommation.
3.  **Monitoring des Conteneurs** : Utiliser des outils comme `docker stats` ou des plateformes plus avancées (Prometheus/Grafana) pour suivre la consommation des ressources (CPU, RAM) de nos conteneurs Docker dans le temps.

### Pistes d'amélioration "Green" pour ce projet

Voici une liste d'actions concrètes qui pourraient être menées pour rendre cette application plus sobre et efficace.

#### 1. Optimisation du code et des algorithmes
-   **Requêtes de base de données :**
    -   **Pagination :** Sur la page listant tous les patients, nous chargeons actuellement l'intégralité de la base. Implémenter la pagination avec `PagingAndSortingRepository` réduirait drastiquement la charge sur la base de données et la consommation mémoire du `patient-service`.
    -   **Indexation :** Assurer que des index sont en place sur les clés étrangères et les champs fréquemment interrogés (ex: `patientId` dans la collection `notes`).
-   **Algorithme du rapport de diabète :**
    -   **Mise en cache du patient :** Le `report-service` appelle systématiquement le `patient-service` pour les détails du patient. On pourrait implémenter un cache (avec Caffeine ou Redis) dans le `report-service` pour éviter des appels réseau répétitifs pour le même patient dans un court intervalle.
    -   **Optimisation de la recherche de déclencheurs :** L'algorithme actuel concatène toutes les notes. Pour de très grands volumes de notes, des approches plus performantes (comme l'utilisation d'expressions régulières compilées une seule fois) pourraient réduire la charge CPU.

#### 2. Architecture et Infrastructure
-   **Images Docker légères :**
    -   Utiliser des images de base minimalistes comme `eclipse-temurin:17-jre-alpine` au lieu des versions standard.
    -   Appliquer systématiquement la construction multi-étapes (`multi-stage build`) dans nos `Dockerfile` pour ne conserver que l'artefact compilé, ce qui peut réduire la taille de l'image finale de plus de 50%.
-   **Communication Asynchrone :** Pour des tâches futures qui n'exigent pas une réponse immédiate, l'utilisation d'un bus de messages (comme RabbitMQ) au lieu d'appels REST synchrones pourrait lisser la charge et rendre le système plus résilient et moins consommateur en pics de ressources.
-   **Arrêt des services inutilisés :** En environnement de développement ou de test, mettre en place des scripts pour arrêter les services non essentiels pourrait réduire la consommation globale.

#### 3. Gestion des Données
-   **Politique de cycle de vie des données :** Définir une politique pour archiver ou purger les notes des patients après une certaine période (ex: 10 ans) afin de ne pas stocker et traiter indéfiniment des données devenues obsolètes.
-   **Nettoyage des données :** Le stockage de données redondantes ou inutiles augmente les besoins en stockage et ralentit les requêtes. Des routines de nettoyage pourraient être envisagées.

#### 4. Front-end
-   **Optimisation des ressources statiques :** Minifier les fichiers CSS et JavaScript et compresser les images (si nous en avions) réduirait la bande passante nécessaire et accélérerait le chargement des pages.
-   **Mise en cache côté client :** Utiliser les en-têtes HTTP de cache (`Cache-Control`, `ETag`) pour que le navigateur de l'utilisateur ne télécharge pas les ressources statiques à chaque visite.