# FieldOps Environment Setup Guide

## 1. Purpose

This document explains how to prepare a local development environment for FieldOps.

The project consists of:

- Angular frontend
- Java Spring Boot backend
- PostgreSQL database
- Go analytics service
- Docker and Docker Compose
- GitHub Actions for CI

The development environment should allow each component to be run independently during development and together through Docker Compose.

---

## 2. Development Environment

The following tools are required:

| Tool | Purpose |
|---|---|
| Git | Source control |
| Java | Spring Boot backend |
| Maven | Java build and dependency management |
| Node.js | Angular development |
| npm | Angular package management |
| Angular CLI | Angular development and build |
| Go | Analytics service |
| PostgreSQL | Relational database |
| Docker | Containerization |
| Docker Compose | Multi-service local environment |
| VS Code | Development environment |
| Postman | API testing |

---

## 3. Project Directory

The project is organized as:

```text
FieldOps/
├── frontend/
├── backend/
├── analytics-service/
├── database/
├── api/
├── docs/
├── docker/
├── .github/
├── docker-compose.yml
├── .env.example
├── .gitignore
└── README.md
```

Each major application component has its own directory.

---

## 4. Verify Installed Tools

Before development, verify that the required tools are available.

### Git

```bash
git --version
```

Expected output should show an installed Git version.

### Java

```bash
java -version
```

### Maven

```bash
mvn -version
```

### Node.js

```bash
node --version
```

### npm

```bash
npm --version
```

### Angular CLI

```bash
ng version
```

### Go

```bash
go version
```

### Docker

```bash
docker --version
```

### Docker Compose

```bash
docker compose version
```

PostgreSQL availability can be verified through the configured PostgreSQL client or database administration tool.

---

## 5. Clone the Repository

Clone the FieldOps repository:

```bash
git clone <repository-url>
```

Move into the project:

```bash
cd FieldOps
```

The actual repository URL will be added once the GitHub repository is finalized.

---

## 6. Environment Variables

FieldOps uses environment variables for configuration that should not be hard-coded into application source code.

The repository contains:

```text
.env.example
```

This file provides the expected configuration structure.

A local environment file can be created from it:

```powershell
Copy-Item .env.example .env
```

Sensitive values should not be committed to Git.

Examples of configuration values include:

- PostgreSQL database name
- PostgreSQL username
- PostgreSQL password
- Database port
- Backend port
- Analytics service port
- Frontend port

---

## 7. PostgreSQL Configuration

FieldOps uses PostgreSQL as its relational database.

The main database configuration includes:

```text
Database: fieldops
Port: 5432
```

The actual username and password are supplied through environment-specific configuration.

The database contains the core FieldOps data:

```text
users
engineers
equipment
work_orders
maintenance_records
```

Database changes are applied through versioned migration files.

Migration files are stored under:

```text
database/migrations/
```

The exact migration tool will be selected during backend/database implementation.

---

## 8. Database Initialization

The development database should be initialized before running the backend.

The initialization process will eventually consist of:

```text
Create PostgreSQL database
        ↓
Run database migrations
        ↓
Create required tables
        ↓
Apply constraints and indexes
        ↓
Load development seed data
```

Seed data will be maintained separately from migration definitions.

Seed data is intended for development and testing and must not contain production secrets.

---

## 9. Backend Setup

The backend is implemented using Java and Spring Boot.

The backend source is located at:

```text
backend/
```

The Maven project is defined by:

```text
backend/pom.xml
```

Move into the backend directory:

```bash
cd backend
```

Build the backend:

```bash
mvn clean package
```

Run backend tests:

```bash
mvn test
```

Run the Spring Boot application:

```bash
mvn spring-boot:run
```

The backend will expose its REST API on the configured backend port.

The default development configuration is expected to use:

```text
http://localhost:8080
```

The final port remains configurable through application configuration.

---

## 10. Backend Configuration

Backend configuration is stored under:

```text
backend/src/main/resources/
```

The main application configuration file is:

```text
application.yml
```

Configuration should include:

- Server configuration
- PostgreSQL connection
- JPA/Hibernate configuration
- Logging configuration
- Analytics-service connection

Environment-specific values should not be hard-coded when they contain secrets or deployment-specific configuration.

---

## 11. Frontend Setup

The frontend is implemented using Angular.

The frontend source is located at:

```text
frontend/
```

Move into the frontend directory:

```bash
cd frontend
```

Install dependencies:

```bash
npm install
```

Start the Angular development server:

```bash
ng serve
```

The development application will normally be available at:

```text
http://localhost:4200
```

The frontend communicates with the Java backend through REST APIs.

---

## 12. Frontend Build

Create a production build with:

```bash
ng build
```

The generated build output will be placed in the Angular build output directory defined by the project configuration.

The production build must complete successfully before the frontend is considered ready for deployment.

---

## 13. Analytics Service Setup

The analytics service is implemented in Go.

Its source is located at:

```text
analytics-service/
```

Move into the directory:

```bash
cd analytics-service
```

Download Go dependencies:

```bash
go mod download
```

Run Go tests:

```bash
go test ./...
```

Build the service:

```bash
go build ./...
```

Run the service:

```bash
go run ./cmd/server
```

The analytics service exposes the risk-analysis API used by the Java backend.

---

## 14. Analytics Service Configuration

The Go service configuration should define environment-specific values such as:

- HTTP port
- Java/backend integration settings where required
- Database configuration if the final implementation requires repository access

The service should keep configuration separate from business logic.

The risk calculation itself belongs in:

```text
analytics-service/pkg/risk/
```

---

## 15. Running the Complete Application Locally

During development, the components can be run independently.

The local architecture is:

```text
Angular
   |
   | REST
   ↓
Spring Boot
   |
   | JPA/Hibernate
   ↓
PostgreSQL

Spring Boot
   |
   | HTTP
   ↓
Go Analytics Service
```

A typical local development setup therefore requires:

- PostgreSQL
- Spring Boot
- Go Analytics Service
- Angular

Each service can be started independently when debugging a specific component.

---

## 16. Docker Compose

FieldOps will provide a Docker Compose environment for running the major application components together.

The Compose environment is intended to include:

- Angular
- Spring Boot
- PostgreSQL
- Go Analytics Service

A reverse proxy may also be included as part of the deployment architecture where required.

Start the complete environment with:

```bash
docker compose up --build
```

Run the environment in detached mode:

```bash
docker compose up --build -d
```

View running containers:

```bash
docker compose ps
```

View logs:

```bash
docker compose logs
```

View logs for a specific service:

```bash
docker compose logs backend
```

Stop the environment:

```bash
docker compose down
```

---

## 17. Docker Development Flow

The expected Docker workflow is:

```text
Source Code
    ↓
Docker Build
    ↓
Application Images
    ↓
Docker Compose
    ↓
Frontend + Backend + Go + PostgreSQL
```

Docker is primarily used to make the complete environment reproducible and to support deployment.

Individual components should still remain runnable independently during development.

---

## 18. API Testing with Postman

Postman will be used to manually verify REST APIs.

The repository contains:

```text
api/postman/
```

Postman collections should contain requests for important endpoints.

Examples include:

```text
Equipment
├── List equipment
├── Get equipment
├── Create equipment
├── Update equipment
└── Deactivate equipment

Work Orders
├── List work orders
├── Create work order
├── Assign engineer
├── Update status
└── Complete work order

Maintenance
└── Retrieve maintenance history

Analytics
└── Retrieve equipment risk
```

Expected request and response behavior should be documented alongside the API implementation.

---

## 19. Running Tests Locally

Before committing changes, run the relevant tests.

### Java

```bash
cd backend
mvn test
```

### Angular

```bash
cd frontend
npm test
```

The exact Angular test command may be adjusted according to the generated project configuration.

### Go

```bash
cd analytics-service
go test ./...
```

---

## 20. Recommended Development Workflow

The recommended workflow for a feature is:

```text
Create/Update Requirement
          ↓
Create Implementation
          ↓
Write Tests
          ↓
Run Focused Tests
          ↓
Run Broader Test Suite
          ↓
Run Application
          ↓
Verify API/UI
          ↓
Commit Changes
```

For a database change:

```text
Requirement
    ↓
Migration
    ↓
Entity/Repository Changes
    ↓
Service Changes
    ↓
Tests
    ↓
API Verification
```

---

## 21. Git Workflow

Development should use meaningful Git commits.

Example:

```bash
git status
```

Stage changes:

```bash
git add .
```

Create a commit:

```bash
git commit -m "Implement equipment management"
```

Push changes:

```bash
git push
```

Major features should preferably be developed through branches and pull requests.

Example:

```bash
git checkout -b feature/equipment-management
```

After implementation and testing:

```bash
git push -u origin feature/equipment-management
```

---

## 22. CI Environment

GitHub Actions will validate the project automatically.

The CI pipeline will eventually perform:

```text
Checkout
   ↓
Java build and tests
   ↓
Angular dependency installation, tests and build
   ↓
Go tests and build
   ↓
Docker validation where configured
```

The CI configuration is stored under:

```text
.github/workflows/
```

CI should provide a repeatable validation environment separate from the developer's local machine.

---

## 23. Troubleshooting

When something fails, use a structured debugging process.

### Step 1: Reproduce

Confirm the problem can be reproduced consistently.

### Step 2: Inspect logs

Check:

- Terminal output
- Spring Boot logs
- Angular console
- Browser network requests
- Go service logs
- PostgreSQL errors
- Docker logs
- CI logs

### Step 3: Identify the failing layer

Determine whether the problem belongs to:

- Angular
- Backend Controller
- Backend Service
- Repository
- Database
- Java-Go Integration
- Go Service
- Docker
- CI

### Step 4: Test the smallest affected component

Run the relevant unit or integration test.

### Step 5: Fix

Make the smallest appropriate change.

### Step 6: Regression test

Add or update a test when the problem represents a behavior that should remain protected.

### Step 7: Run broader tests

Confirm that the fix did not break existing functionality.

---

## 24. Local Development Checklist

Before starting development:

- [ ] Git installed
- [ ] Java installed
- [ ] Maven installed
- [ ] Node.js installed
- [ ] npm installed
- [ ] Angular CLI installed
- [ ] Go installed
- [ ] PostgreSQL installed/configured
- [ ] Docker installed
- [ ] Docker Compose available
- [ ] Repository cloned
- [ ] Environment configuration prepared

Before committing a feature:

- [ ] Implementation completed
- [ ] Unit tests pass
- [ ] Integration tests pass where applicable
- [ ] API verified where applicable
- [ ] Frontend verified where applicable
- [ ] No sensitive configuration committed
- [ ] Git status reviewed
- [ ] CI-relevant changes checked

---

## 25. Environment Separation

Development and production environments should not share secrets or environment-specific configuration.

The following should remain outside source control:

- Database passwords
- Production credentials
- API secrets
- Private keys
- Deployment credentials

The `.env.example` file should contain configuration names and safe example values only.

---

## 26. Security Considerations

FieldOps must not store passwords in plaintext.

Secrets should be supplied through environment-specific configuration.

Production secrets must not be committed to Git.

Application configuration should allow development and deployment environments to use different values without modifying application source code.

---

## 27. Environment Setup Summary

The complete development environment is:

```text
                    Developer
                        |
                    VS Code
                        |
        +---------------+---------------+
        |               |               |
     Angular          Java             Go
        |           Spring Boot      Analytics
        |               |               |
        +---------------+---------------+
                        |
                    PostgreSQL
```

Docker Compose provides a reproducible environment for running the complete application.

GitHub Actions provides automated build and test validation.

The environment is designed to support local development, automated testing, containerized execution, debugging, and eventual deployment.