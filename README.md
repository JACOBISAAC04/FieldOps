# FieldOps

FieldOps is a full-stack equipment and maintenance management platform designed for industrial field operations.

It provides equipment management, engineer management, maintenance work orders, operational dashboards, document management, equipment risk analytics, authentication, role-based access control, and production monitoring.

The project was built as a production-style full-stack engineering project with a Java Spring Boot backend, Angular frontend, PostgreSQL database, and independently deployable Go analytics service.

## Live Deployment

- Frontend: https://fieldops-frontend-djmy.onrender.com
- Backend: https://fieldops-backend-z3ob.onrender.com
- Backend Health: https://fieldops-backend-z3ob.onrender.com/actuator/health
- Analytics Health: https://fieldops-analytics.onrender.com/health

## System Architecture

```text
                         GitHub
                            |
                    GitHub Actions CI
                            |
                    Docker / Deployment
                            |
        +-------------------+-------------------+
        |                   |                   |
        v                   v                   v
 Angular Frontend     Spring Boot Backend    Go Analytics
    + Nginx                 :8080               :8081
        |                     |                   |
        | REST API            | JPA / Hibernate   |
        +-------------------->|                   |
                              v                   |
                         PostgreSQL <-------------+
                         / Supabase

                         Production
                    Render + Supabase
```

The Spring Boot backend is the primary business application. It manages authentication, users, equipment, engineers, work orders, maintenance records, documents, dashboards, and business rules.

The Go analytics service is independently deployable and provides equipment risk analysis through a REST API.

## Key Features

### Authentication and Authorization

- JWT-based authentication
- Role-based access control
- Administrator, engineer, and operations roles
- Protected REST endpoints
- Production security configuration
- Stateless authentication
- CORS configuration for the production frontend

### Equipment Management

- Register equipment
- View equipment
- Search and filter equipment
- Update equipment
- Deactivate equipment
- View maintenance history
- Track maintenance status
- Equipment risk information

### Engineer Management

- Engineer profiles
- Engineer specialization
- Engineer location
- Availability tracking
- Workload tracking
- Engineer assignment to work orders

### Work Order Management

Work orders follow a controlled lifecycle:

```text
OPEN
  |
  v
ASSIGNED
  |
  v
IN_PROGRESS
  |
  v
COMPLETED
```

Work orders can also be cancelled.

Each work order can contain:

- Equipment
- Assigned engineer
- Priority
- Description
- Status
- Creation date
- Due date
- Completion date
- Maintenance information

### Dashboard

The dashboard provides operational visibility including:

- Equipment totals
- Equipment status
- Open work orders
- High-priority work orders
- Engineer availability
- Recent work orders
- Maintenance due information
- Equipment risk information

### Equipment Risk Analytics

The Go analytics service evaluates equipment conditions and returns risk information.

Example response:

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

### Document Management

FieldOps supports document management associated with operational records.

The application includes document upload and retrieval functionality as part of the maintenance workflow.

### Health Monitoring

Spring Boot Actuator provides production health monitoring:

```text
GET /actuator/health
```

The Go analytics service provides:

```text
GET /health
```

Both services expose health information for deployment monitoring.

## Technology Stack

### Frontend

- Angular
- TypeScript
- HTML
- CSS
- Nginx

### Backend

- Java
- Spring Boot
- Spring Data JPA
- Hibernate
- Spring Security
- JWT
- REST APIs
- Maven
- Spring Boot Actuator

### Analytics Service

- Go
- REST API
- Go standard library
- Go testing

### Database

- PostgreSQL
- SQL
- JPA / Hibernate
- Flyway database migrations

### Testing

- JUnit 5
- Mockito
- Spring Boot Test
- MockMvc
- Angular testing
- Go testing
- Integration testing
- API testing

### DevOps

- Git
- GitHub
- Docker
- Docker Compose
- GitHub Actions
- Render
- Supabase

## Repository Structure

```text
FieldOps/
│
├── .github/
│   └── workflows/
│       └── ci.yml
│
├── analytics/
│
├── backend/
│
├── database/
│
├── docker/
│
├── docs/
│   ├── architecture/
│   ├── deployment/
│   ├── requirements/
│   └── testing/
│
├── frontend/
│
├── storage/
│
├── docker-compose.yml
├── .env.example
├── .gitignore
└── README.md
```

## Backend Architecture

The Spring Boot backend follows a layered architecture:

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

Supporting layers include:

- DTO
- Mapper
- Entity
- Exception Handling
- Security
- Integration Clients

### Controllers

Handle:

- HTTP requests
- Request validation
- REST endpoints
- HTTP responses

### Services

Handle:

- Business rules
- Transactions
- Application logic
- Cross-domain operations
- Analytics service integration

### Repositories

Handle:

- Database access
- JPA queries
- Entity persistence

### Entities

Represent persistent database data using JPA and Hibernate.

### DTOs

Separate API request and response models from persistence entities.

### Security

Production security is implemented using Spring Security and JWT authentication with role-based authorization.

## Database

PostgreSQL is used as the primary relational database.

The system uses:

- Primary keys
- Foreign keys
- Constraints
- Indexes
- Transactions
- Versioned Flyway migrations
- JPA / Hibernate

Database schema changes are maintained through migration files.

## Analytics Architecture

The analytics service is implemented separately in Go.

```text
Spring Boot Backend
        |
        | REST
        v
Go Analytics Service
        |
        v
Risk Calculation
        |
        v
Equipment Risk Response
```

The service can be deployed independently from the Java backend.

## Testing

FieldOps uses multiple levels of automated testing.

### Backend

The backend includes:

- Unit tests
- Controller tests
- Service tests
- Repository integration tests
- Security integration tests
- Analytics integration tests

The current backend test suite contains:

```text
133 tests
0 failures
0 errors
0 skipped
```

### Frontend

Angular tests cover frontend components, services, guards, and application behavior.

### Go

The analytics service includes tests for:

- Risk calculation
- Service logic
- HTTP handlers
- Health endpoint

### Regression Testing

Full test suites are run after major implementation and infrastructure changes to ensure existing functionality remains stable.

## CI/CD

GitHub Actions validates the project through automated CI.

The CI workflow covers the major application components and verifies builds and tests before changes are considered ready.

The project also uses Docker-based builds to keep development and deployment environments consistent.

## Docker

FieldOps supports containerized execution for the main services.

The architecture includes:

```text
Angular / Nginx
       |
Spring Boot
       |
Go Analytics
       |
PostgreSQL
```

The local containerized environment can be started with:

```bash
docker compose up --build
```

## Production Deployment

The production deployment uses:

```text
GitHub
   |
GitHub Actions
   |
Docker
   |
Render
   |
+-----------------------+
| Angular / Nginx       |
| Spring Boot           |
| Go Analytics          |
+-----------------------+
           |
        Supabase
       PostgreSQL
```

Production infrastructure includes:

- Render for application services
- Supabase PostgreSQL
- HTTPS
- Production environment variables
- Spring Boot Actuator
- Render health checks
- Application logging
- Service health monitoring

The backend health check is:

```text
/actuator/health
```

The analytics health check is:

```text
/health
```

## Observability

Production logging is configured for the Spring Boot backend.

The production configuration includes:

- Application logs
- Spring Framework logs
- Flyway logs
- Hikari connection pool logs
- Structured console log formatting
- Health monitoring through Spring Boot Actuator

Sensitive health details are not exposed through the public health endpoint.

## Documentation

Project documentation is maintained under `docs/`.

```text
docs/
├── architecture/
│   ├── database-design.md
│   └── system-architecture.md
│
├── deployment/
│   └── environment-setup.md
│
├── requirements/
│   └── SRS.md
│
└── testing/
    └── testing-strategy.md
```

The documentation covers:

- Requirements
- System architecture
- Database design
- Environment setup
- Testing strategy
- Deployment

## Development Workflow

The project was developed incrementally through defined engineering phases.

The workflow included:

1. Requirements and architecture
2. Backend foundation
3. Equipment management
4. Engineer and work order management
5. Angular frontend development
6. Go analytics service
7. Advanced work order and document functionality
8. Analytics and risk engine
9. Authentication and RBAC
10. Engineering quality and integration testing
11. Docker and CI/CD
12. Production deployment and monitoring

Each phase was tested and verified before progressing to the next stage.

## Local Development

Clone the repository and configure the required environment variables.

Backend:

```bash
cd backend
mvn spring-boot:run
```

Frontend:

```bash
cd frontend/fieldops-frontend
npm install
npm start
```

Analytics service:

```bash
cd analytics
go run .
```

The project-specific environment setup is documented in:

```text
docs/deployment/environment-setup.md
```

## Engineering Practices

FieldOps focuses on practical software engineering principles:

- Layered architecture
- Separation of concerns
- RESTful API design
- DTO-based API contracts
- Database migrations
- Automated testing
- Integration testing
- Authentication and authorization
- Service-to-service communication
- Containerization
- CI automation
- Production health monitoring
- Structured logging
- Regression testing
- Incremental development

The project intentionally avoids unnecessary complexity and focuses on demonstrating how a coherent full-stack system can be designed, implemented, tested, deployed, and maintained.

## Project Status

**Production-ready portfolio project**

Current implementation includes:

- Full-stack Angular application
- Spring Boot backend
- PostgreSQL database
- Go analytics service
- JWT authentication
- Role-based access control
- Equipment management
- Engineer management
- Work order management
- Dashboard
- Document management
- Equipment risk analytics
- Automated testing
- Docker
- GitHub Actions CI
- Cloud deployment
- HTTPS
- Production logging
- Health monitoring
- Production E2E verification

## Author

**Jacob Isaac**
B.Tech Computer Science and Engineering
Amrita Vishwa Vidyapeetham

- GitHub: https://github.com/JACOBISAAC04
- LinkedIn: https://www.linkedin.com/in/jacob-isaac-137426291/