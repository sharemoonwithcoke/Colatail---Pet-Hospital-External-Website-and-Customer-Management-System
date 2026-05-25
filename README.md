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

## Two-Factor Authentication (TOTP)

The system supports Google Authenticator-compatible TOTP MFA.

1. Log in as any user
2. Go to **Admin Panel → My Account** (or ask an admin to enable it for you)
3. Click **Enable MFA** — a Base32 secret is shown
4. Enter the secret into Google Authenticator / Authy / any TOTP app
5. Enter the 6-digit code to confirm
6. From the next login, a second step will prompt for your current code

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
