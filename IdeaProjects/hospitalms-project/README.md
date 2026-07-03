# Hospital Management System — SheCanCode Bootcamp

RESTful Hospital Management System built with Spring Boot 3.2.5, Java 21,
Spring Security (JWT), Hibernate/JPA, PostgreSQL, DTOs, MapStruct, and Bean Validation.

## 1. Setup

1. Create a PostgreSQL database:
   ```sql
   CREATE DATABASE hospitalms_db;
   ```
2. Update `src/main/resources/application.yml` with your PostgreSQL username/password
   (defaults to `postgres` / `postgres`).
3. Run the app:
   ```bash
   mvn spring-boot:run
   ```
   Tables are auto-created via `hibernate.ddl-auto: update`.

## 2. Typical flow to test

1. `POST /api/hospitals/register` — registers a hospital + its administrator (public).
2. `POST /api/auth/login` — login as admin (or doctor/patient) to get a JWT.
   Send `Authorization: Bearer <token>` on all further requests.
3. `POST /api/services` (ADMIN) — create hospital services (e.g. Pediatrics).
4. `POST /api/doctors` (ADMIN) — create a doctor, assigning `serviceIds`.
5. `POST /api/patients/register` — a patient self-registers (public).
6. `POST /api/appointments` (PATIENT) — book an appointment.
7. `PATCH /api/appointments/{id}/approve` (ADMIN) — approve it.
8. `POST /api/diagnoses/appointments/{id}` (DOCTOR, multipart/form-data:
   `request` part = JSON, `prescriptionFile` part = PDF) — diagnose the patient.
9. `PATCH /api/appointments/{id}/complete` (DOCTOR) — mark completed (diagnosis becomes read-only).
10. `GET /api/reports/...` (ADMIN) — view reports.

## 3. Package structure

```
entity/          JPA entities (Hospital, Administrator, Doctor, Patient,
                  HospitalService, Appointment, Diagnosis) + enums/
repository/      Spring Data JPA repositories
security/        JWT generation/validation, filter, UserDetailsService, SecurityConfig
dto/             Request/response DTOs per module
mapper/          MapStruct entity <-> DTO mappers
service/         Business logic + validation rules
controller/      REST endpoints
exception/       Custom exceptions + global exception handler
config/          JPA auditing (createdBy/updatedBy) configuration
```

## 4. Notes / things to double check before submission

- `app.jwt.secret` in `application.yml` is a placeholder — fine for coursework,
  but call out in your report that it should be an environment variable in production.
- Reports are intentionally simple (counts/aggregates) — extend with date-range
  query params if your rubric wants more granularity.
- Uploaded prescription PDFs are stored on local disk under `uploads/prescriptions/`
  (configurable via `app.file.upload-dir`), and only the file path is stored in DB.
- Role-based access is enforced in two layers: `SecurityConfig` (URL-level) and
  inside each service method (ownership checks, e.g. a patient can only touch their own record).
