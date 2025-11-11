# 🧪 Tests des Endpoints Mentor - Guide Postman

## ⚙️ Configuration Postman

### Variables d'environnement
```
base_url = http://localhost:8183
mentor_token = <votre_token_JWT_mentor>
mentor_email = mentor@example.com
mentor_id = 3
```

---

## 📍 Tests à effectuer dans l'ordre

### ✅ Test 1 : Login Mentor
**Endpoint** : `POST {{base_url}}/api/auth/login`

**Body (JSON)** :
```json
{
  "email": "mentor@example.com",
  "password": "votre_mot_de_passe"
}
```

**Résultat attendu** : 200 OK
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "role": "MENTOR"
}
```

📝 **Copier le token** et le mettre dans la variable `mentor_token`

---

### ✅ Test 2 : Récupérer le profil
**Endpoint** : `GET {{base_url}}/api/mentors/profile`

**Headers** :
```
Authorization: Bearer {{mentor_token}}
```

**Résultat attendu** : 200 OK
```json
{
  "id": 3,
  "prenom": "Ousmane",
  "nom": "Sall",
  "profession": "Développeur Full Stack",
  "anneesExperience": 5,
  "aPropos": "Passionné par le développement web...",
  "telephone": "+221771234567",
  "email": "mentor@example.com",
  "urlPhoto": "http://localhost:8183/uploads/photos/user_3.jpg"
}
```

🔍 **Vérifier** :
- ✅ `urlPhoto` commence par `http://localhost:8183/uploads/`
- ✅ Toutes les informations sont présentes

---

### ✅ Test 3 : Mettre à jour le profil
**Endpoint** : `PUT {{base_url}}/api/mentors/{{mentor_id}}`

**Headers** :
```
Authorization: Bearer {{mentor_token}}
Content-Type: application/json
```

**Body (JSON)** - tous les champs sont optionnels :
```json
{
  "prenom": "Ousmane",
  "nom": "Sall",
  "telephone": "+221771234567",
  "profession": "Développeur Full Stack Senior",
  "annee_experience": 8,
  "a_propos": "Expert en Java, Spring Boot, React et Flutter avec 8 ans d'expérience dans le développement d'applications web et mobile."
}
```

**Résultat attendu** : 200 OK
```json
{
  "id": 3,
  "prenom": "Ousmane",
  "nom": "Sall",
  "profession": "Développeur Full Stack Senior",
  "anneesExperience": 8,
  "aPropos": "Expert en Java, Spring Boot, React et Flutter...",
  ...
}
```

🔍 **Vérifier** :
- ✅ Les champs ont été mis à jour
- ✅ Les autres champs sont conservés

---

### ✅ Test 4 : Upload photo de profil
**Endpoint** : `POST {{base_url}}/api/utilisateurs/photoprofil`

**Headers** :
```
Authorization: Bearer {{mentor_token}}
```

**Body (form-data)** :
```
file: [sélectionner une image JPG/PNG < 10MB]
email: mentor@example.com
```

**Résultat attendu** : 200 OK
```
"Photo enregistrée avec succès : http://localhost:8183/uploads/photos/user_3.jpg"
```

🔍 **Vérifier** :
- ✅ Message de succès reçu
- ✅ URL commence par `http://localhost:8183/uploads/`

---

### ✅ Test 5 : Accès direct à la photo
**Méthode** : Ouvrir dans le navigateur

**URL** : `http://localhost:8183/uploads/photos/user_3.jpg`

**Résultat attendu** :
- ✅ La photo s'affiche dans le navigateur
- ✅ Pas d'erreur 404
- ✅ Pas d'erreur de sécurité

---

### ✅ Test 6 : Re-récupérer le profil (vérifier la photo)
**Endpoint** : `GET {{base_url}}/api/mentors/profile`

**Headers** :
```
Authorization: Bearer {{mentor_token}}
```

**Résultat attendu** : 200 OK
```json
{
  "id": 3,
  "urlPhoto": "http://localhost:8183/uploads/photos/user_3.jpg",
  ...
}
```

🔍 **Vérifier** :
- ✅ `urlPhoto` est bien l'URL HTTP de la nouvelle photo
- ✅ L'URL fonctionne dans le navigateur

---

### ✅ Test 7 : Tentative de modifier le profil d'un autre mentor (Sécurité)
**Endpoint** : `PUT {{base_url}}/api/mentors/999`

**Headers** :
```
Authorization: Bearer {{mentor_token}}
Content-Type: application/json
```

**Body (JSON)** :
```json
{
  "profession": "Hacker"
}
```

**Résultat attendu** : 403 FORBIDDEN
```
"Vous ne pouvez modifier que votre propre profil"
```

🔍 **Vérifier** :
- ✅ Erreur 403
- ✅ Message clair

---

## 🎯 Tests complémentaires

### Test 8 : Liste des mentors (public)
**Endpoint** : `GET {{base_url}}/api/mentors`

**Headers** : Aucun (endpoint public)

**Résultat attendu** : 200 OK
```json
[
  {
    "id": 1,
    "prenom": "Jean",
    "profession": "Chef de projet",
    "urlPhoto": "http://localhost:8183/uploads/photos/user_1.jpg",
    ...
  },
  {
    "id": 3,
    "prenom": "Ousmane",
    "profession": "Développeur Full Stack Senior",
    "urlPhoto": "http://localhost:8183/uploads/photos/user_3.jpg",
    ...
  }
]
```

🔍 **Vérifier** :
- ✅ Liste de tous les mentors
- ✅ Toutes les `urlPhoto` sont en HTTP
- ✅ Accessible sans authentification

---

### Test 9 : Détails d'un mentor (public)
**Endpoint** : `GET {{base_url}}/api/mentors/{{mentor_id}}`

**Headers** : Aucun (endpoint public)

**Résultat attendu** : 200 OK
```json
{
  "id": 3,
  "prenom": "Ousmane",
  "profession": "Développeur Full Stack Senior",
  "urlPhoto": "http://localhost:8183/uploads/photos/user_3.jpg",
  ...
}
```

---

## ❌ Tests d'erreurs

### Test 10 : Sans authentification
**Endpoint** : `GET {{base_url}}/api/mentors/profile`

**Headers** : Aucun

**Résultat attendu** : 401 UNAUTHORIZED

---

### Test 11 : Avec mauvais token
**Endpoint** : `GET {{base_url}}/api/mentors/profile`

**Headers** :
```
Authorization: Bearer invalid_token_123
```

**Résultat attendu** : 401 UNAUTHORIZED

---

### Test 12 : Fichier trop volumineux
**Endpoint** : `POST {{base_url}}/api/utilisateurs/photoprofil`

**Body (form-data)** :
```
file: [image > 10MB]
email: mentor@example.com
```

**Résultat attendu** : 413 PAYLOAD TOO LARGE
```
"Le fichier est trop volumineux. Taille maximale autorisée dépassée !"
```

---

## 📊 Checklist finale

| Test | Endpoint | Attendu | Status |
|------|----------|---------|--------|
| 1 | Login | 200 + token | ⬜ |
| 2 | GET Profile | 200 + données | ⬜ |
| 3 | PUT Update | 200 + màj | ⬜ |
| 4 | POST Photo | 200 + URL HTTP | ⬜ |
| 5 | Navigateur | Photo affichée | ⬜ |
| 6 | GET Profile | URL HTTP | ⬜ |
| 7 | PUT autre ID | 403 FORBIDDEN | ⬜ |
| 8 | GET Liste | 200 public | ⬜ |
| 9 | Sans auth | 401 | ⬜ |
| 10 | Mauvais token | 401 | ⬜ |

---

## 🐛 En cas d'erreur

### Erreur 404 sur /uploads/photos/...
**Cause** : `StaticResourceConfiguration` non chargée

**Solution** :
1. Vérifier que le fichier existe : `src/main/java/com/example/repartir_backend/config/StaticResourceConfiguration.java`
2. Redémarrer le backend
3. Vérifier les logs Spring : `Mapped URL path [/uploads/**]`

---

### Photo retourne toujours chemin local
**Cause** : Code d'upload non mis à jour

**Solution** :
1. Vérifier `UploadService.java` ligne 48
2. Doit retourner : `serverUrl + "/uploads/" + relativePath`
3. Redémarrer le backend
4. Re-uploader une nouvelle photo

---

### Erreur 403 sur profil
**Cause** : Token avec mauvais rôle

**Solution** :
1. Vérifier le contenu du JWT sur https://jwt.io
2. Le champ `role` doit être `MENTOR`
3. Se reconnecter avec un compte mentor

---

## 🎉 Tous les tests passent ?

✅ **Backend complètement fonctionnel !**  
✅ **Prêt pour l'intégration frontend !**  
✅ **Photos accessibles en HTTP !**  

Passez au frontend Flutter pour l'intégration complète ! 🚀

