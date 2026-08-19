# poja-async-mailing-template — async email workers for Spring Boot

A [Poja](https://poja.io) starter template with **SQS-powered async email sending** pre-configured. Define an event, wire a consumer, push — no queue infrastructure to manage.

→ **[Full guide on docs.poja.io](https://docs.poja.io/docs/hello-world-but-with-asynchronous-reply-by-email)**

Or hit the `Deploy to Poja` button to **deploy this template on your account** : 


[![Deploy on Poja](https://img.shields.io/badge/Deploy%20On%20Poja-007BFF?style=for-the-badge)](https://console.poja.io/applications/create/clone/?templateId=84df308f-8da6-4b70-a83f-0b146b1b8e5f)

---

### What you get

Two classes to write. Poja handles the queue, the worker, and the retries.

```java
// 1. The event — in endpoint.event.model
public class SendEmailRequested extends PojaEvent {
  private String to;

  @Override public Duration maxConsumerDuration() { return Duration.ofSeconds(45); }
  @Override public Duration maxConsumerBackoffBetweenRetries() { return Duration.ofSeconds(30); }
}

// 2. The consumer — in service.event (must be named {EventName}Service)
@Service @AllArgsConstructor
public class SendEmailRequestedService implements Consumer<SendEmailRequested> {
  private final Mailer mailer;

  @Override
  public void accept(SendEmailRequested event) {
    mailer.accept(new Email(new InternetAddress(event.getTo()),
        List.of(), List.of(), "Subject", "Body", List.of()));
  }
}
```

Produce the event from any controller — Poja routes it to the worker automatically.

> Part of the [Poja platform](https://poja.io) — deploy Spring Boot in minutes.

---

## HEI Graduate Management module (PROG4/SYS3 exam project)

Everything below this line documents the academic domain built on top of the
async-mailing template above: students, teachers, courses, groups, promotions,
grades with a mandatory-reason history, transcripts, and the graduate export.

### Architecture

The template's `handler/`, `endpoint/event/`, `mail/`, `file/bucket/` packages
(async email + S3) are reused as-is. The academic module adds:

```
jpa/         persistence entities only (StudentEntity, GradeEntity, ...)
model/       plain business objects, no JPA/Jackson annotations
dto/         request/response shapes returned by endpoints (never a jpa/ entity)
mapper/      jpa <-> model <-> dto conversions
repository/  Spring Data JPA interfaces
validator/   manual validation (no Bean Validation annotations anywhere)
exception/   ResourceNotFoundException, ValidationException, BusinessException,
             UnauthorizedException, ForbiddenException + a @ControllerAdvice
service/     all business logic
security/    AuthContext (resolves X-User-Id), CurrentUser, AccessGuard (roles)
endpoint/    thin @RestController/@Controller classes only
excel/       GraduateExcelExporter (Apache POI)
pdf/         TranscriptPdfGenerator (PDFBox)
```

### MCD (entity-relationship model)

```
Promotion 1───N Group 1───N StudentGroupAssignment N───1 Student
                                                          │
Course N───N Teacher  (through CourseTeaching, which also carries a Group   1
   and an academicYear, so a course can be taught to some groups only)      │
                                                                              │
Student 1───N Grade N───1 Course                                            │
Grade 1───N GradeHistory (oldValue, newValue, reason, author, changedAt)     │
                                                                              │
Role 1───N User (User optionally links to a Student or a Teacher row) ──────┘
```

Key modelling decisions:
- **Student ↔ Group is never a direct foreign key.** A student's group
  membership over time is tracked by `StudentGroupAssignment` rows
  (student, group, startDate, endDate). Moving a student closes the current
  row (`endDate` set) and opens a new one - history is never overwritten.
- **CourseTeaching** is the association entity for "this teacher gives this
  course to this group, this academic year" - a course can have several
  teachers and be taught to only some of its promotion's groups.
- **Grade** always keeps its current value; every change also appends a
  **GradeHistory** row with the old value, new value, a mandatory reason, the
  author and the timestamp - nothing is ever deleted.

### Authentication & permissions

There is **no login/password flow** in this exercise. Callers identify
themselves with an `X-User-Id` header naming an existing `app_user` row;
`AuthContext` resolves it into a `CurrentUser` (id, role, linked student/teacher
id) and `AccessGuard` enforces, per request:

| Role    | Can |
|---------|-----|
| STUDENT | see only their own grades and transcripts |
| TEACHER | see/modify grades only for courses they are assigned to teach (`CourseTeaching`); every modification requires a reason |
| ADMIN   | everything, including promotion rankings and the 3-year results view |

The Thymeleaf pages (`/promotions`, `/promotions/{id}`) have no auth of their
own - a plain browser link can't send a custom header - so their Excel
download goes through a separate, unauthenticated view-only path
(`GraduateService.rankPromotionForView`). The `/api/...` export stays
ADMIN-gated. See `security/AccessGuard.java` and `endpoint/PromotionViewEndpoint.java`.

### Transcript flow (PDF → S3 → async email)

```
POST /api/transcripts/send
  -> TranscriptService
  -> TranscriptPdfGenerator (PDFBox)
  -> StorageService (wraps the template's BucketComponent) -> S3, presigned URL
  -> TranscriptEmailRequested event
  -> the template's existing EventProducer/EventBridge/EventConsumer pipeline
  -> TranscriptEmailRequestedService (Consumer<TranscriptEmailRequested>)
  -> Mailer -> SES
```
No parallel email system was built; `TranscriptEmailRequestedService` follows
the exact same `{EventName}Service` convention as the template's own
`SendEmailRequestedService`.

### Running it locally

Requirements: JDK 21, a PostgreSQL instance, AWS credentials configured
locally (or the app will fail at S3/EventBridge client construction), and
network access to Maven Central (unlike the environment this was written in,
which could not reach Maven Central to actually run `./gradlew`).

```bash
createdb school_bulletin   # or point DB_URL at an existing instance

export DB_URL=jdbc:postgresql://localhost:5432/school_bulletin
export DB_USERNAME=postgres
export DB_PASSWORD=postgres
export AWS_S3_BUCKET=your-bucket-name
export AWS_EVENT_BUS=default   # or your EventBridge bus name

./gradlew bootRun
```

Flyway runs `src/main/resources/db/migration/V1__init_schema.sql`
automatically on startup and seeds the three roles (`STUDENT`, `TEACHER`,
`ADMIN`). `spring.jpa.hibernate.ddl-auto` is `validate`: Flyway is the single
source of truth for the schema, Hibernate only checks the entities match it.

No AWS credentials are ever hardcoded - `BucketConf`/`EmailConf` (existing
template code) and the datasource all read from environment variables, with
local-friendly defaults for the database only.

### Tests

```bash
./gradlew clean test
./gradlew jacocoTestReport   # if the jacoco plugin is applied in your checkout
```

Unit tests (Mockito, no Spring context) cover:
- `validator/`: `GradeValidatorTest`, `StudentValidatorTest`, `AssignmentValidatorTest`
- `security/`: `AccessGuardTest` (self-access, staff access, course-teacher checks)
- `service/`: `GradeServiceTest` (history creation, forbidden for an
  unassigned teacher, student self-access only), `StudentGroupAssignmentServiceTest`
  (reassignment closes the old row instead of overwriting it), `GraduateServiceTest`
  and `GraduateServiceViewRankingTest` (ranking order, admin-only vs. the
  Thymeleaf bypass)
- `excel/`: `GraduateExcelExporterTest` (mandatory column headers and rows)

**Honest caveat:** this project was built in an environment whose network
egress is restricted to GitHub/npm/PyPI/crates.io - Maven Central and
`services.gradle.org` are unreachable, so `./gradlew` could not actually be
run here. Every file was reviewed by hand against the exact method
signatures it calls, and two real, previously-undetected bugs were found and
fixed this way (a Flyway/JPA table-name mismatch, and a wrong AWS S3 bucket
property key - see the `fix(persistence)` and `fix(config)` commits). Run
`./gradlew clean test build` yourself before treating this as verified green.

### Environment variables

| Variable | Purpose | Required |
|----------|---------|----------|
| `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` | PostgreSQL connection | yes (defaults to `localhost:5432/school_bulletin` / `postgres` / `postgres`) |
| `AWS_S3_BUCKET` | Bucket transcripts are uploaded to | yes, no default - startup fails without it |
| `AWS_EVENT_BUS` | EventBridge bus for the async email events | no, defaults to `default` |
| AWS credentials (SDK default chain) | Needed by `BucketConf`, `EmailConf`, `EventConf` (S3, SES, EventBridge clients) | yes, for anything touching transcripts or email |

### API docs & Postman

- [`api.yaml`](./api.yaml) - OpenAPI 3.0, one entry per real endpoint in
  `endpoint/`, nothing documented that doesn't exist in the code.
- [`postman.json`](./postman.json) - a matching collection, grouped the same
  way, using `baseUrl`/`userId`/`studentId`/`teacherId`/`courseId`/`groupId`/
  `promotionId`/`gradeId` variables for an end-to-end walkthrough.

### Excel & Thymeleaf

`GraduateExcelExporter` produces one sheet with headers `Rang | STD | Nom |
Prénom | Moyenne générale`, available both from the API
(`GET /api/promotions/{id}/graduates/export`, ADMIN-only) and from the
Thymeleaf UI (`GET /promotions/{id}/export`, no auth). The Thymeleaf pages
themselves (`GET /promotions`, `GET /promotions/{id}`) are deliberately plain
HTML tables with no CSS framework, per the brief.

