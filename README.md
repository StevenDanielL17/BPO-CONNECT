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
- H2 Database (runtime)
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

## Notes

- The repository ignores generated output in `target/` via `.gitignore`.
- H2 is configured as a runtime dependency for simple local execution.
- Containerization files are included: `Dockerfile`, `.dockerignore`, `docker-compose.yml`.
