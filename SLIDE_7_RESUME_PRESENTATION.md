# Slide 7: Conception et Architecture - Résumé Présentation

---

## 📊 1. ARCHITECTURE DES DONNÉES

### Modélisation
- **20 entités JPA** avec relations hiérarchiques
- **Entité centrale** : `Utilisateur` (email unique, rôle, état)
- **Entités spécialisées** : Jeune, Mentor, Parrain, Admin, CentreFormation, Entreprise
- **Pattern** : Composition via relations OneToOne avec Utilisateur
- **Relations clés** : Jeune ↔ Formation ↔ Paiement ↔ Parrainage

### Stockage
- **SGBD** : MySQL 8.0+ (localhost:3306/repartir)
- **ORM** : Hibernate (génération auto du schéma)
- **Fichiers** : Stockage local (`C:/uploads/`) servis via HTTP
- **Limite** : 10MB par fichier uploadé

### Sécurité
- ✅ **Mots de passe** : Hachage BCrypt
- ✅ **Authentification** : JWT (Access + Refresh tokens)
- ✅ **Autorisation** : RBAC avec 6 rôles (ADMIN, JEUNE, MENTOR, PARRAIN, CENTRE, ENTREPRISE)
- ✅ **CORS** : Configuré pour localhost et IP locales
- ✅ **Validation** : Jakarta Bean Validation

### Conformité
- Traçabilité : Dates de création, historique des paiements
- Intégrité : Contraintes de clé étrangère, transactions
- Données personnelles : Validation email/téléphone uniques

---

## 🛠️ 2. TECHNOLOGIES UTILISÉES

### Framework
- **Spring Boot 3.5.6** + **Java 21** (LTS)

### Backend
- **Spring Data JPA** + **Hibernate** (ORM)
- **MySQL Connector/J** (Base de données)
- **Spring Security** (Sécurité)
- **JWT** (jjwt v0.13.0)

### Communication
- **Spring Web** (REST API)
- **Spring WebSocket** + **STOMP** (Chat temps réel)
- **Spring Mail** (Emails transactionnels)

### Documentation & Utilitaires
- **SpringDoc OpenAPI** (Swagger UI)
- **Lombok** (Réduction du code)
- **Maven** (Build & dépendances)

---

## 🏗️ 3. ARCHITECTURE DE L'APPLICATION

### Architecture en 4 Couches

```
┌─────────────────────────────┐
│  CONTROLLERS (26 fichiers)  │ ← REST + WebSocket
├─────────────────────────────┤
│  SERVICES (27 fichiers)     │ ← Logique métier
├─────────────────────────────┤
│  REPOSITORIES (19 fichiers) │ ← Accès données
├─────────────────────────────┤
│  ENTITIES (20 fichiers)     │ ← Modèle JPA
└─────────────────────────────┘
```

### Structure du Projet
- **controllers/** : 26 contrôleurs REST + WebSocket
- **services/** : 27 services métier
- **repositories/** : 19 repositories JPA
- **entities/** : 20 entités JPA
- **dto/** : 42 DTOs (Request/Response)
- **security/** : Configuration JWT + Spring Security
- **config/** : Configurations Spring

### Communication Temps Réel
- **WebSocket** via STOMP
- **Endpoint** : `/ws`
- **Authentification** : JWT dans les headers WebSocket
- **Use case** : Chat Mentor ↔ Jeune

---

## 🚀 4. DÉPLOIEMENT

### Configuration Actuelle
- **Port** : 8183
- **URL** : `http://localhost:8183`
- **Mode** : Développement

### Build & Exécution
```bash
mvn clean package
java -jar target/repartir_backend-0.0.1-SNAPSHOT.jar
```

### Variables d'Environnement Requises
- `jwtsecret` : Clé secrète JWT
- `defaultadminemail` : Email admin initial
- `defaultadminpassword` : Mot de passe admin
- `emailpassword` : Mot de passe SMTP

### Production Recommandée
1. **Base de données** : MySQL en production (pool de connexions)
2. **Secrets** : Gestionnaire de secrets (Vault, AWS Secrets)
3. **Containerisation** : Docker (optionnel)
4. **Reverse Proxy** : Nginx (SSL/TLS, load balancing)
5. **Monitoring** : Actuator + Prometheus/Grafana
6. **Stockage fichiers** : Cloud (S3) ou CDN

---

## 🎨 5. PRÉSENTATION DES PROTOTYPES

### Flux Principaux

#### Flux 1 : Inscription Jeune
```
Register → Utilisateur + Jeune créés (EN_ATTENTE)
→ Admin valide → Email confirmation
```

#### Flux 2 : Parrainage Formation
```
Jeune s'inscrit → InscriptionFormation
→ Parrain crée parrainage → Paiement (EN_ATTENTE)
→ Admin valide paiement → Inscription validée
→ Email confirmation + Décrément places
```

#### Flux 3 : Mentoring & Chat
```
Jeune demande mentorat → Mentoring (EN_ATTENTE)
→ Mentor accepte → Communication WebSocket temps réel
→ Messages persistés en base
```

### Points Forts
✅ **Modularité** : Code organisé et séparé  
✅ **Sécurité** : JWT + BCrypt + RBAC  
✅ **Performance** : Lazy loading, requêtes optimisées  
✅ **Documentation** : Swagger intégré  
✅ **Temps réel** : WebSocket pour chat  
✅ **Scalabilité** : Stateless, prêt pour load balancing  

### Améliorations Futures
🔄 Cache Redis  
🔄 Queue RabbitMQ (emails asynchrones)  
🔄 Tests unitaires/intégration  
🔄 CI/CD Pipeline  

---

## 📈 Statistiques du Projet

- **26 Contrôleurs** REST + WebSocket
- **27 Services** métier
- **19 Repositories** JPA
- **20 Entités** JPA
- **42 DTOs** (Request/Response)
- **7 Enumerations**
- **Architecture** : 4 couches (Controllers → Services → Repositories → Entities)

---

## 🎯 Conclusion

**Architecture robuste** basée sur Spring Boot avec :
- Séparation claire des responsabilités
- Sécurité renforcée (JWT, RBAC)
- Communication temps réel (WebSocket)
- Prête pour la production avec quelques ajustements

