# Colatail — Pet Hospital Management System

An internal management system for pet hospitals to handle customers, pets, appointments, and daily tasks.

## Tech Stack

| Layer | Technology |
|-------|------------|
| Backend | Java 17 · Spring Boot 3.1 · Spring Data JPA |
| Database | PostgreSQL 16 |
| Frontend | React 18 · Vite · Tailwind CSS |
| Communication | RESTful API · Axios |

## Project Structure

```
.
├── Backend/
│   ├── src/main/java/com/example/backend/
│   │   ├── Appointment/      # Appointment entity & repository
│   │   ├── Customer/         # Customer, pet, case record entities & repositories
│   │   ├── Controller/       # REST controllers
│   │   ├── toDo/             # Todo item entity & repository
│   │   └── config/           # CORS configuration
│   └── src/main/resources/
│       └── application.properties
└── frontend/
    └── src/
        ├── api.js            # Axios API wrappers
        ├── components/       # Shared components (Layout, Modal)
        └── pages/            # Page components
```

## Prerequisites

- Java 17+
- Maven 3.8+
- PostgreSQL 16+
- Node.js 18+

## Getting Started

### 1. Set up PostgreSQL

Make sure PostgreSQL is running, then create the database and user:

```bash
sudo -u postgres psql
```

```sql
CREATE DATABASE colatail;
CREATE USER colatail_user WITH PASSWORD 'colatail123';
GRANT ALL PRIVILEGES ON DATABASE colatail TO colatail_user;
\q
```

> Skip this step if you have already run it before.

### 2. Start the backend

```bash
cd Backend
mvn spring-boot:run
```

The backend starts on `http://localhost:8080`. On first run, Hibernate automatically creates all tables — no manual migration needed.

### 3. Start the frontend

Open a new terminal:

```bash
cd frontend
npm install      # first time only
npm run dev
```

Then open `http://localhost:5173` in your browser.

> The Vite dev server proxies all `/api/*` requests to `localhost:8080`, so no extra CORS setup is required.

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

## Database Configuration

Edit `Backend/src/main/resources/application.properties` to change the database connection:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/colatail
spring.datasource.username=colatail_user
spring.datasource.password=colatail123
```

---

## Pages

| Page | Route | Features |
|------|-------|----------|
| Dashboard | `/` | Stats overview, today's appointments |
| Customers | `/customers` | Searchable list, add / edit / delete |
| Customer Detail | `/customers/:id` | Contact info, pet management, case records |
| Appointments | `/appointments` | Filter by status / doctor / date, inline status change |
| To-Do List | `/todos` | Add, complete, and delete tasks |
