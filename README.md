# Colatail — Pet Hospital Management System

An internal management system for pet hospitals to handle customers, pets, appointments, case records, and wellness tracking. Secured with JWT authentication and optional TOTP two-factor authentication.

## Tech Stack

| Layer | Technology |
|-------|------------|
| Backend | Java 17 · Spring Boot 3.1 · Spring Data JPA · Spring Security 6 |
| Auth | JWT (JJWT 0.12) · TOTP MFA (Google Authenticator compatible) |
| Database | PostgreSQL 16 |
| Frontend | React 18 · Vite · Nginx |
| Styling | Tailwind CSS |
| Containerization | Docker · Docker Compose |

## Project Structure

```
.
├── docker-compose.yml
├── Backend/
│   ├── Dockerfile
│   ├── pom.xml
│   └── src/main/java/com/example/backend/
│       ├── Appointment/      # Appointment entity & repository
│       ├── Customer/         # Person, Pet, CaseRecord, WellnessLog
│       ├── doctor/           # Doctor entity & repository
│       ├── auth/             # SystemUser, AuditLog, JWT, TOTP, Security config
│       ├── Controller/       # REST controllers
│       └── config/           # CORS configuration
└── frontend/
    ├── Dockerfile
    ├── nginx.conf
    └── src/
        ├── api.js            # Axios API wrappers (JWT-injecting)
        ├── AuthContext.jsx   # Auth state & token management
        ├── components/       # Layout, Modal
        └── pages/            # Login, Dashboard, Customers, Appointments, Admin
```

---

## Running with Docker (recommended)

### Prerequisites

- [Docker](https://docs.docker.com/get-docker/)
- [Docker Compose](https://docs.docker.com/compose/install/) (included with Docker Desktop)

### Start everything

```bash
docker compose up --build
```

Docker will:
1. Pull the PostgreSQL 16 image and start the database
2. Build the Spring Boot backend and start it (waits for the database to be healthy)
3. Build the React app with Vite and serve it through Nginx

Once all three containers are running, open **http://localhost** in your browser.

| Service | URL |
|---------|-----|
| Frontend | http://localhost |
| PostgreSQL | localhost:5432 |

> The backend API is not exposed on the host — it is only accessible through the Nginx reverse proxy at `/api/*`.

> Tables are created automatically on first start — no manual migration needed.

### Default admin account

On first startup the system automatically creates an administrator account:

| Field | Value |
|-------|-------|
| Username | `usermanage` |
| Password | `usermanage` |
| Role | `ADMIN` |

**Change this password immediately after your first login** via Admin Panel → My Account → Change Password.

### Stop

```bash
docker compose down
```

Database data is stored in a named volume (`postgres_data`) and persists between restarts. To also delete the data:

```bash
docker compose down -v
```

### Rebuild after code changes

```bash
docker compose up --build
```

---

## Running Locally (without Docker)

### Prerequisites

- Java 17+
- Maven 3.8+
- PostgreSQL 16+
- Node.js 18+

### 1. Set up PostgreSQL

```bash
sudo -u postgres psql
```

```sql
CREATE DATABASE colatail;
CREATE USER colatail_user WITH PASSWORD 'colatail123';
GRANT ALL PRIVILEGES ON DATABASE colatail TO colatail_user;
\q
```

### 2. Start the backend

```bash
cd Backend
mvn spring-boot:run
```

Backend runs on `http://localhost:8080`.

### 3. Start the frontend

```bash
cd frontend
npm install
npm run dev
```

Frontend runs on `http://localhost:5173` (Vite proxies `/api/*` to the backend automatically).

---

## Configuration

| Variable | Default | Description |
|----------|---------|-------------|
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://localhost:5432/colatail` | JDBC connection URL |
| `SPRING_DATASOURCE_USERNAME` | `colatail_user` | Database user |
| `SPRING_DATASOURCE_PASSWORD` | `colatail123` | Database password |
| `JWT_SECRET` | *(default, insecure)* | HS256 signing secret — **change in production** |

To override in Docker, set these in `docker-compose.yml` under the `backend` service's `environment` block.

---

## Roles & Access Control

| Role | Permissions |
|------|-------------|
| **STAFF** | View and manage customers, pets, appointments, case records, wellness logs |
| **ADMIN** | Everything STAFF can do, plus: delete customers/pets, manage users, manage doctors, view audit log |

All API endpoints (except `/api/auth/login`) require a valid JWT Bearer token.

---

## Operational Workflows

### 1. New Patient Registration

```
Staff opens Customers page
  → Click "+ Add Customer"
  → Fill in name, email, phone, address → Save
  → Open the new customer's detail page
  → Click "+ Add" in the Pets panel
  → Fill in pet name, breed, gender, birthday → Save
```

### 2. Booking an Appointment

```
Staff opens Appointments page
  → Click "+ New Appointment"
  → Select customer → pet list filters automatically
  → Select pet, doctor, date & time
  → Status defaults to PENDING → Save
```

### 3. Visit Workflow (Day of Appointment)

```
Staff finds the appointment on the Dashboard (Today's Appointments)
  or filters Appointments page by today's date

  During check-in:
    → Change appointment status to CONFIRMED

  After the visit:
    → Open Customer Detail → select the pet
    → Click "+ Add Record" in the Case Records tab
    → Select the attending doctor
    → Fill in Chief Complaint, Diagnosis, Prescription, Notes → Save
    → Change appointment status to COMPLETED
```

### 4. Wellness Tracking

```
Staff opens Customer Detail → selects a pet
  → Switch to "Wellness Logs" tab
  → Click "+ Add Log"
  → Select type: WEIGHT / VACCINE / DEWORMING / OTHER
  → Enter value (e.g. "5.2 kg" or "Rabies booster") and optional notes → Save
```
Logs are shown newest-first so the latest weight or vaccine date is always at the top.

### 5. Onboarding a New Staff Member (Admin only)

```
Admin opens Admin Panel → Users tab
  → Click "+ Create User"
  → Enter username, temporary password, role (STAFF or ADMIN) → Create
  → Share credentials with the new staff member
  → Staff member logs in, goes to Admin Panel → My Account
  → Changes their password
  → Optionally enables MFA
```

### 6. Managing Doctors (Admin only)

```
Admin opens Admin Panel → Doctors tab
  → Click "+ Add Doctor" → enter name, specialty → Save
  Doctors marked as Active appear in appointment and case-record doctor dropdowns.
  → To retire a doctor: Edit → uncheck Active → Save
    (existing records are preserved; the doctor no longer appears in new dropdowns)
```

---

## Data Entity Relationships

```
persons ──< pets ──< case_records
                │         └── doctors
                │         └── appointments (optional link)
                │
                └──< wellness_logs
                │
appointments ──> persons
appointments ──> pets
appointments ──> doctors (nullable)

system_users   (standalone — no FK to persons)
audit_logs     (stores userId + username as snapshot — no FK)
doctors        (standalone — referenced by appointments and case_records)
```

### Relationship details

| Relationship | Type | Cascade |
|---|---|---|
| Person → Pets | One-to-Many | DELETE person → deletes all their pets |
| Pet → CaseRecords | One-to-Many | DELETE pet → deletes all case records |
| Pet → WellnessLogs | One-to-Many | DELETE pet → deletes all wellness logs |
| Pet → Owner (Person) | Many-to-One | — |
| CaseRecord → Pet | Many-to-One | — |
| CaseRecord → Doctor | Many-to-One | — |
| CaseRecord → Appointment | Many-to-One | nullable (record can exist without appointment) |
| Appointment → Person | Many-to-One | — |
| Appointment → Pet | Many-to-One | — |
| Appointment → Doctor | Many-to-One | nullable (can book without assigning a doctor) |
| WellnessLog → Pet | Many-to-One | — |

---

## MFA Authentication Flow

### Login without MFA enabled

```
Client                          Server
  │                               │
  ├─ POST /api/auth/login ────────►│
  │  { username, password }       │
  │                               │ verify password (BCrypt)
  │◄─ 200 OK ─────────────────────┤
  │  { token, username, role,     │
  │    mfaEnabled: false }        │
  │                               │
  │  Store token in localStorage  │
  │  Attach as Bearer on all      │
  │  subsequent requests          │
```

### Login with MFA enabled

```
Client                          Server
  │                               │
  ├─ POST /api/auth/login ────────►│
  │  { username, password }       │
  │                               │ verify password ✓
  │                               │ mfaEnabled = true
  │◄─ 206 Partial Content ─────────┤
  │  { mfaRequired: true }        │
  │                               │
  │  Prompt user for TOTP code    │
  │                               │
  ├─ POST /api/auth/login ────────►│
  │  { username, password,        │
  │    totpCode: 123456 }         │
  │                               │ verify password ✓
  │                               │ verify TOTP (±1 window) ✓
  │◄─ 200 OK ─────────────────────┤
  │  { token, username, role,     │
  │    mfaEnabled: true }         │
```

### Setting up MFA for the first time

```
Client                          Server
  │                               │
  ├─ POST /api/auth/mfa/setup ───►│  (Bearer token required)
  │                               │ generate random Base32 secret
  │                               │ save secret to user (mfaEnabled still false)
  │◄─ 200 OK ─────────────────────┤
  │  { secret: "BASE32...",       │
  │    otpauthUri: "otpauth://..." }
  │                               │
  │  User scans QR / enters       │
  │  secret in authenticator app  │
  │                               │
  ├─ POST /api/auth/mfa/confirm ──►│
  │  { code: 123456 }             │
  │                               │ verify TOTP code ✓
  │                               │ set mfaEnabled = true
  │◄─ 200 OK ─────────────────────┤
  │  { message: "MFA enabled" }   │
```

### Token expiry and session

- Tokens are valid for **8 hours** (`jwt.expiration=28800000` ms)
- When a token expires, all API calls return `401 Unauthorized`
- The frontend automatically redirects to `/login` on any 401 response
- There is no refresh token — the user must log in again

---

## Database Schema

All primary keys are UUID v4, generated by PostgreSQL (`gen_random_uuid()`).  
`created_at` / `visited_at` / `logged_at` columns default to `NOW()` via a JPA `@PrePersist` hook.

### `persons`

| Column | Type | Constraints |
|--------|------|-------------|
| `id` | `uuid` | PK |
| `name` | `varchar` | NOT NULL |
| `email` | `varchar` | NOT NULL, UNIQUE |
| `phone` | `varchar` | — |
| `address` | `varchar` | — |
| `created_at` | `timestamp` | set on insert |

### `pets`

| Column | Type | Constraints |
|--------|------|-------------|
| `id` | `uuid` | PK |
| `name` | `varchar` | NOT NULL |
| `breed` | `varchar` | — |
| `gender` | `varchar` | enum: `MALE`, `FEMALE` |
| `birthday` | `date` | — |
| `photo_url` | `varchar` | — |
| `created_at` | `timestamp` | set on insert |
| `owner_id` | `uuid` | FK → `persons.id` NOT NULL |

### `doctors`

| Column | Type | Constraints |
|--------|------|-------------|
| `id` | `uuid` | PK |
| `name` | `varchar` | NOT NULL |
| `specialty` | `varchar` | — |
| `is_active` | `boolean` | NOT NULL, default `true` |
| `created_at` | `timestamp` | set on insert |

### `appointments`

| Column | Type | Constraints |
|--------|------|-------------|
| `id` | `uuid` | PK |
| `person_id` | `uuid` | FK → `persons.id` NOT NULL |
| `pet_id` | `uuid` | FK → `pets.id` NOT NULL |
| `doctor_id` | `uuid` | FK → `doctors.id` nullable |
| `scheduled_time` | `timestamp` | NOT NULL |
| `status` | `varchar` | NOT NULL, enum: `PENDING` `CONFIRMED` `COMPLETED` `CANCELLED` |
| `notes` | `text` | — |
| `created_at` | `timestamp` | set on insert |

### `case_records`

| Column | Type | Constraints |
|--------|------|-------------|
| `id` | `uuid` | PK |
| `pet_id` | `uuid` | FK → `pets.id` NOT NULL |
| `doctor_id` | `uuid` | FK → `doctors.id` NOT NULL |
| `appointment_id` | `uuid` | FK → `appointments.id` nullable |
| `chief_complaint` | `text` | — |
| `diagnosis` | `text` | — |
| `prescription` | `text` | — |
| `notes` | `text` | — |
| `visited_at` | `timestamp` | set on insert |

### `wellness_logs`

| Column | Type | Constraints |
|--------|------|-------------|
| `id` | `uuid` | PK |
| `pet_id` | `uuid` | FK → `pets.id` NOT NULL |
| `type` | `varchar` | NOT NULL, enum: `WEIGHT` `VACCINE` `DEWORMING` `OTHER` |
| `value` | `varchar` | — |
| `notes` | `text` | — |
| `logged_at` | `timestamp` | set on insert |

### `system_users`

| Column | Type | Constraints |
|--------|------|-------------|
| `id` | `uuid` | PK |
| `username` | `varchar` | NOT NULL, UNIQUE |
| `password_hash` | `varchar` | NOT NULL (BCrypt) |
| `role` | `varchar` | NOT NULL, enum: `STAFF` `ADMIN` |
| `mfa_enabled` | `boolean` | NOT NULL, default `false` |
| `mfa_secret` | `varchar` | nullable (Base32, only set when MFA configured) |
| `is_active` | `boolean` | NOT NULL, default `true` |
| `created_at` | `timestamp` | set on insert |

### `audit_logs`

| Column | Type | Constraints |
|--------|------|-------------|
| `id` | `uuid` | PK |
| `user_id` | `uuid` | nullable snapshot of the actor's ID |
| `username` | `varchar` | snapshot of the actor's username |
| `action` | `varchar` | NOT NULL (e.g. `CREATE_USER`, `DELETE_CUSTOMER`) |
| `detail` | `text` | human-readable description |
| `created_at` | `timestamp` | set on insert |

> `audit_logs` deliberately stores `username` as a plain string rather than a foreign key so that log entries survive user deletion.

---

## API Reference

All requests (except login) require:
```
Authorization: Bearer <token>
```

### Auth `/api/auth`

| Method | Path | Description |
|--------|------|-------------|
| POST | `/api/auth/login` | Login. Returns `{ token, username, role, mfaEnabled }`. Returns HTTP 206 if MFA is required — resend with `totpCode` |
| GET | `/api/auth/me` | Get current user info |
| POST | `/api/auth/change-password` | Change own password |
| POST | `/api/auth/mfa/setup` | Generate MFA secret (returns `secret` + `otpauthUri`) |
| POST | `/api/auth/mfa/confirm` | Activate MFA with a verified code |
| POST | `/api/auth/mfa/disable` | Disable MFA with current code |

**Login request body:**
```json
{ "username": "usermanage", "password": "usermanage", "totpCode": 123456 }
```
`totpCode` is only required when MFA is enabled and HTTP 206 was returned on the first attempt.

### Customers `/api/customers`

| Method | Path | Auth | Description |
|--------|------|------|-------------|
| GET | `/api/customers` | Any | List all. `?search=` for name/email/phone search |
| GET | `/api/customers/{id}` | Any | Get customer with their pets |
| POST | `/api/customers` | Any | Create a customer |
| PUT | `/api/customers/{id}` | Any | Update a customer |
| DELETE | `/api/admin/customers/{id}` | **ADMIN** | Delete a customer (cascades to pets and records) |

**Customer body:**
```json
{ "name": "Jane Smith", "email": "jane@example.com", "phone": "555-1234", "address": "123 Main St" }
```

### Pets `/api/pets`

| Method | Path | Auth | Description |
|--------|------|------|-------------|
| GET | `/api/pets` | Any | List all pets |
| GET | `/api/pets/{id}` | Any | Get pet by ID |
| GET | `/api/pets/owner/{ownerId}` | Any | List pets by owner |
| GET | `/api/pets/{id}/case-records` | Any | Case records for a pet |
| GET | `/api/pets/{id}/wellness-logs` | Any | Wellness logs for a pet |
| POST | `/api/pets` | Any | Create a pet |
| PUT | `/api/pets/{id}` | Any | Update a pet |
| DELETE | `/api/pets/{id}` | **ADMIN** | Delete a pet |

**Pet body:**
```json
{ "name": "Buddy", "breed": "Golden Retriever", "gender": "MALE", "birthday": "2020-03-15", "ownerId": "<uuid>", "photoUrl": "" }
```

**Gender values:** `MALE` · `FEMALE`

### Case Records `/api/case-records`

| Method | Path | Auth | Description |
|--------|------|------|-------------|
| GET | `/api/case-records/pet/{petId}` | Any | List case records (newest first) |
| GET | `/api/case-records/{id}` | Any | Get a case record |
| POST | `/api/case-records` | Any | Create a case record |
| PUT | `/api/case-records/{id}` | Any | Update a case record |
| DELETE | `/api/case-records/{id}` | Any | Delete a case record |

**Case record body:**
```json
{ "petId": "<uuid>", "doctorId": "<uuid>", "appointmentId": "<uuid>", "chiefComplaint": "...", "diagnosis": "...", "prescription": "...", "notes": "..." }
```

### Wellness Logs `/api/wellness-logs`

| Method | Path | Auth | Description |
|--------|------|------|-------------|
| GET | `/api/wellness-logs/pet/{petId}` | Any | List wellness logs (newest first) |
| POST | `/api/wellness-logs` | Any | Add a wellness log |
| DELETE | `/api/wellness-logs/{id}` | Any | Delete a wellness log |

**Wellness log body:**
```json
{ "petId": "<uuid>", "type": "WEIGHT", "value": "5.2 kg", "notes": "Annual checkup" }
```

**Type values:** `WEIGHT` · `VACCINE` · `DEWORMING` · `OTHER`

### Appointments `/api/appointments`

| Method | Path | Auth | Description |
|--------|------|------|-------------|
| GET | `/api/appointments` | Any | List all. `?status=` to filter |
| GET | `/api/appointments/{id}` | Any | Get an appointment |
| POST | `/api/appointments` | Any | Create an appointment |
| PUT | `/api/appointments/{id}` | Any | Update an appointment |
| DELETE | `/api/appointments/{id}` | Any | Delete an appointment |

**Appointment body:**
```json
{ "personId": "<uuid>", "petId": "<uuid>", "doctorId": "<uuid>", "scheduledTime": "2025-06-01T10:30:00", "status": "PENDING", "notes": "..." }
```

**Status values:** `PENDING` · `CONFIRMED` · `COMPLETED` · `CANCELLED`

### Doctors `/api/doctors`

| Method | Path | Auth | Description |
|--------|------|------|-------------|
| GET | `/api/doctors` | Any | List all doctors. `?activeOnly=true` for active only |
| GET | `/api/doctors/{id}` | Any | Get a doctor |
| POST | `/api/doctors` | **ADMIN** | Add a doctor |
| PUT | `/api/doctors/{id}` | **ADMIN** | Update a doctor |
| DELETE | `/api/doctors/{id}` | **ADMIN** | Delete a doctor |

**Doctor body:**
```json
{ "name": "Sarah Lee", "specialty": "Surgery", "active": true }
```

### Admin `/api/admin`

All routes require `ADMIN` role.

| Method | Path | Description |
|--------|------|-------------|
| GET | `/api/admin/users` | List all system users |
| POST | `/api/admin/users` | Create a user (`{ username, password, role }`) |
| PUT | `/api/admin/users/{id}/role` | Change role (`{ role: "ADMIN" \| "STAFF" }`) |
| PUT | `/api/admin/users/{id}/active` | Enable/disable user (`{ active: true/false }`) |
| PUT | `/api/admin/users/{id}/reset-password` | Reset password (`{ password: "..." }`) |
| DELETE | `/api/admin/users/{id}` | Delete a user |
| DELETE | `/api/admin/customers/{id}` | Delete a customer (with all pets and records) |
| GET | `/api/admin/audit-logs` | Paginated audit log (`?page=0&size=50`) |

---

## Pages

| Page | Route | Role | Features |
|------|-------|------|----------|
| Login | `/login` | Public | Username/password + TOTP second step |
| Dashboard | `/` | Any | Stats, today's appointments |
| Customers | `/customers` | Any | Search, add/edit; delete requires ADMIN |
| Customer Detail | `/customers/:id` | Any | Contact info, pet list, case records, wellness logs |
| Appointments | `/appointments` | Any | Filter by status, inline status change, doctor assignment |
| Admin Panel | `/admin` | **ADMIN** | User management, doctor management, audit log, MFA settings |
