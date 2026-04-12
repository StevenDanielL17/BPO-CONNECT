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
- PostgreSQL / Supabase-compatible Database (default runtime)
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

### Essential (To Run the App)
- **Java 17 JDK** - Required for Spring Boot 3.2.3
  - [Download Oracle JDK 17](https://www.oracle.com/java/technologies/downloads/#java17)
  - Or via Chocolatey: `choco install openjdk17`
- **Maven 3.9+** - Build tool for Java projects
  - [Download Maven](https://maven.apache.org/download.cgi)
  - Or via Chocolatey: `choco install maven`
  - Verify with `mvn -v` in your terminal

### Optional (For Containerization)
- **Docker Desktop** - To run the app in containers
  - [Download Docker Desktop](https://www.docker.com/products/docker-desktop)
  - Includes Docker and Docker Compose

### Optional (For Testing/QA)
- **k6** - Load testing tool (for stress tests in `qa/stress`)
  - [Download k6](https://k6.io/docs/getting-started/installation/)
  - Or via Chocolatey: `choco install k6`
- **PowerShell 5.1+** - Required for smoke tests and Windows scripts (pre-installed on Windows)

## Quick Setup (Windows)

A setup script is provided to automate the installation of dependencies via Chocolatey:

```powershell
./setup.ps1
```

## Run Locally

### Option 1: Maven (Standard)

```bash
# Build the project
mvn clean package -DskipTests

# Run the application
mvn spring-boot:run
```

### Option 2: PowerShell Script (Windows)

```powershell
./run.ps1
```

### Option 3: Bash Script (Linux/macOS)

```bash
./run.sh
```

### Option 4: Docker Compose

```bash
docker-compose up --build
```

## Test

### Unit and Integration Tests

```bash
mvn test
```

### Smoke Tests (requires app running)

```powershell
./qa/smoke/smoke-tests.ps1
```

### Stress Tests (requires app running and k6 installed)

```bash
k6 run qa/stress/k6-load-test.js
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
  - New Frontend: `http://localhost:8080/index.html`

## Notes

- The repository ignores generated output in `target/` via `.gitignore`.
- The app loads database settings from the repo `.env` file automatically and defaults to Postgres/Supabase via `DB_POOLER_URL` if present, otherwise `DB_URL`, plus `DB_USERNAME` and `DB_PASSWORD`.
- H2 remains only as a runtime dependency for optional fallback/testing, not the default app database.
- If Supabase direct connection is IPv4-blocked in your network, set `DB_POOLER_URL` to the Supabase session pooler connection string.
- Containerization files are included: `Dockerfile`, `.dockerignore`, `docker-compose.yml`.
- Docker profile overrides are in `src/main/resources/application-docker.properties`.
- QA audit summary is available in `docs/qa-report.md`.
