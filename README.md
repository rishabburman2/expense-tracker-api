# Expense Tracker API

A production-grade REST API for personal expense tracking, built with Java 21 and Spring Boot 4. Features JWT authentication, expense CRUD, and monthly/category spending reports.

**Live Demo:** https://expense-tracker-frontend-b7ru.onrender.com  
**API Base URL:** https://expense-tracker-api-qcdw.onrender.com

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 4.0.6 |
| Security | Spring Security 7 + JWT (jjwt 0.12.6) |
| Database | PostgreSQL 16 |
| ORM | Spring Data JPA + Hibernate 7 |
| Build | Maven |
| Containerisation | Docker (multi-stage build) |
| Deployment | Render (Web Service + Managed PostgreSQL) |

---

## Architecture

```
React Frontend
      │
      │ HTTPS + Authorization: Bearer <JWT>
      ▼
┌─────────────────────────────────────────┐
│           Spring Boot App               │
│                                         │
│  SecurityConfig → JwtAuthFilter         │
│       ↓                                 │
│  Controllers (Auth, Expense, Report)    │
│       ↓                                 │
│  Services (business logic)              │
│       ↓                                 │
│  Repositories (Spring Data JPA)         │
│       ↓                                 │
│  GlobalExceptionHandler                 │
└─────────────────────────────────────────┘
      │
      ▼
PostgreSQL (users + expenses tables)
```

---

## API Endpoints

### Auth (public)
| Method | URL | Description |
|--------|-----|-------------|
| POST | `/api/auth/register` | Register new user, returns JWT |
| POST | `/api/auth/login` | Login, returns JWT |

### Expenses (JWT required)
| Method | URL | Description |
|--------|-----|-------------|
| GET | `/api/expenses` | Get all expenses for logged-in user |
| POST | `/api/expenses` | Create a new expense |
| GET | `/api/expenses/{id}` | Get single expense by ID |
| PUT | `/api/expenses/{id}` | Update an expense |
| DELETE | `/api/expenses/{id}` | Delete an expense |

### Reports (JWT required)
| Method | URL | Description |
|--------|-----|-------------|
| GET | `/api/reports/monthly` | Total spending grouped by month |
| GET | `/api/reports/category` | Total spending grouped by category |

---

## Request / Response Examples

### Register
```http
POST /api/auth/register
Content-Type: application/json

{
  "name": "Rishabh Burman",
  "email": "rishabh@gmail.com",
  "password": "password123"
}
```
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "email": "rishabh@gmail.com",
  "name": "Rishabh Burman"
}
```

### Create Expense
```http
POST /api/expenses
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9...
Content-Type: application/json

{
  "title": "Swiggy dinner",
  "amount": 450.00,
  "category": "FOOD",
  "expenseDate": "2026-05-12",
  "note": "Biryani"
}
```
```json
{
  "id": "44223346-f4d9-43a6-8bc4-cad8bdab0ef4",
  "title": "Swiggy dinner",
  "amount": 450.00,
  "category": "FOOD",
  "expenseDate": "2026-05-12",
  "note": "Biryani",
  "createdAt": "2026-05-12T19:53:08",
  "userId": "2ea96819-fc61-4105-9d47-8b0b8dd1581e"
}
```

### Monthly Report
```http
GET /api/reports/monthly
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9...
```
```json
[
  { "month": "2026-05", "total": 28300.00 }
]
```

---

## Error Responses

All errors return a consistent JSON shape:

```json
{
  "status": 400,
  "error": "Email already registered",
  "timestamp": "2026-05-12T18:46:26"
}
```

Validation errors include field-level details:

```json
{
  "status": 400,
  "error": "Validation failed",
  "fields": {
    "email": "Invalid email format",
    "password": "Password must be at least 6 characters"
  },
  "timestamp": "2026-05-12T18:47:05"
}
```

---

## Project Structure

```
src/main/java/com/expensetracker/expensetracker/
├── entity/
│   ├── Category.java          # enum: FOOD, TRAVEL, RENT, HEALTH, OTHER
│   ├── User.java              # implements UserDetails
│   └── Expense.java           # @ManyToOne User
├── repository/
│   ├── UserRepository.java
│   └── ExpenseRepository.java # custom JPQL + native SQL queries
├── dto/
│   ├── RegisterRequest.java
│   ├── LoginRequest.java
│   ├── AuthResponse.java
│   ├── ExpenseRequest.java
│   ├── ExpenseResponse.java   # never exposes password hash
│   ├── MonthlyReportResponse.java
│   └── CategoryReportResponse.java
├── service/
│   ├── AuthService.java
│   ├── ExpenseService.java    # ownership checks prevent IDOR
│   └── ReportService.java
├── controller/
│   ├── AuthController.java
│   ├── ExpenseController.java
│   └── ReportController.java
├── security/
│   ├── JwtUtil.java           # generate / validate / extract
│   ├── JwtAuthFilter.java     # OncePerRequestFilter
│   ├── SecurityConfig.java    # CORS + stateless sessions
│   └── UserDetailsServiceImpl.java
└── exception/
    └── GlobalExceptionHandler.java  # @RestControllerAdvice
```

---

## Running Locally

### Prerequisites
- Java 21
- Maven 3.9+
- Docker Desktop

### Steps

**1. Clone the repo**
```bash
git clone https://github.com/rishabburman2/expense-tracker-api.git
cd expense-tracker-api
```

**2. Start Postgres via Docker**
```bash
docker compose up -d
```

**3. Run the app**
```bash
mvn spring-boot:run
```

App starts on `http://localhost:8080`

**4. Test with the included API file**

Open `api.http` in VS Code with the REST Client extension and run the requests.

---

## Environment Variables (production)

| Variable | Description |
|---|---|
| `SPRING_PROFILES_ACTIVE` | Set to `prod` |
| `DATABASE_URL` | JDBC URL for Postgres |
| `DATABASE_USERNAME` | DB username |
| `DATABASE_PASSWORD` | DB password |
| `JWT_SECRET` | 256-bit hex secret for signing tokens |
| `PORT` | Port to bind to (Render injects this) |

---

## Key Design Decisions

**Stateless JWT auth** — no server-side sessions. Every request is self-contained. Scales horizontally without shared session storage.

**DTO pattern** — entities never leave the service layer. `ExpenseResponse` exposes `userId` not the full `User` object, preventing password hash leaks.

**Ownership checks** — `ExpenseService.getById()` verifies the expense belongs to the requesting user before returning it. Prevents IDOR (Insecure Direct Object Reference) attacks.

**BCrypt strength 12** — balances security and performance. Slow enough to deter brute force, fast enough for real traffic.

**Native SQL for reports** — `DATE_TRUNC` is Postgres-specific and not available in JPQL. Used `nativeQuery = true` for the monthly aggregation query.

---

## Future Enhancements

- [ ] AI-based expense categorization via Claude API
- [ ] Role-based access control (ADMIN / USER)
- [ ] Pagination on expense listing
- [ ] Token denylist in Redis for instant logout
- [ ] Account email verification flow
