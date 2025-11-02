# Guide d'Intégration Angular - Interface Admin RePartir

Ce document fournit toutes les informations nécessaires pour implémenter l'interface admin Angular avec toutes les fonctionnalités de gestion.

---

## 📋 TABLE DES MATIÈRES

1. [Modèles TypeScript](#1-modèles-typescript)
2. [Services HTTP](#2-services-http)
3. [Endpoints API](#3-endpoints-api-complets)
4. [Fonctionnalités à Implémenter](#4-fonctionnalités-à-implémenter)
5. [Exemples de Composants](#5-exemples-de-composants)

---

## 1. MODÈLES TYPESCRIPT

### 1.1 Modèles de Base

```typescript
// models/enums.ts
export enum Role {
  JEUNE = 'JEUNE',
  PARRAIN = 'PARRAIN',
  MENTOR = 'MENTOR',
  CENTRE = 'CENTRE',
  ENTREPRISE = 'ENTREPRISE',
  ADMIN = 'ADMIN'
}

export enum Etat {
  EN_ATTENTE = 'EN_ATTENTE',
  VALIDE = 'VALIDE',
  REFUSE = 'REFUSE',
  SUPPRIME = 'SUPPRIME'
}
```

### 1.2 Modèles Utilisateur

```typescript
// models/utilisateur.model.ts
export interface UtilisateurResponseDto {
  id: number;
  nom: string;
  email: string;
  telephone: string;
  role: Role;
  etat: Etat;
  estActive: boolean;
}
```

### 1.3 Modèles Admin

```typescript
// models/admin.model.ts
export interface Admin {
  id: number;
  prenom: string;
  nom: string;
  email: string;
  role: Role;
}

export interface AdminDto {
  prenom: string;
  nom: string;
  email: string;
  motDePasse: string;
}

export interface UpdateAdminDto {
  prenom?: string;
  nom?: string;
  email?: string;
  motDePasse?: string;
}

export interface AdminResponseDto {
  id: number;
  nom: string;
  prenom: string;
  email: string;
  role: string;
}
```

### 1.4 Modèles Centre de Formation

```typescript
// models/centre-formation.model.ts
export interface ResponseCentre {
  id: number;
  nom: string;
  adresse: string;
  telephone: string;
  email: string;
  urlPhoto: string | null;
  role: Role;
  estActive: boolean;
  agrement: string;
}
```

### 1.5 Modèles Entreprise

```typescript
// models/entreprise.model.ts
export interface Entreprise {
  id: number;
  adresse: string;
  agrement: string;
  utilisateur: UtilisateurResponseDto;
}
```

### 1.6 Modèles Jeune

```typescript
// models/jeune.model.ts
export interface Jeune {
  id: number;
  utilisateur: UtilisateurResponseDto;
  // Ajouter autres champs selon besoin
}
```

### 1.7 Modèles Parrain

```typescript
// models/parrain.model.ts
export interface ResponseParrain {
  nom: string;
  prenom: string;
  email: string;
  telephone: string;
  urlPhoto: string | null;
  role: Role;
  estActive: boolean;
  profession: string;
  dateInscription: string; // ISO date string
}
```

### 1.8 Modèles Mentor

```typescript
// models/mentor.model.ts
export interface MentorResponseDto {
  id: number;
  nomComplet: string;
  email: string;
  annee_experience: number;
  a_propos: string;
  profession: string;
  urlPhoto: string | null;
}
```

### 1.9 Modèles Domaine

```typescript
// models/domaine.model.ts
export interface DomaineDto {
  libelle: string;
}

export interface DomaineResponseDto {
  id: number;
  libelle: string;
}
```

### 1.10 Modèles Statistiques

```typescript
// models/statistics.model.ts
export interface MonthlyCountDto {
  month: number; // 1-12
  count: number;
}

export interface DashboardStatsDto {
  year: number;
  monthlyRegistrations: MonthlyCountDto[];
  centresCount: number;
  blockedAccountsCount: number;
  pendingAccountsCount: number;
  activeAdminsCount: number;
}
```

---

## 2. SERVICES HTTP

### 2.1 Service Admin

```typescript
// services/admin.service.ts
import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Admin, AdminDto, UpdateAdminDto, AdminResponseDto } from '../models/admin.model';
import { UtilisateurResponseDto } from '../models/utilisateur.model';

@Injectable({ providedIn: 'root' })
export class AdminService {
  private baseUrl = '/administrateurs';

  constructor(private http: HttpClient) {}

  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('access_token');
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    });
  }

  // Créer un admin
  creerAdmin(adminDto: AdminDto): Observable<AdminResponseDto> {
    return this.http.post<AdminResponseDto>(
      `${this.baseUrl}/creer`,
      adminDto,
      { headers: this.getHeaders() }
    );
  }

  // Lister tous les admins
  listerAdmins(): Observable<Admin[]> {
    return this.http.get<Admin[]>(
      `${this.baseUrl}/lister`,
      { headers: this.getHeaders() }
    );
  }

  // Modifier un admin
  modifierAdmin(adminId: number, updateAdminDto: UpdateAdminDto): Observable<AdminResponseDto> {
    return this.http.put<AdminResponseDto>(
      `${this.baseUrl}/modifier/${adminId}`,
      updateAdminDto,
      { headers: this.getHeaders() }
    );
  }

  // Lister comptes en attente
  listerComptesEnAttente(): Observable<UtilisateurResponseDto[]> {
    return this.http.get<UtilisateurResponseDto[]>(
      `${this.baseUrl}/comptes-en-attente`,
      { headers: this.getHeaders() }
    );
  }

  // Valider un compte
  validerCompte(userId: number): Observable<UtilisateurResponseDto> {
    return this.http.put<UtilisateurResponseDto>(
      `${this.baseUrl}/valider-compte/${userId}`,
      {},
      { headers: this.getHeaders() }
    );
  }

  // Refuser un compte
  refuserCompte(userId: number): Observable<UtilisateurResponseDto> {
    return this.http.put<UtilisateurResponseDto>(
      `${this.baseUrl}/refuser-compte/${userId}`,
      {},
      { headers: this.getHeaders() }
    );
  }

  // Bloquer un utilisateur
  bloquerUtilisateur(userId: number): Observable<UtilisateurResponseDto> {
    return this.http.put<UtilisateurResponseDto>(
      `${this.baseUrl}/bloquer-utilisateur/${userId}`,
      {},
      { headers: this.getHeaders() }
    );
  }

  // Débloquer un utilisateur
  debloquerUtilisateur(userId: number): Observable<UtilisateurResponseDto> {
    return this.http.put<UtilisateurResponseDto>(
      `${this.baseUrl}/debloquer-utilisateur/${userId}`,
      {},
      { headers: this.getHeaders() }
    );
  }
}
```

### 2.2 Service Centre de Formation

```typescript
// services/centre-formation.service.ts
import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ResponseCentre } from '../models/centre-formation.model';

@Injectable({ providedIn: 'root' })
export class CentreFormationService {
  private baseUrl = '/api/centres';

  constructor(private http: HttpClient) {}

  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('access_token');
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    });
  }

  // Lister tous les centres
  getAllCentres(): Observable<ResponseCentre[]> {
    return this.http.get<ResponseCentre[]>(
      `${this.baseUrl}`,
      { headers: this.getHeaders() }
    );
  }

  // Lister centres actifs
  getCentresActifs(): Observable<ResponseCentre[]> {
    return this.http.get<ResponseCentre[]>(
      `${this.baseUrl}/actifs`,
      { headers: this.getHeaders() }
    );
  }

  // Obtenir un centre par ID
  getCentreById(id: number): Observable<ResponseCentre> {
    return this.http.get<ResponseCentre>(
      `${this.baseUrl}/${id}`,
      { headers: this.getHeaders() }
    );
  }

  // Activer un centre
  activerCentre(id: number): Observable<any> {
    return this.http.put(
      `${this.baseUrl}/${id}/activer`,
      {},
      { headers: this.getHeaders() }
    );
  }

  // Désactiver un centre
  desactiverCentre(id: number): Observable<any> {
    return this.http.put(
      `${this.baseUrl}/${id}/desactiver`,
      {},
      { headers: this.getHeaders() }
    );
  }

  // Supprimer un centre
  deleteCentre(id: number): Observable<any> {
    return this.http.delete(
      `${this.baseUrl}/${id}`,
      { headers: this.getHeaders() }
    );
  }
}
```

### 2.3 Service Entreprise

```typescript
// services/entreprise.service.ts
import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Entreprise } from '../models/entreprise.model';

@Injectable({ providedIn: 'root' })
export class EntrepriseService {
  private baseUrl = '/api/entreprises';

  constructor(private http: HttpClient) {}

  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('access_token');
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    });
  }

  // ⚠️ NOTE: Cet endpoint doit être créé côté backend
  // Lister toutes les entreprises
  getAllEntreprises(): Observable<Entreprise[]> {
    return this.http.get<Entreprise[]>(
      `${this.baseUrl}`,
      { headers: this.getHeaders() }
    );
  }
}
```

### 2.4 Service Jeune

```typescript
// services/jeune.service.ts
import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Jeune } from '../models/jeune.model';

@Injectable({ providedIn: 'root' })
export class JeuneService {
  private baseUrl = '/api/jeunes';

  constructor(private http: HttpClient) {}

  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('access_token');
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    });
  }

  // ⚠️ NOTE: Cet endpoint doit être créé côté backend
  // Lister tous les jeunes
  getAllJeunes(): Observable<Jeune[]> {
    return this.http.get<Jeune[]>(
      `${this.baseUrl}`,
      { headers: this.getHeaders() }
    );
  }
}
```

### 2.5 Service Parrain

```typescript
// services/parrain.service.ts
import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ResponseParrain } from '../models/parrain.model';

@Injectable({ providedIn: 'root' })
export class ParrainService {
  private baseUrl = '/api/parrains';

  constructor(private http: HttpClient) {}

  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('access_token');
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    });
  }

  // Lister tous les parrains
  getAllParrains(): Observable<ResponseParrain[]> {
    return this.http.get<ResponseParrain[]>(
      `${this.baseUrl}`,
      { headers: this.getHeaders() }
    );
  }

  // Lister parrains actifs
  getParrainsActifs(): Observable<ResponseParrain[]> {
    return this.http.get<ResponseParrain[]>(
      `${this.baseUrl}/actifs`,
      { headers: this.getHeaders() }
    );
  }

  // Obtenir parrain par ID
  getParrainById(id: number): Observable<ResponseParrain> {
    return this.http.get<ResponseParrain>(
      `${this.baseUrl}/${id}`,
      { headers: this.getHeaders() }
    );
  }
}
```

### 2.6 Service Mentor

```typescript
// services/mentor.service.ts
import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { MentorResponseDto } from '../models/mentor.model';

@Injectable({ providedIn: 'root' })
export class MentorService {
  private baseUrl = '/api/mentors';

  constructor(private http: HttpClient) {}

  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('access_token');
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    });
  }

  // Lister tous les mentors
  getAllMentors(): Observable<MentorResponseDto[]> {
    return this.http.get<MentorResponseDto[]>(
      `${this.baseUrl}`,
      { headers: this.getHeaders() }
    );
  }

  // Obtenir mentor par ID
  getMentorById(id: number): Observable<any> {
    return this.http.get(
      `${this.baseUrl}/${id}`,
      { headers: this.getHeaders() }
    );
  }
}
```

### 2.7 Service Domaine

```typescript
// services/domaine.service.ts
import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { DomaineDto, DomaineResponseDto } from '../models/domaine.model';

@Injectable({ providedIn: 'root' })
export class DomaineService {
  private baseUrl = '/api/domaines';

  constructor(private http: HttpClient) {}

  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('access_token');
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    });
  }

  // Créer un domaine
  creerDomaine(domaineDto: DomaineDto): Observable<any> {
    return this.http.post(
      `${this.baseUrl}/creer`,
      domaineDto,
      { headers: this.getHeaders() }
    );
  }

  // Lister tous les domaines
  listerDomaines(): Observable<DomaineResponseDto[]> {
    return this.http.get<DomaineResponseDto[]>(
      `${this.baseUrl}/lister`,
      { headers: this.getHeaders() }
    );
  }

  // ⚠️ NOTE: Ces endpoints doivent être créés côté backend
  // Modifier un domaine
  modifierDomaine(id: number, domaineDto: DomaineDto): Observable<DomaineResponseDto> {
    return this.http.put<DomaineResponseDto>(
      `${this.baseUrl}/modifier/${id}`,
      domaineDto,
      { headers: this.getHeaders() }
    );
  }

  // Supprimer un domaine
  supprimerDomaine(id: number): Observable<void> {
    return this.http.delete<void>(
      `${this.baseUrl}/supprimer/${id}`,
      { headers: this.getHeaders() }
    );
  }
}
```

### 2.8 Service Statistiques

```typescript
// services/statistics.service.ts
import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { DashboardStatsDto } from '../models/statistics.model';

@Injectable({ providedIn: 'root' })
export class StatisticsService {
  private baseUrl = '/administrateurs/statistiques';

  constructor(private http: HttpClient) {}

  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('access_token');
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    });
  }

  getDashboard(year?: number): Observable<DashboardStatsDto> {
    const url = year 
      ? `${this.baseUrl}/dashboard?year=${year}`
      : `${this.baseUrl}/dashboard`;
    return this.http.get<DashboardStatsDto>(url, { headers: this.getHeaders() });
  }
}
```

---

## 3. ENDPOINTS API COMPLETS

### 3.1 Endpoints Admin

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| `POST` | `/administrateurs/creer` | Créer un admin |
| `GET` | `/administrateurs/lister` | Lister tous les admins |
| `PUT` | `/administrateurs/modifier/{adminId}` | Modifier un admin |
| `GET` | `/administrateurs/comptes-en-attente` | Lister comptes en attente |
| `PUT` | `/administrateurs/valider-compte/{userId}` | Valider un compte |
| `PUT` | `/administrateurs/refuser-compte/{userId}` | Refuser un compte |
| `PUT` | `/administrateurs/bloquer-utilisateur/{userId}` | Bloquer un utilisateur |
| `PUT` | `/administrateurs/debloquer-utilisateur/{userId}` | Débloquer un utilisateur |
| `GET` | `/administrateurs/statistiques/dashboard?year={year}` | Statistiques dashboard |

### 3.2 Endpoints Centre de Formation

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| `GET` | `/api/centres` | Lister tous les centres |
| `GET` | `/api/centres/actifs` | Lister centres actifs |
| `GET` | `/api/centres/{id}` | Obtenir un centre par ID |
| `PUT` | `/api/centres/{id}/activer` | Activer un centre |
| `PUT` | `/api/centres/{id}/desactiver` | Désactiver un centre |
| `DELETE` | `/api/centres/{id}` | Supprimer un centre |

### 3.3 Endpoints Entreprise

| Méthode | Endpoint | Description | ⚠️ |
|---------|----------|-------------|-----|
| `GET` | `/api/entreprises` | **À CRÉER** - Lister toutes les entreprises | ⚠️ |

### 3.4 Endpoints Jeune

| Méthode | Endpoint | Description | ⚠️ |
|---------|----------|-------------|-----|
| `GET` | `/api/jeunes` | **À CRÉER** - Lister tous les jeunes | ⚠️ |

### 3.5 Endpoints Parrain

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| `GET` | `/api/parrains` | Lister tous les parrains |
| `GET` | `/api/parrains/actifs` | Lister parrains actifs |
| `GET` | `/api/parrains/{id}` | Obtenir parrain par ID |

### 3.6 Endpoints Mentor

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| `GET` | `/api/mentors` | Lister tous les mentors |
| `GET` | `/api/mentors/{id}` | Obtenir mentor par ID |

### 3.7 Endpoints Domaine

| Méthode | Endpoint | Description | ⚠️ |
|---------|----------|-------------|-----|
| `POST` | `/api/domaines/creer` | Créer un domaine | |
| `GET` | `/api/domaines/lister` | Lister tous les domaines | |
| `PUT` | `/api/domaines/modifier/{id}` | **À CRÉER** - Modifier un domaine | ⚠️ |
| `DELETE` | `/api/domaines/supprimer/{id}` | **À CRÉER** - Supprimer un domaine | ⚠️ |

---

## 4. FONCTIONNALITÉS À IMPLÉMENTER

### 4.1 Vue Liste des Centres de Formation

```typescript
// components/centres-list/centres-list.component.ts
import { Component, OnInit } from '@angular/core';
import { CentreFormationService } from '../../services/centre-formation.service';
import { ResponseCentre } from '../../models/centre-formation.model';

@Component({
  selector: 'app-centres-list',
  templateUrl: './centres-list.component.html'
})
export class CentresListComponent implements OnInit {
  centres: ResponseCentre[] = [];
  loading = false;
  error: string | null = null;

  constructor(private centreService: CentreFormationService) {}

  ngOnInit(): void {
    this.loadCentres();
  }

  loadCentres(): void {
    this.loading = true;
    this.centreService.getAllCentres().subscribe({
      next: (data) => {
        this.centres = data;
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Erreur lors du chargement des centres';
        this.loading = false;
      }
    });
  }

  activerCentre(id: number): void {
    this.centreService.activerCentre(id).subscribe({
      next: () => this.loadCentres(),
      error: (err) => alert('Erreur: ' + err.message)
    });
  }

  desactiverCentre(id: number): void {
    this.centreService.desactiverCentre(id).subscribe({
      next: () => this.loadCentres(),
      error: (err) => alert('Erreur: ' + err.message)
    });
  }

  supprimerCentre(id: number): void {
    if (confirm('Êtes-vous sûr de vouloir supprimer ce centre ?')) {
      this.centreService.deleteCentre(id).subscribe({
        next: () => this.loadCentres(),
        error: (err) => alert('Erreur: ' + err.message)
      });
    }
  }
}
```

### 4.2 Vue Liste des Utilisateurs en Attente

```typescript
// components/pending-accounts/pending-accounts.component.ts
import { Component, OnInit } from '@angular/core';
import { AdminService } from '../../services/admin.service';
import { UtilisateurResponseDto } from '../../models/utilisateur.model';

@Component({
  selector: 'app-pending-accounts',
  templateUrl: './pending-accounts.component.html'
})
export class PendingAccountsComponent implements OnInit {
  comptesEnAttente: UtilisateurResponseDto[] = [];
  loading = false;

  constructor(private adminService: AdminService) {}

  ngOnInit(): void {
    this.loadComptesEnAttente();
  }

  loadComptesEnAttente(): void {
    this.loading = true;
    this.adminService.listerComptesEnAttente().subscribe({
      next: (data) => {
        this.comptesEnAttente = data;
        this.loading = false;
      },
      error: (err) => {
        alert('Erreur: ' + err.message);
        this.loading = false;
      }
    });
  }

  validerCompte(userId: number): void {
    this.adminService.validerCompte(userId).subscribe({
      next: () => {
        alert('Compte validé avec succès');
        this.loadComptesEnAttente();
      },
      error: (err) => alert('Erreur: ' + err.message)
    });
  }

  refuserCompte(userId: number): void {
    if (confirm('Êtes-vous sûr de vouloir refuser ce compte ?')) {
      this.adminService.refuserCompte(userId).subscribe({
        next: () => {
          alert('Compte refusé');
          this.loadComptesEnAttente();
        },
        error: (err) => alert('Erreur: ' + err.message)
      });
    }
  }
}
```

### 4.3 Vue Gestion Admin (Créer/Modifier)

```typescript
// components/admin-management/admin-management.component.ts
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AdminService } from '../../services/admin.service';
import { AdminDto, UpdateAdminDto } from '../../models/admin.model';

@Component({
  selector: 'app-admin-management',
  templateUrl: './admin-management.component.html'
})
export class AdminManagementComponent implements OnInit {
  adminForm: FormGroup;
  admins: any[] = [];
  editingAdmin: number | null = null;

  constructor(
    private fb: FormBuilder,
    private adminService: AdminService
  ) {
    this.adminForm = this.fb.group({
      prenom: ['', Validators.required],
      nom: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      motDePasse: ['']
    });
  }

  ngOnInit(): void {
    this.loadAdmins();
  }

  loadAdmins(): void {
    this.adminService.listerAdmins().subscribe({
      next: (data) => this.admins = data,
      error: (err) => alert('Erreur: ' + err.message)
    });
  }

  creerAdmin(): void {
    if (this.adminForm.valid) {
      const adminDto: AdminDto = this.adminForm.value;
      this.adminService.creerAdmin(adminDto).subscribe({
        next: () => {
          alert('Admin créé avec succès');
          this.adminForm.reset();
          this.loadAdmins();
        },
        error: (err) => alert('Erreur: ' + err.message)
      });
    }
  }

  modifierAdmin(adminId: number): void {
    const updateDto: UpdateAdminDto = this.adminForm.value;
    this.adminService.modifierAdmin(adminId, updateDto).subscribe({
      next: () => {
        alert('Admin modifié avec succès');
        this.adminForm.reset();
        this.editingAdmin = null;
        this.loadAdmins();
      },
      error: (err) => alert('Erreur: ' + err.message)
    });
  }

  editerAdmin(admin: any): void {
    this.editingAdmin = admin.id;
    this.adminForm.patchValue({
      prenom: admin.prenom,
      nom: admin.nom,
      email: admin.email,
      motDePasse: ''
    });
  }
}
```

### 4.4 Vue Gestion Domaine (Créer/Modifier/Supprimer)

```typescript
// components/domaine-management/domaine-management.component.ts
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { DomaineService } from '../../services/domaine.service';
import { DomaineDto, DomaineResponseDto } from '../../models/domaine.model';

@Component({
  selector: 'app-domaine-management',
  templateUrl: './domaine-management.component.html'
})
export class DomaineManagementComponent implements OnInit {
  domaineForm: FormGroup;
  domaines: DomaineResponseDto[] = [];
  editingDomaine: number | null = null;

  constructor(
    private fb: FormBuilder,
    private domaineService: DomaineService
  ) {
    this.domaineForm = this.fb.group({
      libelle: ['', Validators.required]
    });
  }

  ngOnInit(): void {
    this.loadDomaines();
  }

  loadDomaines(): void {
    this.domaineService.listerDomaines().subscribe({
      next: (data) => this.domaines = data,
      error: (err) => alert('Erreur: ' + err.message)
    });
  }

  creerDomaine(): void {
    if (this.domaineForm.valid) {
      const domaineDto: DomaineDto = this.domaineForm.value;
      this.domaineService.creerDomaine(domaineDto).subscribe({
        next: () => {
          alert('Domaine créé avec succès');
          this.domaineForm.reset();
          this.loadDomaines();
        },
        error: (err) => alert('Erreur: ' + err.message)
      });
    }
  }

  modifierDomaine(id: number): void {
    const domaineDto: DomaineDto = this.domaineForm.value;
    this.domaineService.modifierDomaine(id, domaineDto).subscribe({
      next: () => {
        alert('Domaine modifié avec succès');
        this.domaineForm.reset();
        this.editingDomaine = null;
        this.loadDomaines();
      },
      error: (err) => alert('Erreur: ' + err.message)
    });
  }

  supprimerDomaine(id: number): void {
    if (confirm('Êtes-vous sûr de vouloir supprimer ce domaine ?')) {
      this.domaineService.supprimerDomaine(id).subscribe({
        next: () => {
          alert('Domaine supprimé avec succès');
          this.loadDomaines();
        },
        error: (err) => alert('Erreur: ' + err.message)
      });
    }
  }

  editerDomaine(domaine: DomaineResponseDto): void {
    this.editingDomaine = domaine.id;
    this.domaineForm.patchValue({ libelle: domaine.libelle });
  }
}
```

---

## 5. ENDPOINTS BACKEND À CRÉER

### ⚠️ Endpoints Manquants

1. **GET `/api/entreprises`** - Lister toutes les entreprises
2. **GET `/api/jeunes`** - Lister tous les jeunes
3. **PUT `/api/domaines/modifier/{id}`** - Modifier un domaine
4. **DELETE `/api/domaines/supprimer/{id}`** - Supprimer un domaine

### Exemples d'implémentation backend

#### GET `/api/entreprises`

```java
@GetMapping
public ResponseEntity<List<Entreprise>> getAllEntreprises() {
    return ResponseEntity.ok(entrepriseServices.getAllEntreprises());
}
```

#### GET `/api/jeunes`

```java
@GetMapping
public ResponseEntity<List<Jeune>> getAllJeunes() {
    return ResponseEntity.ok(jeuneServices.getAllJeunes());
}
```

#### PUT `/api/domaines/modifier/{id}`

```java
@PutMapping("/modifier/{id}")
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<?> modifierDomaine(@PathVariable int id, @RequestBody DomaineDto domaineDto) {
    try {
        DomaineResponseDto domaine = domaineServices.modifierDomaine(id, domaineDto);
        return ResponseEntity.ok(domaine);
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
}
```

#### DELETE `/api/domaines/supprimer/{id}`

```java
@DeleteMapping("/supprimer/{id}")
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<?> supprimerDomaine(@PathVariable int id) {
    try {
        domaineServices.supprimerDomaine(id);
        return ResponseEntity.ok("Domaine supprimé avec succès");
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
}
```

---

## 6. CONFIGURATION HTTP INTERCEPTOR

```typescript
// interceptors/auth.interceptor.ts
import { Injectable } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler } from '@angular/common/http';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  intercept(req: HttpRequest<any>, next: HttpHandler) {
    const token = localStorage.getItem('access_token');
    
    if (token) {
      const cloned = req.clone({
        headers: req.headers.set('Authorization', `Bearer ${token}`)
      });
      return next.handle(cloned);
    }
    
    return next.handle(req);
  }
}
```

**app.module.ts:**
```typescript
import { HTTP_INTERCEPTORS } from '@angular/common/http';
import { AuthInterceptor } from './interceptors/auth.interceptor';

providers: [
  {
    provide: HTTP_INTERCEPTORS,
    useClass: AuthInterceptor,
    multi: true
  }
]
```

---

## 7. UTILS - LIBELLÉS MOIS

```typescript
// utils/month-labels.ts
export const MONTH_LABELS_FR = [
  'Jan', 'Fév', 'Mar', 'Avr', 'Mai', 'Jui',
  'Juil', 'Aoû', 'Sep', 'Oct', 'Nov', 'Déc'
];

export function toSeriesArray(monthly: {month: number; count: number}[]): number[] {
  const byMonth = new Map(monthly.map(m => [m.month, m.count]));
  return Array.from({ length: 12 }, (_, i) => byMonth.get(i + 1) ?? 0);
}
```

---

## 8. NOTES IMPORTANTES

1. **Authentification** : Tous les endpoints nécessitent un token JWT dans le header `Authorization: Bearer {token}`
2. **Base URL** : Configurer la base URL du backend dans `environment.ts`
3. **Gestion d'erreurs** : Implémenter un service de gestion d'erreurs global
4. **Loading States** : Gérer les états de chargement pour une meilleure UX
5. **Confirmations** : Toujours demander confirmation avant suppression
6. **Refresh** : Recharger les listes après chaque action (create/update/delete)

---

## 9. RÉSUMÉ DES FICHIERS À CRÉER

### Models
- `models/enums.ts`
- `models/utilisateur.model.ts`
- `models/admin.model.ts`
- `models/centre-formation.model.ts`
- `models/entreprise.model.ts`
- `models/jeune.model.ts`
- `models/parrain.model.ts`
- `models/mentor.model.ts`
- `models/domaine.model.ts`
- `models/statistics.model.ts`

### Services
- `services/admin.service.ts`
- `services/centre-formation.service.ts`
- `services/entreprise.service.ts`
- `services/jeune.service.ts`
- `services/parrain.service.ts`
- `services/mentor.service.ts`
- `services/domaine.service.ts`
- `services/statistics.service.ts`

### Components (exemples)
- `components/centres-list/centres-list.component.ts`
- `components/pending-accounts/pending-accounts.component.ts`
- `components/admin-management/admin-management.component.ts`
- `components/domaine-management/domaine-management.component.ts`
- `components/dashboard/dashboard.component.ts` (statistiques)

### Utils
- `utils/month-labels.ts`
- `interceptors/auth.interceptor.ts`

---

**Tous les endpoints listés ci-dessus doivent synchroniser les données avec la base de données. Chaque action (CRUD) est immédiatement persistée dans MySQL via JPA/Hibernate.**

