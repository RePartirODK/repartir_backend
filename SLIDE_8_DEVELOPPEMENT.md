# Slide 8: Développement

## 🔄 Méthodologie de Développement

### Approche
- **Architecture en couches** : Séparation claire des responsabilités
  - Controllers → Services → Repositories → Entities
- **Pattern REST** : API RESTful standardisée
- **Développement itératif** : Fonctionnalités développées par modules
- **Test-driven** : Tests unitaires et d'intégration (Spring Boot Test)

### Pratiques
- **Code propre** : Nomenclature claire, commentaires documentaires
- **Gestion des erreurs** : Handler global d'exceptions (`GlobalExceptionHandler`)
- **Validation** : Validation des données en entrée (Jakarta Bean Validation)
- **Documentation** : Swagger/OpenAPI intégré pour documentation automatique

---

## 💻 Stack Technique

### Langage
- **Java 21** (LTS)
  - Records, Pattern Matching, Sealed Classes
  - Features modernes Java

### Framework Principal
- **Spring Boot 3.5.6**
  - Auto-configuration
  - Serveur embarqué (Tomcat)
  - Injection de dépendances

### Persistance & Base de Données
- **Spring Data JPA**
  - Abstraction des repositories
  - Queries par convention
- **Hibernate** (ORM)
  - Mapping objet-relationnel
  - Génération automatique du schéma
- **MySQL 8.0+**
  - Base de données relationnelle
  - Driver : MySQL Connector/J

### Communication
- **Spring Web** (REST API)
  - Contrôleurs REST
  - Sérialisation JSON (Jackson)
- **Spring WebSocket** + **STOMP**
  - Communication temps réel
  - Chat en temps réel

### Email
- **Spring Mail**
  - Envoi d'emails transactionnels
  - SMTP avec TLS
  - Templates HTML

### Documentation API
- **SpringDoc OpenAPI 2.8.13**
  - Documentation interactive (Swagger UI)
  - Génération automatique
  - URL : `/swagger-ui/index.html`

### Utilitaires
- **Lombok**
  - Réduction du code boilerplate
  - Annotations : `@Getter`, `@Setter`, `@RequiredArgsConstructor`
- **Jackson**
  - Sérialisation/Désérialisation JSON
  - Support JSR310 (LocalDateTime, etc.)

### Build & Gestion de Dépendances
- **Maven**
  - Gestion des dépendances
  - Build et packaging
  - Plugins : Spring Boot Maven Plugin

### Développement
- **Spring Boot DevTools**
  - Rechargement automatique
  - Configuration conditionnelle

---

## 🔐 Méthodes de Sécurité

### Authentification
- **JWT (JSON Web Token)**
  - Bibliothèque : `jjwt` v0.13.0
  - Algorithme : HS256
  - Access Token : Expiration 30 minutes
  - Refresh Token : Stocké en base de données
  - Validation via `JwtAuthFilter` sur chaque requête

### Autorisation
- **Spring Security 6.x**
  - Filtres de sécurité
  - Gestion des rôles
- **RBAC (Role-Based Access Control)**
  - 6 rôles : ADMIN, JEUNE, MENTOR, PARRAIN, CENTRE, ENTREPRISE
  - Protection des endpoints par rôle
  - Sécurité au niveau méthode (`@EnableMethodSecurity`)

### Protection des Mots de Passe
- **BCrypt**
  - Hachage des mots de passe
  - Via `BCryptPasswordEncoder` (10 rounds)
  - Aucun mot de passe stocké en clair

### Sécurité Réseau
- **CORS** configuré
  - Origines autorisées : `localhost:*`, `127.0.0.1:*`, `10.0.2.2:*`
  - Credentials activés
- **CSRF** désactivé (normal pour API REST stateless avec JWT)

### WebSocket Sécurisé
- **JWT dans WebSocket**
  - `JwtAuthChannelInterceptor` pour validation
  - Authentification requise pour connexion WebSocket

### Validation des Données
- **Jakarta Bean Validation**
  - Validation des entrées utilisateur
  - Contrôle des formats et contraintes

**Note** : OAuth2 et Keycloak ne sont **pas utilisés** dans ce projet. L'authentification est gérée via JWT personnalisé.

---

## 🛠️ Outils Utilisés

### IDE & Éditeurs
- **IntelliJ IDEA** ou **Eclipse** (recommandé pour Spring Boot)
- **VS Code** avec extensions Java/Spring

### Base de Données
- **MySQL Workbench** ou **DBeaver**
  - Visualisation et gestion de la base
- **phpMyAdmin** (optionnel)

### API Testing
- **Postman** ou **Insomnia**
  - Test des endpoints REST
  - Test des WebSockets
  - Collections de requêtes

### Documentation
- **Swagger UI** (intégré)
  - Documentation interactive
  - Test des endpoints directement
  - URL : `http://localhost:8183/swagger-ui/index.html`

### Versioning
- **Git**
  - Gestion du code source
  - Collaboration

### Build & Exécution
- **Maven**
  - `mvn clean package` : Build
  - `mvn spring-boot:run` : Exécution
- **JDK 21**
  - Compilation et exécution

### Logging
- **Logback** (intégré Spring Boot)
  - Logs de l'application
  - Configuration dans `application.properties`

### Monitoring (à venir)
- **Spring Boot Actuator** (déjà dans les dépendances)
  - Endpoints de santé
  - Métriques

---

## 📦 Dépendances Principales (Maven)

```xml
Spring Boot 3.5.6
├── spring-boot-starter-web
├── spring-boot-starter-data-jpa
├── spring-boot-starter-security
├── spring-boot-starter-websocket
├── spring-boot-starter-mail
├── spring-boot-starter-validation
├── spring-boot-devtools
└── mysql-connector-j

Sécurité
├── jjwt-api (0.13.0)
├── jjwt-impl (0.13.0)
└── jjwt-jackson (0.13.0)

Documentation
└── springdoc-openapi-starter-webmvc-ui (2.8.13)

Utilitaires
├── lombok
├── jackson-datatype-jsr310
└── spring-boot-starter-test
```

---

## 🏗️ Architecture de Développement

### Structure des Packages
```
com.example.repartir_backend/
├── controllers/    (26 fichiers) - REST + WebSocket
├── services/       (27 fichiers) - Logique métier
├── repositories/   (19 fichiers) - Accès données
├── entities/       (20 fichiers) - Modèle JPA
├── dto/            (42 fichiers) - Transferts de données
├── security/       (4 fichiers)  - Configuration sécurité
├── config/         (4 fichiers)  - Configurations Spring
└── enumerations/   (7 fichiers)  - Enums
```

### Standards de Code
- **Nomenclature** : camelCase pour variables, PascalCase pour classes
- **Annotations** : Lombok pour getters/setters
- **DTOs** : Séparation Request/Response pour toutes les entrées/sorties
- **Transactions** : `@Transactional` pour opérations critiques
- **Documentation** : Javadoc pour méthodes complexes

---

## 📝 Résumé

| Catégorie | Technologies |
|-----------|-------------|
| **Langage** | Java 21 |
| **Framework** | Spring Boot 3.5.6 |
| **Base de données** | MySQL 8.0+ avec JPA/Hibernate |
| **Sécurité** | JWT + Spring Security + BCrypt |
| **API** | REST + WebSocket (STOMP) |
| **Documentation** | Swagger/OpenAPI |
| **Build** | Maven |
| **Test** | Spring Boot Test |

**Méthodologie** : Architecture en couches, RESTful, développement itératif


