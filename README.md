# FieldOps

FieldOps is a full-stack equipment and maintenance management platform designed for industrial field operations.

The system helps operations teams manage equipment, field engineers, maintenance work orders, maintenance history, equipment status, and operational risk.

## Objectives

FieldOps is being built as a production-style portfolio project to demonstrate:

- Java and Spring Boot backend development
- Angular frontend development
- PostgreSQL and SQL
- JPA and Hibernate
- REST API design
- Go service development
- Service-to-service integration
- Unit and integration testing
- Test-driven development practices
- Docker-based development and deployment
- Git and GitHub workflows
- GitHub Actions CI
- API documentation
- Agile software development practices
- Debugging and regression testing

## System Architecture

The application follows this flow:

```text
Angular Frontend
        |
        | REST API
        v
Java Spring Boot Backend
        |
        +---- JPA / Hibernate
        |
        v
PostgreSQL Database

Java Spring Boot Backend
        |
        | REST integration
        v
Go Analytics Service
        |
        v
Equipment Risk Calculation
```

The Java backend is the primary business application. It handles users, equipment, engineers, work orders, maintenance records, authentication, and business rules.

The Go service is an independently deployable analytics service responsible for equipment risk assessment.

## Main User Roles

### Administrator

Administrators manage:

- Equipment
- Engineers
- Users
- System configuration

### Field Engineer

Field engineers can:

- View assigned work orders
- Update work progress
- Record maintenance activities
- Complete assigned tasks

### Operations User

Operations users can:

- Monitor equipment
- Create maintenance work orders
- View operational dashboards
- Monitor equipment status
- Review maintenance and risk information

## Core Features

### Equipment Management

- Register equipment
- View equipment
- Search and filter equipment
- Update equipment
- Deactivate equipment
- View maintenance history
- Identify equipment approaching maintenance

### Engineer Management

- Manage engineer profiles
- Track specialization
- Track location
- Track availability
- View current workload
- Assign engineers to work orders

### Work Order Management

Work orders support the following lifecycle:

```text
OPEN -> ASSIGNED -> IN_PROGRESS -> COMPLETED
```

A work order may also be:

```text
CANCELLED
```

Work orders contain:

- Equipment
- Assigned engineer
- Priority
- Description
- Status
- Creation date
- Due date
- Completion date

### Dashboard

The dashboard provides:

- Equipment totals
- Equipment status summary
- Open work orders
- High-priority work orders
- Engineer availability
- Recent work orders
- Maintenance due information
- Equipment risk summary

### Equipment Risk Analytics

The Go analytics service evaluates equipment risk.

The service considers equipment-related conditions and returns information such as:

- Equipment ID
- Risk level
- Whether maintenance is due
- Risk reasons

Example:

```json
{
  "equipmentId": 101,
  "riskLevel": "HIGH",
  "maintenanceDue": true,
  "reasons": [
    "Maintenance overdue",
    "Equipment status requires attention"
  ]
}
```

## Technology Stack

### Frontend

- Angular
- TypeScript
- HTML
- CSS

### Backend

- Java
- Spring Boot
- Spring Data JPA
- Hibernate
- REST APIs
- Maven

### Analytics Service

- Go
- HTTP API
- Go standard testing tools

### Database

- PostgreSQL
- SQL
- Database migrations

### Testing

- JUnit 5
- Mockito
- Spring Boot Test
- MockMvc
- Angular TestBed
- Go testing
- API testing

### DevOps

- Git
- GitHub
- Docker
- Docker Compose
- GitHub Actions

## Repository Structure

```text
FieldOps/
│
├── frontend/
│
├── backend/
│
├── analytics-service/
│
├── database/
│   ├── migrations/
│   └── seed/
│
├── docs/
│   ├── requirements/
│   ├── architecture/
│   ├── testing/
│   └── deployment/
│
├── api/
│   └── postman/
│
├── docker/
│   └── nginx/
│
├── .github/
│   └── workflows/
│
├── .env.example
├── .gitignore
├── docker-compose.yml
└── README.md
```

## Backend Architecture

The Java backend follows a layered architecture:

```text
Controller
    |
    v
Service
    |
    v
Repository
    |
    v
PostgreSQL
```

### Controller

Handles:

- HTTP requests
- Request validation
- HTTP responses
- REST API endpoints

### Service

Handles:

- Business rules
- Transactions
- Application logic
- Integration with the Go analytics service

### Repository

Handles:

- Database access
- JPA queries
- Entity persistence

### Entity

Represents persistent database data using JPA and Hibernate.

### DTO

Defines request and response payloads separately from database entities.

### Mapper

Converts between entities and DTOs.

### Exception

Provides centralized error handling.

## Database

The primary database is PostgreSQL.

Core tables include:

- users
- engineers
- equipment
- work_orders
- maintenance_records

The database uses:

- Primary keys
- Foreign keys
- Constraints
- Indexes
- Transactions
- Versioned migrations

Database schema changes are maintained through migration files.

## Development Approach

FieldOps is developed incrementally.

The major development phases are:

1. Project foundation
2. Database and backend foundation
3. Equipment management
4. Engineers and work orders
5. Angular dashboard and workflows
6. Go analytics service
7. Java-Go integration
8. Testing and regression
9. Docker and CI refinement
10. Deployment and final documentation

Each phase is completed and verified before moving to the next phase.

## Testing Approach

Testing follows a TDD-oriented workflow:

1. Define expected behavior
2. Write a failing test
3. Implement the smallest solution
4. Refactor
5. Run the relevant test suite
6. Run regression tests

Testing will cover:

- Backend business logic
- REST APIs
- Database integration
- Angular components and services
- Go analytics logic
- Go HTTP handlers
- API behavior
- Regression scenarios

## CI

GitHub Actions will validate:

- Java build and tests
- Angular dependencies, tests, and build
- Go tests and build
- Docker image builds during final validation

## Docker

Docker Compose will eventually run the main application components:

- Angular frontend
- Java Spring Boot backend
- Go analytics service
- PostgreSQL database
- Optional reverse proxy

The intended local command is:

```bash
docker compose up --build
```

## Documentation

Project documentation is maintained under `docs/`.

Documentation will cover:

- Requirements
- System architecture
- Database design
- Testing strategy
- Deployment
- Environment setup
- API usage

## Project Quality Goals

FieldOps should be:

- Maintainable
- Testable
- Reliable
- Secure
- Observable
- Understandable
- Independently deployable where appropriate

The project avoids unnecessary enterprise technologies and complexity. The architecture focuses on demonstrating the required full-stack engineering capabilities through one coherent system.

## Local Development

Local environment setup is documented in:

```text
docs/deployment/environment-setup.md
```

## API Documentation

API documentation will be maintained through:

```text
api/openapi.yaml
```

Postman collections will be maintained under:

```text
api/postman/
```

## Project Status

Current phase:

**Phase 1 - Project Foundation**

The project is currently establishing:

- Repository structure
- Requirements
- Architecture documentation
- Database design
- Development environment
- Spring Boot foundation
- Angular foundation
- Go foundation
- CI foundation