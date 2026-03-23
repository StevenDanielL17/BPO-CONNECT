# BPO-CONNECT

BPO-CONNECT is a Spring Boot based customer support platform for BPO workflows. It models users, customers, tickets, SLA escalation, quality checks, and reporting in a single backend project.

## Features

- Ticket lifecycle management for voice, chat, and email scenarios
- User and role-based entities for support teams
- SLA and escalation strategy handling
- Knowledge base and feedback modules
- Quality evaluation and reporting components
- Pattern-oriented design using Factory, Strategy, Observer, Singleton, and Template Method

## Tech Stack

- Java 17
- Spring Boot 3.2.3
- Spring Web
- Spring Data JPA
- H2 Database (local default)
- PostgreSQL (Supabase-compatible, production profile)
- Maven

## Project Structure

- `src/main/java/com/bpoconnect/controller`: REST controllers
- `src/main/java/com/bpoconnect/service`: business services
- `src/main/java/com/bpoconnect/repository`: persistence interfaces
- `src/main/java/com/bpoconnect/model`: domain entities
- `src/main/java/com/bpoconnect/patterns`: design pattern implementations
- `src/main/resources/static`: frontend static assets
- `docs/reference.md`: software engineering reference notes

## Prerequisites

- JDK 17+
- Maven 3.8+

## Run Locally

### Option 1: Maven

```bash
mvn clean install -DskipTests
mvn spring-boot:run
```

### Option 2: Script

```bash
./run.sh
```

## Build

```bash
mvn clean package
```

## Test

```bash
mvn test
```

## DevOps Setup

### Continuous Integration

GitHub Actions workflow is available at `.github/workflows/ci.yml`.

It runs on push and pull requests to `main` and performs:

- Maven build and tests (`mvn clean verify`)
- Docker image build validation

### Continuous Delivery

GitHub Actions workflow is available at `.github/workflows/cd.yml`.

On push to `main`, it publishes Docker images to GitHub Container Registry:

- `ghcr.io/<github-username>/bpo-connect:latest`
- `ghcr.io/<github-username>/bpo-connect:sha-<commit>`

### Docker Build

```bash
docker build -t bpo-connect:latest .
docker run --rm -p 8080:8080 bpo-connect:latest
```

### Docker Compose

```bash
docker compose up --build -d
docker compose logs -f
docker compose down
```

## API and UI

Once the app starts, use:

- Base URL: `http://localhost:8080`
- Static UI: `http://localhost:8080/index.html`

## Application Flow (End-to-End)

1. **UI actions** (`src/main/resources/static/js/app.js`) call backend endpoints under `/api`.
2. **Controllers** (`BpoApiController`, `UserController`) receive requests and delegate business logic.
3. **Services** (`TicketService`, `UserService`, `CallService`, `KnowledgeBaseService`, etc.) apply rules (factory, strategy, observer patterns).
4. **Repositories** (Spring Data JPA interfaces) persist/load entities.
5. **Database** stores entities (`users`, `tickets`, `customers`, `slas`, etc.).

Important: The app does **not** use browser localStorage/sessionStorage for business data. The previous persistence behavior was in-memory H2, which reset on restart.

## Database Migration Choice: Supabase vs Firebase

- **Chosen:** Supabase (PostgreSQL)
- **Why:** The app already uses relational JPA entities and repositories. Supabase is PostgreSQL, so migration is minimal.
- **Not chosen:** Firebase/Firestore would require substantial repository/service redesign (document model + SDK-based data layer).

## Supabase Configuration

Create a Supabase project, then collect:

- DB host, port, database name
- DB user and password

Set environment variables:

```bash
SPRING_PROFILES_ACTIVE=postgres
DB_URL=jdbc:postgresql://<HOST>:5432/postgres?sslmode=require
DB_USERNAME=<USER>
DB_PASSWORD=<PASSWORD>
JPA_DDL_AUTO=update
```

Notes:

- `application.properties` keeps local H2 defaults for easy local runs.
- `application-postgres.properties` activates PostgreSQL/Supabase settings.

### Quick env setup

1. Copy `.env.example` to `.env`.
2. Set your real Supabase DB password in `.env` (`DB_PASSWORD=...`).

#### Windows PowerShell

```powershell
.\scripts\set-env.ps1
mvn spring-boot:run
```

#### Linux/macOS

```bash
chmod +x ./scripts/set-env.sh
source ./scripts/set-env.sh
mvn spring-boot:run
```

## Deployment Strategy

Because this is a Spring Boot server app + static frontend:

- **Backend:** deploy to Render/Railway/Fly.io (or any Docker-capable host)
- **Frontend:** deploy static UI to Vercel

### Backend on Render

- `render.yaml` is included in the root.
- Configure env vars in Render dashboard:
	- `SPRING_PROFILES_ACTIVE=postgres`
	- `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`
	- `CORS_ALLOWED_ORIGINS=https://<your-vercel-domain>`

### Frontend on Vercel

- `vercel.json` is included for static routing.
- Update the `/api` rewrite destination in `vercel.json`:

```json
{ "source": "/api/:path*", "destination": "https://<your-backend-domain>/api/:path*" }
```

Alternative to rewrite: set `window.BPO_API_BASE` in `src/main/resources/static/js/config.js` to your backend API base.

## Notes

- The repository ignores generated output in `target/` via `.gitignore`.
- H2 remains for local development defaults.
- PostgreSQL driver is included for Supabase/production use.
- Containerization files are included: `Dockerfile`, `.dockerignore`, `docker-compose.yml`.
- Docker profile overrides are in `src/main/resources/application-docker.properties`.
- QA audit summary is available in `docs/qa-report.md`.
