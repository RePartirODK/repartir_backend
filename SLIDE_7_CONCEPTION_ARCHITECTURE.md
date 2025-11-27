# Slide 7: Conception et Architecture
## RePartir Backend - Documentation Technique

---

## 1. Architecture des Données

### 1.1 Modélisation des Données

L'application utilise une architecture relationnelle basée sur **JPA/Hibernate** avec MySQL. Le modèle de données suit un pattern de **composition/héritage** via associations.

#### **Structure Principale des Entités**

##### **Entité Central : Utilisateur**
```java
- id (PK, auto-increment)
- nom, email (unique), téléphone (unique)
- motDePasse (haché avec BCrypt)
- role (enum: ADMIN, JEUNE, MENTOR, PARRAIN, CENTRE, ENTREPRISE)
- etat (enum: EN_ATTENTE, VALIDE, REFUSE, ANNULER, TERMINE, EN_COURS)
- estActive (boolean)
- dateCreation (LocalDateTime)
- urlPhoto
```

##### **Entités Spécialisées (Relation OneToOne avec Utilisateur)**

**1. Jeune**
- Informations spécifiques : age, genre, niveau, a_propos, urlDiplome
- Relations : Mentorings, Paiements, Inscriptions, Candidatures, Parrainages

**2. Mentor**
- Informations : prenom, annee_experience, profession, a_propos
- Relations : Liste de Mentorings

**3. Parrain**
- Informations : prenom, profession
- Relations : Liste de Parrainages

**4. Admin**
- Accès administratif complet

**5. CentreFormation**
- Informations spécifiques au centre
- Relations : Liste de Formations

**6. Entreprise**
- Informations d'entreprise
- Relations : Offres d'emploi

#### **Entités Fonctionnelles**

**Formation**
- titre, description, dates (debut/fin)
- cout, nbre_place, format (enum), duree
- urlFormation, urlCertificat
- Relations : CentreFormation, Inscriptions, Parrainages

**InscriptionFormation**
- Lie Jeune ↔ Formation
- Statut : EN_ATTENTE, VALIDE, REFUSE
- Relations : Liste de Paiements

**Parrainage**
- Lie Jeune ↔ Formation ↔ Parrain (optionnel)
- Permet le financement partiel/total d'une formation

**Paiement**
- montant, reference, date, statut (EN_ATTENTE, VALIDE, REFUSE, A_REMBOURSE, REMBOURSE)
- Relations : Jeune, InscriptionFormation, Parrainage

**Mentoring**
- Relation Mentor ↔ Jeune
- Statut de validation

**Message**
- Chat en temps réel entre Mentor et Jeune
- Stockage persistant des messages

**Notification**
- Système de notifications pour les utilisateurs
- Destinataire lié à Utilisateur

**OffreEmploi & CandidatureOffre**
- Gestion des offres d'emploi par les entreprises
- Candidatures des jeunes

**Domaine & UserDomaine**
- Classification par domaines (compétences/secteurs)
- Relations many-to-many avec utilisateurs

#### **Relations Clés**

```
Utilisateur (1) ←→ (1) Jeune/Mentor/Parrain/Admin/Centre/Entreprise
    ↓
Jeune (1) ←→ (N) Mentoring ←→ (1) Mentor
Jeune (1) ←→ (N) InscriptionFormation ←→ (1) Formation
Jeune (1) ←→ (N) Parrainage ←→ (1) Formation [← (1) Parrain (optionnel)]
Jeune (1) ←→ (N) Paiement ←→ (1) InscriptionFormation [← (1) Parrainage]
Formation (N) ←→ (1) CentreFormation
Entreprise (1) ←→ (N) OffreEmploi
Jeune (N) ←→ (1) CandidatureOffre ←→ (1) OffreEmploi
```

### 1.2 Stockage des Données

#### **Base de Données**
- **SGBD** : MySQL 8.0+
- **Configuration** :
  - URL : `jdbc:mysql://localhost:3306/repartir`
  - Mode DDL : `update` (Hibernate génère/migre automatiquement le schéma)
  - Affichage SQL : Activé en développement (`spring.jpa.show-sql=true`)

#### **Stratégie de Génération des IDs**
- **Type** : `GenerationType.IDENTITY` (auto-increment)
- Toutes les entités utilisent des clés primaires auto-générées

#### **Gestion des Fichiers**
- **Stockage Local** : Fichiers statiques (photos, CV, diplômes) stockés sur le système de fichiers
- **Chemin** : `C:/Users/DELL Latitude/Desktop/uploads/`
- **Serving** : Configuration via `StaticResourceConfiguration` pour servir via HTTP (`/uploads/**`)
- **Limite Upload** : 10MB par fichier

#### **Session & Cache**
- **Session** : Stateless (pas de session serveur, utilisation JWT)
- **Cache** : Aucun cache configuré actuellement

### 1.3 Sécurité des Données

#### **Authentification & Autorisation**

**1. Authentification JWT (JSON Web Token)**
- **Bibliothèque** : `jjwt` v0.13.0
- **Flux** :
  - Login → Génération Access Token + Refresh Token
  - Refresh Token stocké en base (`RefreshToken` entity)
  - Validation du token à chaque requête via `JwtAuthFilter`
  - Support WebSocket via `JwtAuthChannelInterceptor`

**2. Hachage des Mots de Passe**
- **Algorithme** : BCrypt (via Spring Security)
- **Configuration** : BCryptPasswordEncoder (10 rounds par défaut)

**3. Gestion des Rôles (RBAC - Role-Based Access Control)**
```java
Rôles disponibles :
- ADMIN : Accès complet
- JEUNE : Gestion profil, inscriptions, candidatures
- MENTOR : Gestion profil, mentorings, chat
- PARRAIN : Gestion profil, parrainages, paiements
- CENTRE : Gestion formations
- ENTREPRISE : Gestion offres d'emploi
```

**4. Configuration CORS**
- Origines autorisées : `localhost:*`, `127.0.0.1:*`, `10.0.2.2:*`
- Méthodes : POST, GET, PUT, DELETE, PATCH
- Credentials : Activés

**5. Protection CSRF**
- Désactivé (normal pour API REST stateless avec JWT)

#### **Sécurité au Niveau des Endpoints**

```java
Endpoints Publics :
- /api/auth/login
- /api/utilisateurs/register
- /api/auth/refresh
- /api/password/**
- /api/domaines/lister
- /api/centres/**
- /api/formations/**
- /uploads/** (fichiers statiques)
- /swagger-ui/** (documentation)

Endpoints Protégés par Rôle :
- /administrateurs/** → ADMIN
- /api/domaines/** → ADMIN
- /api/entreprise/** → ENTREPRISE
- /api/parrains/** → PARRAIN, ADMIN
- /api/mentors/** → MENTOR, JEUNE, ADMIN
- /api/paiements/** → ENTREPRISE, PARRAIN, JEUNE, ADMIN
- /api/mentoring/** → MENTOR, JEUNE, ADMIN
- etc.
```

#### **Validation des Données**
- **Framework** : Jakarta Bean Validation
- Validation des entrées utilisateur
- Gestion centralisée des exceptions via `GlobalExceptionHandler`

#### **Sécurité des Données Sensibles**

**1. Mots de Passe**
- Jamais stockés en clair
- Hachage BCrypt
- Variables d'environnement pour secrets (`${jwtsecret}`, `${emailpassword}`)

**2. Tokens JWT**
- Secret stocké en variable d'environnement
- Expiration configurée
- Refresh tokens pour renouvellement

**3. Configuration Email**
- Credentials dans variables d'environnement
- SMTP avec TLS (port 587)

### 1.4 Conformité

#### **Gestion des Données Personnelles**
- **Email, Téléphone** : Uniques et validés
- **Photos** : Stockage local avec URLs accessibles
- **État des comptes** : Système de validation/refus avec états clairs

#### **Traçabilité**
- **Dates de création** : Enregistrées (`dateCreation` dans Utilisateur)
- **Historique des paiements** : Dates, références, statuts
- **Notifications** : Traçabilité des actions importantes

#### **Intégrité des Données**
- **Contraintes de clé étrangère** : Gérées par JPA
- **Transactions** : Annotations `@Transactional` pour opérations critiques
- **Validations métier** : Vérifications avant sauvegarde

---

## 2. Technologies Utilisées

### 2.1 Framework Principal

**Spring Boot 3.5.6**
- Framework Java pour applications enterprise
- Auto-configuration
- Embeddable server (Tomcat)
- Actuators pour monitoring

### 2.2 Gestion des Données

**Spring Data JPA**
- Abstraction de l'accès aux données
- Repositories automatiques
- Queries par convention de nommage

**Hibernate (via JPA)**
- ORM (Object-Relational Mapping)
- Génération automatique du schéma (`ddl-auto=update`)
- Gestion des relations et lazy loading

**MySQL Connector/J**
- Driver JDBC pour MySQL
- Support des dernières fonctionnalités MySQL

### 2.3 Sécurité

**Spring Security**
- Framework de sécurité complet
- Filtres de sécurité
- Gestion des rôles et permissions

**JWT (JSON Web Token)**
- `jjwt-api`, `jjwt-impl`, `jjwt-jackson` v0.13.0
- Génération, validation, parsing de tokens

**BCrypt**
- Hachage des mots de passe
- Intégré via Spring Security

### 2.4 Communication

**Spring Web**
- REST API
- Controllers avec annotations
- Support JSON/XML

**Spring WebSocket**
- Communication temps réel
- Protocole STOMP
- Chat en temps réel entre mentor/jeune

### 2.5 Email

**Spring Mail**
- Envoi d'emails transactionnels
- Support SMTP
- Templates HTML

### 2.6 Documentation API

**SpringDoc OpenAPI (Swagger)**
- Documentation interactive
- Interface Swagger UI
- Génération automatique de la documentation

### 2.7 Utilitaires

**Lombok**
- Réduction du code boilerplate
- Annotations : `@Getter`, `@Setter`, `@RequiredArgsConstructor`, etc.

**Jackson**
- Sérialisation/Désérialisation JSON
- Support JSR310 (dates Java 8+)

**Spring Validation**
- Validation des données
- Jakarta Bean Validation

### 2.8 Développement

**Spring Boot DevTools**
- Rechargement automatique en développement
- Configuration conditionnelle

**Maven**
- Gestion des dépendances
- Build et packaging

### 2.9 Langage & Version

- **Java 21** (LTS)
- **Features modernes** : Records, Pattern Matching, etc.

---

## 3. Architecture de l'Application

### 3.1 Architecture en Couches (Layered Architecture)

```
┌─────────────────────────────────────────────────┐
│           COUCHE PRÉSENTATION                    │
│  Controllers (REST + WebSocket)                  │
│  - AuthentificationControllers                   │
│  - JeuneControllers                              │
│  - MentorControllers                             │
│  - PaiementControllers                           │
│  - ChatController (WebSocket)                    │
│  - etc.                                          │
└─────────────────────────────────────────────────┘
                      ↓
┌─────────────────────────────────────────────────┐
│           COUCHE MÉTIER                          │
│  Services (Business Logic)                       │
│  - JeuneServices                                 │
│  - PaiementServices                              │
│  - MentoringServices                             │
│  - MailSendServices                              │
│  - etc.                                          │
└─────────────────────────────────────────────────┘
                      ↓
┌─────────────────────────────────────────────────┐
│           COUCHE PERSISTANCE                     │
│  Repositories (Data Access)                      │
│  - JeuneRepository                               │
│  - PaiementRepository                            │
│  - FormationRepository                           │
│  - etc.                                          │
└─────────────────────────────────────────────────┘
                      ↓
┌─────────────────────────────────────────────────┐
│           COUCHE DONNÉES                         │
│  MySQL Database                                  │
│  - Tables générées par Hibernate                 │
└─────────────────────────────────────────────────┘
```

### 3.2 Structure du Projet

```
src/main/java/com/example/repartir_backend/
├── advice/                    # Gestion centralisée des exceptions
│   └── GlobalExceptionHandler.java
├── components/                # Composants système
│   └── AdminInitializer.java  # Initialisation admin au démarrage
├── config/                    # Configurations Spring
│   ├── SecurityConfig.java
│   ├── SwaggerConfig.java
│   ├── WebSocketConfig.java
│   ├── WebConfig.java
│   └── StaticResourceConfiguration.java
├── controllers/               # Contrôleurs REST (26 fichiers)
│   ├── AuthentificationControllers.java
│   ├── JeuneControllers.java
│   ├── MentorControllers.java
│   ├── PaiementControllers.java
│   ├── ChatController.java (WebSocket)
│   └── ...
├── dto/                       # Data Transfer Objects (42 fichiers)
│   ├── RequestPaiement.java
│   ├── ResponsePaiement.java
│   └── ...
├── entities/                  # Entités JPA (20 fichiers)
│   ├── Utilisateur.java
│   ├── Jeune.java
│   ├── Mentor.java
│   ├── Formation.java
│   └── ...
├── enumerations/              # Enums (7 fichiers)
│   ├── Role.java
│   ├── Etat.java
│   ├── StatutPaiement.java
│   └── ...
├── repositories/              # Repositories JPA (19 fichiers)
│   ├── JeuneRepository.java
│   ├── PaiementRepository.java
│   └── ...
├── security/                  # Sécurité
│   ├── SecurityConfig.java
│   ├── JwtAuthFilter.java
│   ├── JwtServices.java
│   └── JwtAuthChannelInterceptor.java
├── services/                  # Services métier (27 fichiers)
│   ├── JeuneServices.java
│   ├── PaiementServices.java
│   ├── MailSendServices.java
│   └── ...
└── RepartirBackendApplication.java  # Point d'entrée
```

### 3.3 Flux de Données Typique

#### **Exemple : Création d'un Paiement**

```
1. Client → POST /api/paiements
   Headers: Authorization: Bearer <JWT>

2. PaiementControllers.creerPaiement()
   ↓
3. SecurityConfig → JwtAuthFilter
   - Vérifie token JWT
   - Extrait utilisateur authentifié
   ↓
4. PaiementServices.creerPaiement()
   - Validation métier
   - Récupération Jeune, Inscription, Parrainage
   - Création entité Paiement
   ↓
5. PaiementRepository.save()
   ↓
6. Hibernate → MySQL
   - INSERT dans table paiement
   ↓
7. Réponse JSON → Client
```

### 3.4 Communication Temps Réel

#### **WebSocket pour le Chat**

```
Architecture :
- Endpoint STOMP : /ws
- Préfixe application : /app
- Préfixe broker : /topic

Flux :
1. Client connecte via WebSocket (avec JWT)
2. JwtAuthChannelInterceptor valide le token
3. Client envoie message → /app/chat/{mentoringId}
4. ChatController.processMessage()
   - Sauvegarde dans Message entity
   - Broadcast via messagingTemplate
5. Tous les clients abonnés à /topic/chat/{mentoringId} reçoivent
```

### 3.5 Gestion des Erreurs

**GlobalExceptionHandler**
- Capture toutes les exceptions non gérées
- Retourne des réponses JSON standardisées
- Codes HTTP appropriés (400, 401, 403, 404, 500)

---

## 4. Déploiement

### 4.1 Configuration Actuelle

**Environnement de Développement**
- **Port** : 8183
- **URL** : `http://localhost:8183`
- **Base de données** : MySQL locale
- **Mode** : Développement (logging SQL activé)

### 4.2 Variables d'Environnement Requises

```properties
# JWT
jwtsecret=<secret-key-jwt>

# Admin par défaut
defaultadminemail=<email-admin>
defaultadminpassword=<mot-de-passe-admin>

# Email SMTP
emailpassword=<mot-de-passe-email>
```

### 4.3 Build & Packaging

**Maven Build**
```bash
mvn clean package
```

**Résultat** : Fichier JAR exécutable
- Location : `target/repartir_backend-0.0.1-SNAPSHOT.jar`

**Exécution**
```bash
java -jar target/repartir_backend-0.0.1-SNAPSHOT.jar
```

### 4.4 Configuration de Production Recommandée

#### **1. Base de Données**
- MySQL en production (serveur dédié ou cloud)
- Configuration de pool de connexions
- Backups automatiques

#### **2. Variables d'Environnement**
- Utiliser un fichier `.env` ou variables système
- Secrets dans un gestionnaire de secrets (Vault, AWS Secrets Manager)

#### **3. Serveur d'Application**
- **Option 1** : JAR standalone avec Java 21
- **Option 2** : Déploiement dans un serveur d'application (Tomcat, WildFly)
- **Option 3** : Containerisation avec Docker

#### **4. Configuration Docker (Exemple)**

```dockerfile
FROM openjdk:21-jdk-slim
WORKDIR /app
COPY target/repartir_backend-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8183
ENTRYPOINT ["java", "-jar", "app.jar"]
```

#### **5. Reverse Proxy**
- **Nginx** ou **Apache** pour :
  - SSL/TLS termination
  - Load balancing (si plusieurs instances)
  - Gestion du domaine

#### **6. Monitoring & Logging**
- **Actuator** : Endpoints de santé (`/actuator/health`)
- **Logging** : Configuration log4j2 ou Logback
- **Monitoring** : Prometheus, Grafana

#### **7. Sécurité Production**
- HTTPS obligatoire
- CORS restreint aux domaines autorisés
- Rate limiting
- Firewall

### 4.5 Stockage des Fichiers en Production

**Options Recommandées :**
1. **Stockage Cloud** : AWS S3, Azure Blob, Google Cloud Storage
2. **Serveur NFS** : Partage réseau
3. **CDN** : Distribution des fichiers statiques

---

## 5. Présentation des Prototypes

### 5.1 Architecture Fonctionnelle

#### **Flux Utilisateur : Inscription Jeune**

```
1. POST /api/utilisateurs/register
   ↓
2. Création Utilisateur + Jeune
   ↓
3. État : EN_ATTENTE
   ↓
4. Admin valide → État : VALIDE
   ↓
5. Email de confirmation envoyé
```

#### **Flux : Parrainage d'une Formation**

```
1. Jeune s'inscrit à une formation
   → InscriptionFormation (EN_ATTENTE)
   
2. Parrain crée un parrainage
   → POST /api/parrainage
   → Parrainage lié à Jeune + Formation
   
3. Création du paiement
   → POST /api/paiements
   → Statut : EN_ATTENTE
   
4. Admin valide le paiement
   → PUT /api/paiements/{id}/valider
   → Statut : VALIDE
   
5. Si montant suffisant → InscriptionFormation → VALIDE
   → Email de confirmation
   → Décrément places disponibles
```

#### **Flux : Mentoring & Chat**

```
1. Jeune demande un mentorat
   → Création Mentoring (EN_ATTENTE)
   
2. Mentor accepte
   → Mentoring → VALIDE
   
3. Communication via WebSocket
   → Messages en temps réel
   → Persistance en base
```

### 5.2 Diagrammes d'Architecture

#### **Diagramme de Classes Principal**

```
┌─────────────────────┐
│    Utilisateur      │
├─────────────────────┤
│ id, nom, email      │
│ role, etat          │
└──────────┬──────────┘
           │ 1
           │
           │ 1
   ┌───────┴──────────┬──────────┬──────────┐
   │                  │          │          │
┌──▼───┐      ┌──────▼───┐  ┌───▼────┐  ┌──▼──────┐
│Jeune │      │  Mentor  │  │Parrain │  │Centre   │
└──┬───┘      └──────┬───┘  └───┬────┘  └──┬──────┘
   │                 │          │          │
   │ N               │ N        │ N        │ 1
┌──▼─────────┐  ┌───▼──────┐ ┌─▼────────┐ │
│Mentoring   │  │Parrainage│ │Paiement  │ │
└────────────┘  └───┬──────┘ └────┬─────┘ │
                    │              │       │
                    │ 1            │ 1     │
              ┌─────▼──────────────▼───────▼────┐
              │        Formation                │
              └──────────────────────────────────┘
```

### 5.3 Points Forts de l'Architecture

✅ **Séparation des responsabilités** (Couches claires)  
✅ **Sécurité robuste** (JWT, BCrypt, RBAC)  
✅ **Scalabilité** (Stateless, possibilité de load balancing)  
✅ **Maintenabilité** (Code organisé, DTOs, services réutilisables)  
✅ **Extensibilité** (Facile d'ajouter de nouvelles fonctionnalités)  
✅ **Documentation API** (Swagger intégré)  
✅ **Communication temps réel** (WebSocket pour le chat)  

### 5.4 Points d'Amélioration Futures

🔄 **Cache** : Implémentation d'un cache (Redis) pour performances  
🔄 **Queue** : Système de queue (RabbitMQ) pour emails asynchrones  
🔄 **Tests** : Augmentation de la couverture de tests unitaires/intégration  
🔄 **Monitoring** : Intégration d'outils de monitoring avancés  
🔄 **CI/CD** : Pipeline automatique de déploiement  

---

## Conclusion

L'architecture RePartir Backend est conçue selon les meilleures pratiques Java/Spring Boot :

- **Modularité** : Code organisé en packages fonctionnels
- **Sécurité** : Authentification JWT, autorisation par rôles, hachage des mots de passe
- **Performance** : Lazy loading, requêtes optimisées
- **Maintenabilité** : DTOs, services, repositories clairs
- **Documentation** : Swagger pour l'API
- **Temps réel** : WebSocket pour les communications instantanées

Le système est prêt pour un déploiement en production avec quelques ajustements de configuration selon l'environnement cible.


