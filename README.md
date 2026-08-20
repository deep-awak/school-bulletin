# School Bulletin

[![Java](https://img.shields.io/badge/Java-21-orange?style=flat-square)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-green?style=flat-square)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-14+-blue?style=flat-square)](https://www.postgresql.org/)
[![AWS](https://img.shields.io/badge/AWS-SQS%20%7C%20EventBridge%20%7C%20SES%20%7C%20S3-orange?style=flat-square)](https://aws.amazon.com/)

---

## Table des matières

- [Fonctionnalités](#-fonctionnalités)
- [Architecture](#-architecture)
- [Modèle de données](#-modèle-de-données)
- [Authentification & Permissions](#-authentification--permissions)
- [Installation & Exécution](#-installation--exécution)
- [API & Endpoints](#-api--endpoints)
- [Flux de travail clés](#-flux-de-travail-clés)
- [Gestion des événements](#-gestion-des-événements)
- [Tests](#-tests)
- [Variables d'environnement](#-variables-denvironnement)
- [Bugs & Recommandations](#-bugs--recommandations)
- [Contribuer](#-contribuer)

---

## Fonctionnalités

### Gestion Académique
- ✅ Gestion des **étudiants, professeurs, cours, groupes, promotions**
- ✅ **Notes avec historique immuable** — chaque modification créé une trace (oldValue, newValue, reason, author)
- ✅ **Raison obligatoire** pour toute modification de note (audit trail)
- ✅ **Transcriptions PDF** générées via PDFBox
- ✅ **Export Excel** des résultats (Rang | STD | Nom | Prénom | Moyenne générale)
- ✅ **Classement des promotions** avec statistiques agrégées

### Système Asynchrone
- ✅ **Événements événementiels** → AWS EventBridge → SQS Queue
- ✅ **Consommateurs parallélisés** avec retry automatique
- ✅ **Emails asynchrones** via AWS SES (SendEmail, TranscriptEmail, etc.)
- ✅ **Dead-letter handling** (échoués loggés, nécessite amélioration)

### Contrôle d'Accès
- ✅ **Authentification sans mot de passe** via en-tête `X-User-Id`
- ✅ **RBAC à 3 rôles** : STUDENT (lecture seule), TEACHER (ses cours), ADMIN (accès total)
- ✅ **Permissions granulaires** par endpoint et par ressource
- ✅ **Vérifications centralisées** via `AccessGuard`

### Stockage & Persistence
- ✅ **PostgreSQL** avec Flyway migrations
- ✅ **JPA/Hibernate** avec FetchType.LAZY optimisé
- ✅ **S3 bucket** pour transcriptions PDF (presigned URLs)

---

## Architecture

### Arborescence du projet

```
src/main/java/api/poja/app/
├── endpoint/                     # Contrôleurs & événements
│   ├── event/
│   │   ├── EventProducer.java          # Envoie événements → EventBridge
│   │   ├── EventConf.java              # Config AWS SQS
│   │   ├── EventStack.java             # Enum des queues (STACK_1, STACK_2)
│   │   ├── consumer/
│   │   │   ├── EventConsumer.java      # Lit depuis SQS, parallélise
│   │   │   └── EventServiceInvoker.java # Dispatch par reflection
│   │   └── model/
│   │       ├── PojaEvent.java          # Base abstraite
│   │       └── SendEmailRequested.java
│   ├── rest/controller/
│   │   ├── HelloWorldController.java   # Test: /hello?to=email
│   │   ├── GradeController.java        # /api/grades/*
│   │   ├── StudentController.java      # /api/students/*
│   │   └── ...
│   └── RequestLoggerConfigurer.java    # Interceptor HTTP logging
├── service/                      # Logique métier
│   ├── event/
│   │   ├── SendEmailRequestedService.java  # Consomme SendEmailRequested
│   │   └── TranscriptEmailRequestedService.java
│   ├── GradeService.java                   # CRUD notes + historique
│   ├── StudentService.java
│   ├── TeacherService.java
│   ├── GraduateService.java                # Classements + exports
│   └── TranscriptService.java              # PDF generation
├── security/                     # Auth & permissions
│   ├── AuthContext.java          # Résout X-User-Id → CurrentUser
│   ├── CurrentUser.java          # (id, role, studentId?, teacherId?)
│   └── AccessGuard.java          # Vérifications d'accès (ensureCanViewGrade, etc.)
├── jpa/                          # Entités JPA (DB mapping)
│   ├── UserEntity.java
│   ├── StudentEntity.java
│   ├── TeacherEntity.java
│   ├── CourseEntity.java
│   ├── GroupEntity.java
│   ├── PromotionEntity.java
│   ├── GradeEntity.java          # Note courante
│   ├── GradeHistoryEntity.java   # Historique (oldValue, newValue, reason)
│   ├── StudentGroupAssignmentEntity.java
│   ├── CourseTeachingEntity.java
│   └── RoleEntity.java
├── model/                        # Objets métier (sans JPA)
│   ├── Grade.java
│   ├── Student.java
│   └── Role.java (enum)
├── dto/                          # Request/Response DTOs
│   ├── GradeDto.java
│   ├── StudentDto.java
│   └── ...
├── mapper/                       # JPA ↔ Model ↔ DTO conversions
│   ├── GradeMapper.java
│   └── StudentMapper.java
├── repository/                   # Spring Data JPA
│   ├── GradeRepository.java
│   ├── StudentRepository.java
│   └── ...
├── validator/                    # Validations métier (pas de @NotNull/@Valid)
│   ├── GradeValidator.java
│   └── StudentValidator.java
├── exception/                    # Exceptions métier
│   ├── ResourceNotFoundException.java
│   ├── ValidationException.java
│   ├── BusinessException.java
│   ├── UnauthorizedException.java
│   ├── ForbiddenException.java
│   └── GlobalExceptionHandler.java (@ControllerAdvice)
├── excel/
│   └── GraduateExcelExporter.java    # Export Excel (Apache POI)
├── pdf/
│   └── TranscriptPdfGenerator.java   # PDF generation (PDFBox)
├── mail/                             # Template Poja
│   └── Mailer.java (AWS SES)
├── file/bucket/                      # Template Poja
│   └── BucketComponent.java (AWS S3)
└── PojaGenerated.java                # Marker pour code généré

db/migration/
└── V1__init_schema.sql               # Flyway: DDL initial + seed ROLES
```

### Flux de données

#### 1️⃣ Flux Asynchrone (Email)

```
GET /hello?to=user@example.com
    ↓
HelloWorldController.helloWorld()
    ↓ create SendEmailRequested event
EventProducer.accept(List<SendEmailRequested>)
    ├─ validate payload (max 10 items)
    ├─ serialize to JSON
    └─ PUT events → AWS EventBridge
        ↓
    EventBridge → routes to SQS Queue
        ↓
    [Worker] EventConsumer.accept(List<ConsumableEvent>)
        ├─ parallelize with Workers<T>
        └─ for each event:
            ↓
        EventServiceInvoker.accept(TypedEvent)
            ├─ reflection: find Event class
            ├─ instantiate {EventName}Service
            └─ call accept(event)
                ↓
            SendEmailRequestedService.accept(SendEmailRequested)
                ├─ extract recipient email
                ├─ create Email object
                └─ Mailer.accept(email)
                    ↓
                AWS SES → send email
```

#### Flux Transcription (PDF → S3 → Email)

```
POST /api/transcripts/send
    ↓
[Controller] Verify permissions (CurrentUser must be STUDENT/ADMIN)
    ↓
TranscriptService.generateAndSend()
    ├─ fetch student data
    ├─ TranscriptPdfGenerator.generate() → PDF bytes (PDFBox)
    ├─ StorageService.upload() → S3 + presigned URL
    ├─ produce TranscriptEmailRequested event
    └─ EventProducer.accept(List<TranscriptEmailRequested>)
        ↓
[Worker pipeline as above]
    ↓
TranscriptEmailRequestedService.accept(TranscriptEmailRequested)
    ├─ extract recipient + presigned URL
    ├─ create Email with link
    └─ Mailer.accept() → AWS SES
```

#### Flux Modification de Note (Avec Audit)

```
PUT /api/grades/{id}
    ↓
[Controller] Verify permissions (TEACHER or ADMIN + course check)
    ↓
GradeService.updateGrade(gradeId, newValue, reason, authorUserId)
    ├─ load current Grade
    ├─ validate newValue (0-20)
    ├─ GradeService.createHistory()
    │   └─ insert GradeHistory(gradeId, oldValue, newValue, reason, authorUserId, timestamp)
    ├─ update Grade.value = newValue
    ├─ update Grade.updatedAt = now()
    └─ produce GradeModifiedEvent
        ↓
    [Event pipeline]
        ↓
    GradeModifiedEventService
        ├─ notify student (email + presigned transcript link)
        └─ notify teacher (email confirmation)
```

---

## Modèle de données (MCD)

```
┌─────────────┐         ┌───────────┐         ┌──────────────────────┐
│ Promotion   │────────>│   Group   │────────>│ StudentGroupAssignment
│ (promotion) │ 1───N   │  (student │ 1───N   │      (history)
└─────────────┘         │  _group)  │         └──────────┬───────────┘
      │                 └───────────┘                    │
      │                       │ N                        │ N
      │                       │                          │
      │                       │                    ┌──────────────┐
      │                       │                    │   Student    │
      │                       │                    │  (email, STD)│
      │                       │                    └──────┬───────┘
      │                       │                           │ 1
      │                       │                           │
      │                       └───────────────────────────┘

┌─────────────────────────────────────────────┐
│            CourseTeaching                   │
│ (course_id, teacher_id, group_id,          │
│  academic_year, ...)                       │
│ → "teacher X teaches course Y              │
│    to group Z in year 2024"                │
└─────────────────────────────────────────────┘
         ↑         ↑              ↑
         │         │              │
    1───►│    1───►│         1───►│
         │         │              │
    ┌────────┐  ┌────────┐   ┌──────────┐
    │ Course │  │Teacher │   │  Group   │
    └────────┘  └────────┘   └──────────┘

┌──────────────────────────────────────────────┐
│             Grade                            │
│ (student_id, course_id, value, author_user_id,
│  created_at, updated_at)                    │
│ → Student's current grade in course         │
└──────────────────────────────────────────────┘
              │ 1
              │
         N───►│
              │
    ┌──────────────────────────────────────────────┐
    │         GradeHistory                         │
    │ (grade_id, old_value, new_value, reason,    │
    │  author_user_id, changed_at)                │
    │ → Immutable audit trail of all changes      │
    └──────────────────────────────────────────────┘

┌──────────────────────────────────────────────┐
│         User (app_user)                      │
│ (id, email, fullName, role_id,              │
│  student_id?, teacher_id?)                  │
│ → User can be STUDENT, TEACHER, or ADMIN    │
└──────────────────────────────────────────────┘
         │ N
         │
    1───►│
         │
    ┌──────────┐
    │   Role   │
    │ (STUDENT,│
    │  TEACHER,│
    │  ADMIN)  │
    └──────────┘
```

### Choix de modélisation clés

1. **StudentGroupAssignment au lieu de Foreign Key directe**
   - Permet de tracer l'historique des changements de groupe
   - Un étudiant dans une promotion peut changer de groupe → clôturer l'ancienne row, créer une nouvelle
   - `endDate` marque la fin de l'assignment, pas de suppression

2. **CourseTeaching comme entité d'association**
   - Un cours peut être enseigné par plusieurs professeurs
   - Un professeur peut enseigner à plusieurs groupes
   - L'année académique est stockée pour distinguer "cours 2024" vs "cours 2025"

3. **GradeHistory pour audit immuable**
   - Chaque modification de note crée une row GradeHistory
   - JAMAIS de suppression → traçabilité complète
   - Raison obligatoire pour chaque changement

---

## Authentification & Permissions

### Modèle d'authentification

**Il n'y a pas de login/password.** Les clients s'identifient via un en-tête HTTP :

```http
GET /api/grades
X-User-Id: 42
```

Le serveur :
1. Résout l'ID 42 → `UserEntity` (email, role, student_id?, teacher_id?)
2. Crée `CurrentUser(id=42, role=TEACHER, studentId=null, teacherId=5)`
3. Passe `CurrentUser` au contrôleur

### Matrice de permissions

| Action | STUDENT | TEACHER | ADMIN |
|--------|---------|---------|-------|
| **Voir ses propres notes** | ✅ OUI | — | — |
| **Voir notes d'autres étudiants** | ❌ NON | ✅ Si course assignment | ✅ OUI |
| **Modifier notes** | ❌ NON | ✅ Si course assignment<br>+ raison obligatoire | ✅ OUI (raison optionnelle) |
| **Voir transcriptions** | ✅ Ses propres | ✅ Les étudiants de ses cours | ✅ Toutes |
| **Gestion promotions/groupes** | ❌ NON | ❌ NON | ✅ OUI |
| **Export 3 ans (view ranking)** | ❌ NON | ❌ NON | ✅ OUI |
| **Download Excel (Thymeleaf)** | ✅ Vue lecture | ✅ Vue lecture | ✅ API + Vue |

### Implémentation: AccessGuard

```java
// Dans un contrôleur:
@GetMapping("/api/grades/{id}")
public GradeDto getGrade(@PathVariable Long id) {
  CurrentUser user = authContext.getCurrentUser();
  Grade grade = gradeService.findById(id);
  
  // ✅ VÉRIFIER LES PERMISSIONS AVANT D'ACCÉDER
  accessGuard.ensureCanViewGrade(user, grade);
  
  return gradeMapper.toDto(grade);
}

// Sinon: AccessGuard throws ForbiddenException
```

**Pattern important** : Permissions toujours vérifiées au niveau **contrôleur**, AVANT l'appel au service.

### Différence: API vs Thymeleaf

- **API (`/api/...`)**: Requiert `X-User-Id` header + RBAC strict
- **Thymeleaf (`/promotions`)**: Pas d'auth (browser ne peut pas envoyer header custom)
  - Solution: `GraduateService.rankPromotionForView()` retourne une vue lecture-seule
  - Les données sensibles sont omises (pas d'access control)

---

## 🚀 Installation & Exécution

### Prérequis

- **JDK 21+** ([télécharger](https://www.oracle.com/java/technologies/downloads/))
- **PostgreSQL 14+** ([installer](https://www.postgresql.org/download/))
- **AWS credentials** configurées localement (pour S3, SES, EventBridge)
  - Fichier `~/.aws/credentials` OU variables `AWS_ACCESS_KEY_ID` / `AWS_SECRET_ACCESS_KEY`
  - OU rôle IAM si exécuté sur EC2/Lambda

### Setup initial

```bash
# 1. Cloner le repo
git clone https://github.com/deep-awak/school-bulletin.git
cd school-bulletin

# 2. Créer la base de données PostgreSQL
createdb school_bulletin

# 3. Configurer les variables d'environnement
export DB_URL=jdbc:postgresql://localhost:5432/school_bulletin
export DB_USERNAME=postgres
export DB_PASSWORD=postgres
export AWS_S3_BUCKET=my-transcripts-bucket
export AWS_EVENT_BUS=default
export AWS_EVENT_STACK_1_SQS_QUEUE_URL=https://sqs.eu-west-3.amazonaws.com/123456789/queue-1
export AWS_EVENT_STACK_2_SQS_QUEUE_URL=https://sqs.eu-west-3.amazonaws.com/123456789/queue-2

# 4. Démarrer l'application
./gradlew bootRun
```

**Logs d'attente** :

- ✅ `Flyway successfully validated schema` → migrations OK
- ✅ `Inserting: ROLE STUDENT` → seed roles OK
- ✅ `Tomcat started on port 8080` → serveur prêt

### Base de données

- **Migration**: Flyway exécute `src/main/resources/db/migration/V1__init_schema.sql` au démarrage
- **Données de seed**: Les 3 rôles (`STUDENT`, `TEACHER`, `ADMIN`) sont créés automatiquement
- **Validation Hibernate**: `spring.jpa.hibernate.ddl-auto=validate` → Flyway est la source de vérité, Hibernate vérifie seulement

---

## 📡 API & Endpoints

### Endpoints actuels

Voir `api.yaml` pour la spécification OpenAPI complète.

#### Students (`/api/students`)

```http
GET    /api/students              # List all students (ADMIN only)
GET    /api/students/{id}         # Get student (ADMIN or STUDENT={id})
POST   /api/students              # Create (ADMIN only)
PUT    /api/students/{id}         # Update (ADMIN only)
DELETE /api/students/{id}         # Delete (ADMIN only)
```

#### Grades (`/api/grades`)

```http
GET    /api/grades/{id}           # Get grade (ADMIN, TEACHER if assigned, STUDENT if own)
PUT    /api/grades/{id}           # Update grade (ADMIN, TEACHER if assigned)
                                  # Request body must include: reason (obligatoire)
GET    /api/grades/{id}/history   # View GradeHistory (ADMIN, TEACHER, STUDENT own only)
```

#### Transcripts (`/api/transcripts`)

```http
GET    /api/transcripts/{studentId}   # Download PDF (ADMIN or STUDENT own)
POST   /api/transcripts/send           # Generate + email (ADMIN or STUDENT own)
                                        # Produces TranscriptEmailRequested event
```

#### Promotions (`/api/promotions`)

```http
GET    /api/promotions                 # List (ADMIN only)
GET    /api/promotions/{id}            # Get (ADMIN only)
GET    /api/promotions/{id}/graduates/export  # Excel (ADMIN only)
```

#### Test Health

```http
GET    /health                    # Spring Boot Actuator
GET    /hello?to=user@email.com  # Test event system (HelloWorldController)
GET    /health/email?to=...      # Test AWS SES (HealthEmailController)
```

---

## 🔄 Flux de travail clés

### 1. Ajouter une note

```bash
# Pré-requis: 
#   - CourseTeaching existe (teacher enseigne le cours au groupe de l'étudiant)
#   - StudentGroupAssignment actuelle (étudiant est dans le groupe)

curl -X PUT http://localhost:8080/api/grades/123 \
  -H "X-User-Id: 5" \
  -H "Content-Type: application/json" \
  -d '{
    "value": 16.5,
    "reason": "Évaluation finale - très bon travail"
  }'

# Résultat:
#   ✅ Grade.value = 16.5, updatedAt = now
#   ✅ GradeHistory: oldValue=14, newValue=16.5, reason=..., authorUserId=5, changedAt=now
#   ✅ Événement GradeModifiedEvent produit
#   ✅ Email asynchrone envoyé à l'étudiant
```

### 2. Télécharger une transcription

```bash
# STUDENT télécharge son propre PDF
curl http://localhost:8080/api/transcripts/12 \
  -H "X-User-Id: 12" \
  > transcript.pdf

# ADMIN télécharge le PDF de n'importe quel étudiant
curl http://localhost:8080/api/transcripts/12 \
  -H "X-User-Id: 1" \
  > transcript.pdf

# ❌ STUDENT=12 essaye de télécharger STUDENT=99:
#    403 Forbidden (ForbiddenException)
```

### 3. Envoyer une transcription par email

```bash
POST /api/transcripts/send
X-User-Id: 12
Content-Type: application/json

{
  "studentId": 12,
  "email": "student@example.com"
}

# Événement TranscriptEmailRequested produit
# → EventBridge → SQS Queue
# → TranscriptEmailRequestedService (worker)
# → PDF généré + uploadé S3
# → Email avec lien presigned URL envoyé
```

### 4. Exporter les résultats (Excel)

```bash
# API (ADMIN only, retourne le fichier téléchargé)
GET /api/promotions/5/graduates/export
X-User-Id: 1  # ADMIN

# Thymeleaf (pas d'auth, mais vue lecture-seule)
GET /promotions/5/export
# → Browser → Excel download

# Classement: Rang | STD | Nom | Prénom | Moyenne générale
```

---

## 📨 Gestion des événements

### Architecture événementielle

1. **Producteur** (contrôleur)
   - Crée un événement métier (ex: `SendEmailRequested`)
   - Appelle `EventProducer.accept(List<Event>)`
   
2. **EventProducer** (composant)
   - Batch les événements (max 10 par requête AWS)
   - Sérialise en JSON
   - Envoie vers AWS EventBridge
   
3. **EventBridge** (AWS)
   - Route l'événement vers les SQS Queues
   - Stocke temporairement
   
4. **EventConsumer** (worker)
   - Lit les messages de la queue
   - Parallélise avec `Workers<T>`
   
5. **EventServiceInvoker** (dispatcher)
   - Reflection pour trouver `{EventName}Service`
   - Instancie + appelle `.accept(event)`
   
6. **Service consommateur** (ex: `SendEmailRequestedService`)
   - Traite l'événement
   - Envoie email, sauvegarde, etc.

### Événements implémentés

#### SendEmailRequested

```java
public class SendEmailRequested extends PojaEvent {
  private String to;  // Email du destinataire
  // BUG: subject et body sont hardcodés → nécessite amélioration
}

// Consumer: SendEmailRequestedService
// → Mailer.accept() → AWS SES
```

#### TranscriptEmailRequested

```java
public class TranscriptEmailRequested extends PojaEvent {
  private String studentId;
  private String recipientEmail;
  private String presignedUrl;  // S3 link
}

// Consumer: TranscriptEmailRequestedService
// → Email avec lien presigned + attachment
```

#### GradeModifiedEvent (exemple)

```java
public class GradeModifiedEvent extends PojaEvent {
  private Long gradeId;
  private Double oldValue;
  private Double newValue;
  private String reason;
  private Long authorUserId;
  private Instant changedAt;
}

// Consumer: GradeModifiedEventService
// → Notify student + teacher
```

### Convention de nommage Poja

**Important** : Le nom du service consommateur DOIT respecter la convention :

```
{EventClassName}Service

Exemple:
  Event: SendEmailRequested
  Service: SendEmailRequestedService
  Package: api.poja.app.service.event

  Event: GradeModifiedEvent
  Service: GradeModifiedEventService
```

Si la classe est mal nommée ou dans le mauvais package, `EventServiceInvoker` ne la trouvera PAS et les événements s'accumuleront dans la queue ! 🚨

---

## 🧪 Tests

```bash
# Exécuter tous les tests
./gradlew clean test

# Générer rapport couverture (jacoco)
./gradlew jacocoTestReport
# Résultat: build/reports/jacoco/test/html/index.html
```

### Tests unitaires existants

| Package | Classe | Couverture |
|---------|--------|-----------|
| `validator/` | `GradeValidatorTest` | Validation métier notes |
| | `StudentValidatorTest` | Validation étudiants |
| `security/` | `AccessGuardTest` | Self-access, staff access, course checks |
| `service/` | `GradeServiceTest` | History creation, forbidden teacher |
| | `StudentGroupAssignmentServiceTest` | Reassignment closes old row |
| | `GraduateServiceTest` | Ranking order |
| | `GraduateServiceViewRankingTest` | Admin vs Thymeleaf bypass |
| `excel/` | `GraduateExcelExporterTest` | Headers + rows |

### Écrire un test

```java
// Exemple: GradeServiceTest
@Test
void givenStudentGrade_whenModify_thenHistoryCreated() {
  // Arrange
  Grade grade = Grade.builder().value(14).studentId(1L).build();
  given(gradeRepository.findById(123)).willReturn(Optional.of(grade));
  
  // Act
  Grade updated = gradeService.updateGrade(123, 16.5, "Good work", 5);
  
  // Assert
  assertThat(updated.getValue()).isEqualTo(16.5);
  verify(gradeHistoryRepository).save(
    argThat(h -> h.getOldValue() == 14 && h.getNewValue() == 16.5)
  );
}
```

---

## 🔧 Variables d'environnement

| Variable | Description | Défaut | Requis |
|----------|-------------|--------|---------|
| `DB_URL` | JDBC PostgreSQL | `jdbc:postgresql://localhost:5432/school_bulletin` | ❌ (local) |
| `DB_USERNAME` | User DB | `postgres` | ❌ (local) |
| `DB_PASSWORD` | Password DB | `postgres` | ❌ (local) |
| `AWS_S3_BUCKET` | S3 bucket pour transcriptions | — | ✅ |
| `AWS_EVENT_BUS` | EventBridge bus name | `default` | ❌ |
| `AWS_EVENT_STACK_1_SQS_QUEUE_URL` | Queue 1 SQS URL | — | ✅ (si utilisée) |
| `AWS_EVENT_STACK_2_SQS_QUEUE_URL` | Queue 2 SQS URL | — | ✅ (si utilisée) |
| `AWS_REGION` | Region AWS | `eu-west-3` | ❌ |
| `AWS_ACCESS_KEY_ID` | AWS credentials | (from chain) | ✅ (prod) |
| `AWS_SECRET_ACCESS_KEY` | AWS credentials | (from chain) | ✅ (prod) |

**Chaîne de configuration AWS** :
1. Variables `AWS_ACCESS_KEY_ID` / `AWS_SECRET_ACCESS_KEY`
2. Fichier `~/.aws/credentials`
3. Rôle IAM (EC2, Lambda, ECS)

---

## 🐛 Bugs & Recommandations

### Bugs critiques 🚨

#### 1. EventProducer.checkResponse() — Toutes les entrées marquées comme succès

**Fichier** : `src/main/java/api/poja/app/endpoint/event/EventProducer.java` (ligne ~87)

```java
// ❌ BUG
private void checkResponse(PutEventsResponse response) {
  List<PutEventsResultEntry> failedEntries = new ArrayList<>();
  List<PutEventsResultEntry> successfulEntries = new ArrayList<>();

  for (PutEventsResultEntry resultEntry : response.entries()) {
    if (resultEntry.eventId() == null) {
      failedEntries.add(resultEntry);
    }
    successfulEntries.add(resultEntry);  // ← TOUJOURS ajouté!
  }

  if (!failedEntries.isEmpty()) {
    log.error("Following events were not successfully sent: {}", failedEntries);
  }
  if (!successfulEntries.isEmpty()) {
    log.info("Following events were successfully sent: {}", successfulEntries);
  }
}

// ✅ FIX
private void checkResponse(PutEventsResponse response) {
  List<PutEventsResultEntry> failedEntries = new ArrayList<>();
  List<PutEventsResultEntry> successfulEntries = new ArrayList<>();

  for (PutEventsResultEntry resultEntry : response.entries()) {
    if (resultEntry.eventId() != null) {
      successfulEntries.add(resultEntry);
    } else {
      failedEntries.add(resultEntry);
    }
  }

  if (!failedEntries.isEmpty()) {
    log.error("Failed to send {} events: {}", failedEntries.size(), failedEntries);
    // TODO: Implémenter retry ou dead-letter queue
  }
  if (!successfulEntries.isEmpty()) {
    log.info("Successfully sent {} events", successfulEntries.size());
  }
}
```

**Impact** : Les événements échoués sont loggés comme succès → perte de données

---

#### 2. SendEmailRequestedService — Subject & body hardcodés

**Fichier** : `src/main/java/api/poja/app/service/event/SendEmailRequestedService.java`

```java
// ❌ BUG
@Override
public void accept(SendEmailRequested sendEmailRequested) {
  InternetAddress recipientAddress = new InternetAddress(sendEmailRequested.getTo());
  mailer.accept(new Email(
      recipientAddress,
      List.of(),
      List.of(),
      "",                    // ← SUBJECT VIDE!
      "... world!",          // ← BODY HARDCODÉ!
      List.of()
  ));
}

// ✅ FIX
public class SendEmailRequested extends PojaEvent {
  private String to;
  private String subject;    // AJOUTER
  private String body;       // AJOUTER
  // ...
}

@Override
public void accept(SendEmailRequested event) {
  try {
    InternetAddress recipientAddress = new InternetAddress(event.getTo());
    mailer.accept(new Email(
        recipientAddress,
        List.of(),
        List.of(),
        event.getSubject(),  // Utiliser
        event.getBody(),     // Utiliser
        List.of()
    ));
    log.info("Email sent to: {}", event.getTo());
  } catch (Exception e) {
    log.error("Failed to send email", e);
    throw new RuntimeException("Email sending failed", e);
  }
}
```

**Impact** : Tous les emails envoyés ont un sujet vide et un corps "... world!"

---

### Améliorations recommandées 💡

#### 1. Ajouter validation des variables d'environnement au démarrage

```java
@Component
public class EnvironmentValidator {
  
  @PostConstruct
  public void validate() {
    String[] required = {
        "AWS_S3_BUCKET",
        "AWS_EVENT_STACK_1_SQS_QUEUE_URL",
        "AWS_EVENT_STACK_2_SQS_QUEUE_URL"
    };
    
    for (String var : required) {
      if (System.getenv(var) == null) {
        throw new IllegalStateException("Missing required env var: " + var);
      }
    }
  }
}
```

#### 2. Implémenter retry policy et dead-letter queue

```java
private void checkPayload(List<T> events) {
  if (!isPayloadValid(events)) {
    // Stocker dans dead-letter table
    gradeHistoryService.logFailedEvent(events);
    
    // Thrower exception pour SQS retry
    throw new EventProcessingException("Payload too large");
  }
}

@Transactional
public void retryFailedEvents() {
  List<FailedEvent> failed = failedEventRepository.findAll();
  for (FailedEvent fe : failed) {
    try {
      EventProducer.accept(List.of(fe.getEvent()));
      failedEventRepository.delete(fe);
    } catch (Exception e) {
      fe.setRetryCount(fe.getRetryCount() + 1);
      if (fe.getRetryCount() > 3) {
        fe.setStatus(Status.ABANDONED);
        alertAdmin("Event abandoned: " + fe);
      }
    }
  }
}
```

#### 3. Centraliser les vérifications d'accès avec @Secured

```java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequireCanViewGrade {}

@Aspect
@Component
public class SecurityAspect {
  
  @Before("@annotation(RequireCanViewGrade) && args(id, ..)")
  public void checkViewGradeAccess(Long id) {
    CurrentUser user = authContext.getCurrentUser();
    Grade grade = gradeService.findById(id);
    accessGuard.ensureCanViewGrade(user, grade);
  }
}

// Utilisation:
@GetMapping("/api/grades/{id}")
@RequireCanViewGrade
public GradeDto getGrade(@PathVariable Long id) {
  // Permissions déjà vérifiées par l'aspect
  return mapper.toDto(gradeService.findById(id));
}
```

#### 4. Ajouter logging structuré (JSON)

```java
// Utiliser Logback JSON encoder
log.info("grade.modified", 
  Map.of(
    "gradeId", grade.getId(),
    "oldValue", oldValue,
    "newValue", grade.getValue(),
    "authorUserId", author.getId(),
    "reason", reason,
    "timestamp", Instant.now()
  )
);
```

#### 5. Ajouter monitoring & alertes

```java
@Component
@Scheduled(fixedRate = 60000) 
public void monitorEventQueue() {
  long failedCount = failedEventRepository.count();
  if (failedCount > 100) {
    alertService.sendAlert("High failed event count: " + failedCount);
  }
}
```

---

## 📚 Documentation

- **OpenAPI/Swagger**: `api.yaml` — spécification complète de tous les endpoints
- **Postman Collection**: `postman.json` — tests end-to-end avec variables
- **Flyway Migrations**: `src/main/resources/db/migration/` — DDL et seed
- **Javadoc**: Documenté via commentaires dans le code

---

## 🤝 Contribuer

### Avant de committer

1. ✅ Exécuter les tests : `./gradlew test`
2. ✅ Vérifier la couverture : `./gradlew jacocoTestReport`
3. ✅ Respecter la structure packages existante
4. ✅ Ajouter des commentaires pour les logiques complexes

### Convention de nommage

- **Classes**: `PascalCase` (ex: `GradeService`, `StudentDto`)
- **Méthodes**: `camelCase` (ex: `findById`, `updateGrade`)
- **Constantes**: `UPPER_SNAKE_CASE` (ex: `MAX_EVENTS_FOR_PUT_REQUEST`)
- **Packages**: 
  - `endpoint/event/model/` pour événements métier
  - `service/event/` pour consommateurs
  - `security/` pour auth & permissions

### Checklist d'une nouvelle feature

- [ ] Ajouter entité JPA + migration Flyway
- [ ] Créer mapper (JPA ↔ DTO)
- [ ] Implémenter service métier + tests
- [ ] Créer contrôleur REST + vérifications d'accès
- [ ] Ajouter endpoint à `api.yaml`
- [ ] Tester avec Postman collection
- [ ] Documenter variables d'environnement si besoin
- [ ] Passer la couverture de tests à ≥ 80%

---

## 📞 Support & Troubleshooting

### L'application ne démarre pas

```bash
# Erreur: "Failed to connect to PostgreSQL"
→ Vérifier DB_URL, DB_USERNAME, DB_PASSWORD
→ Vérifier que PostgreSQL est en cours d'exécution: psql --version

# Erreur: "Missing env var: AWS_S3_BUCKET"
→ Exporter: export AWS_S3_BUCKET=my-bucket

# Erreur: "EventBridge PutEvents failed"
→ Vérifier AWS credentials: aws sts get-caller-identity
→ Vérifier permissions IAM (eventbridge:PutEvents)
```

### Les emails ne sont pas envoyés

```bash
# Vérifier les logs de EventConsumer:
# grep "EventConsumer\|SendEmailRequestedService" logs/*.log

# Vérifier les événements dans la queue:
# AWS SQS → queue URL → "Send and receive messages" → voir messages en attente

# Vérifier AWS SES:
# AWS SES → Email Addresses → vérifier que l'adresse est approuvée/sandbox
```

### Les notes ne sont pas mises à jour

```bash
# Vérifier les permissions (AccessGuard):
# curl -X PUT /api/grades/123 -H "X-User-Id: 5" \
#   -d '{"value": 16, "reason": "..."}'
# → Si 403 Forbidden: vérifier que le professeur est assigné au cours

# Vérifier l'historique:
# GET /api/grades/123/history -H "X-User-Id: 5"
```

---

## 📄 Licence

Ce projet est une étude de cas académique PROG4/SYS3. Basé sur le template [Poja](https://poja.io).

---

**Dernière mise à jour** : Août 2026 | Branch : `preprod`
