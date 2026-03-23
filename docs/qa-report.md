# QA Report - BPO-CONNECT

Date: 2026-03-23
Scope: DevOps and runtime configuration quality check

## Findings

1. Medium - Docker profile was declared but not defined
- Impact: Container runs used SPRING_PROFILES_ACTIVE=docker without profile-specific overrides, which makes behavior unclear and environment assumptions hard to validate.
- Fix: Added src/main/resources/application-docker.properties.

2. Medium - H2 console enabled by default
- Impact: Browser DB console may be unintentionally exposed outside local/dev use.
- Current status: Mitigated for Docker profile by disabling H2 console in application-docker.properties.

3. Low - Local QA execution tooling unavailable in current machine
- Impact: java, mvn, and docker commands cannot be executed locally in this environment; runtime smoke tests could not be run here.
- Recommendation: Validate through GitHub Actions and run local verification on a machine with JDK 17, Maven, and Docker installed.

## Verifications Performed

- Static review of CI workflow: .github/workflows/ci.yml
- Static review of CD workflow: .github/workflows/cd.yml
- Static review of Docker assets: Dockerfile, docker-compose.yml, .dockerignore
- Static review of application config: src/main/resources/application.properties

## Final QA Status

- CI/CD workflow files are syntactically consistent.
- Docker and Compose assets are present.
- Profile alignment issue fixed for Docker environment.
- Remaining validation is runtime execution on a host with required tools.
