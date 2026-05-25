# Colatail — Pet Hospital Management System

An internal management system for pet hospitals to handle customers, pets, appointments, and daily tasks.

## Tech Stack

| Layer | Technology |
|-------|------------|
| Backend | Java 17 · Spring Boot 3.1 · Spring Data JPA |
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
│       ├── Customer/         # Customer, pet, case record entities & repositories
│       ├── Controller/       # REST controllers
│       ├── toDo/             # Todo item entity & repository
│       └── config/           # CORS configuration
└── frontend/
    ├── Dockerfile
    ├── nginx.conf
    └── src/
        ├── api.js            # Axios API wrappers
        ├── components/       # Shared components (Layout, Modal)
        └── pages/            # Page components
```

## Running with Docker (recommended)

### Prerequisites

- [Docker](https://docs.docker.com/get-docker/)
- [Docker Compose](https://docs.docker.com/compose/install/) (included with Docker Desktop)

### Start everything

```bash
docker compose up --build
```

That's it. Docker will:
1. Pull the PostgreSQL 16 image and start the database
2. Build the Spring Boot backend and start it (waits for the database to be healthy)
3. Build the React app with Vite and serve it through Nginx

Once all three containers are running, open **http://localhost** in your browser.

| Service | URL |
|---------|-----|
| Frontend | http://localhost |
| Backend API | http://localhost:8080 |
| PostgreSQL | localhost:5432 |

> Tables are created automatically on first start — no manual migration needed.

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

Database connection settings are controlled by environment variables with local fallbacks:

| Variable | Default | Description |
|----------|---------|-------------|
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://localhost:5432/colatail` | JDBC connection URL |
| `SPRING_DATASOURCE_USERNAME` | `colatail_user` | Database user |
| `SPRING_DATASOURCE_PASSWORD` | `colatail123` | Database password |

To use a different database, set these variables in `docker-compose.yml` or your shell before running locally.

---

## API Reference

### Customers `/api/customers`

| Method | Path | Description |
|--------|------|-------------|
| GET | `/api/customers` | List all customers. Supports `?search=` for full-text search |
| GET | `/api/customers/{id}` | Get a customer by ID (includes their pets) |
| POST | `/api/customers` | Create a customer |
| PUT | `/api/customers/{id}` | Update a customer |
| DELETE | `/api/customers/{id}` | Delete a customer (cascades to pets and case records) |

### Pets `/api/pets`

| Method | Path | Description |
|--------|------|-------------|
| GET | `/api/pets` | List all pets |
| GET | `/api/pets/owner/{ownerId}` | List pets belonging to a customer |
| POST | `/api/pets` | Create a pet (body must include `ownerId`) |
| PUT | `/api/pets/{id}` | Update a pet |
| DELETE | `/api/pets/{id}` | Delete a pet |

### Case Records `/api/case-records`

| Method | Path | Description |
|--------|------|-------------|
| GET | `/api/case-records/pet/{petId}` | List case records for a pet (newest first) |
| POST | `/api/case-records` | Add a case record (body must include `petId`) |
| DELETE | `/api/case-records/{id}` | Delete a case record |

### Appointments `/api/appointments`

| Method | Path | Description |
|--------|------|-------------|
| GET | `/api/appointments` | List all appointments. Supports `?status=` `?date=` `?doctor=` |
| GET | `/api/appointments/{id}` | Get an appointment by ID |
| POST | `/api/appointments` | Create an appointment |
| PUT | `/api/appointments/{id}` | Update an appointment (including status changes) |
| DELETE | `/api/appointments/{id}` | Delete an appointment |

**Status values:** `PENDING` · `COMPLETED` · `CANCELLED`  
**Doctor options:** `Clair` · `Michell` · `Jay` · `Alex` · `Cam`

### To-Do Items `/api/todos`

| Method | Path | Description |
|--------|------|-------------|
| GET | `/api/todos` | List all tasks (newest first) |
| POST | `/api/todos` | Create a task (body: `{ "title": "..." }`) |
| PUT | `/api/todos/{id}/toggle` | Toggle completion status |
| DELETE | `/api/todos/{id}` | Delete a task |

---

## Pages

| Page | Route | Features |
|------|-------|----------|
| Dashboard | `/` | Stats overview, today's appointments |
| Customers | `/customers` | Searchable list, add / edit / delete |
| Customer Detail | `/customers/:id` | Contact info, pet management, case records |
| Appointments | `/appointments` | Filter by status / doctor / date, inline status change |
| To-Do List | `/todos` | Add, complete, and delete tasks |
