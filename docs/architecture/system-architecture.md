# FieldOps System Architecture

## 1. Architecture Overview

FieldOps uses a layered full-stack architecture consisting of:

- Angular frontend
- Java Spring Boot backend
- PostgreSQL database
- Go analytics service

The main application flow is:

```text
Angular Frontend
       |
       | REST API
       v
Java Spring Boot Backend
       |
       +--------------------+
       |                    |
       | JPA / Hibernate    | REST integration
       |                    |
       v                    v
PostgreSQL Database   Go Analytics Service
                            |
                            v
                     Equipment Risk
                       Calculation
```

The Java Spring Boot backend is the primary application responsible for business operations and persistence.

The Go service provides equipment risk analysis as an independently deployable service.

---

## 2. Architectural Goals

The architecture is designed to:

- Keep responsibilities separated
- Maintain clear boundaries between frontend and backend
- Separate business logic from database access
- Keep API contracts independent from persistence entities
- Provide a clear integration boundary for the Go analytics service
- Make individual components testable
- Keep the system understandable and maintainable
- Allow the analytics service to be deployed independently

The architecture intentionally avoids unnecessary infrastructure and additional services.

---

## 3. Frontend Architecture

The frontend is implemented using Angular.

The frontend follows a feature-based structure.

```text
frontend/
└── src/
    └── app/
        ├── core/
        │   ├── guards/
        │   ├── interceptors/
        │   ├── services/
        │   └── models/
        │
        ├── shared/
        │   ├── components/
        │   ├── directives/
        │   └── pipes/
        │
        └── features/
            ├── auth/
            ├── dashboard/
            ├── equipment/
            ├── work-orders/
            └── engineers/
```

### 3.1 Core

The core area contains application-wide infrastructure.

It includes:

- API services
- Authentication guards
- HTTP interceptors
- Shared application models

Core services are intended to be created once and reused across the application.

### 3.2 Shared

The shared area contains reusable UI functionality.

Examples include:

- Tables
- Forms
- Loading indicators
- Error displays
- Status indicators
- Reusable components
- Directives
- Pipes

Shared functionality should not contain feature-specific business logic.

### 3.3 Features

Each major business area is organized as a feature.

#### Auth

Responsible for authentication-related functionality.

#### Dashboard

Responsible for operational monitoring and summary information.

#### Equipment

Responsible for:

- Equipment listing
- Equipment search
- Equipment filtering
- Equipment creation
- Equipment editing
- Equipment status
- Maintenance information

#### Work Orders

Responsible for:

- Work order listing
- Work order creation
- Assignment
- Status updates
- Work completion

#### Engineers

Responsible for:

- Engineer listing
- Engineer profiles
- Availability
- Workload
- Assignment information

---

## 4. Backend Architecture

The Java backend is implemented using Spring Boot.

The backend follows a layered architecture:

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

The backend is organized into:

```text
backend/
└── src/
    └── main/
        └── java/
            └── com/
                └── fieldops/
                    ├── controller/
                    ├── service/
                    ├── repository/
                    ├── entity/
                    ├── dto/
                    ├── mapper/
                    ├── exception/
                    └── config/
```

---

## 5. Controller Layer

The controller layer is responsible for HTTP communication.

Responsibilities include:

- Defining REST endpoints
- Receiving HTTP requests
- Validating request input
- Calling the appropriate service
- Returning HTTP responses
- Mapping application results to API responses

Controllers should not contain complex business logic.

Example flow:

```text
HTTP Request
     |
     v
EquipmentController
     |
     v
EquipmentService
```

---

## 6. Service Layer

The service layer contains application and business logic.

Responsibilities include:

- Business rules
- Validation beyond basic request validation
- Work order lifecycle rules
- Equipment maintenance logic
- Engineer assignment rules
- Transactions
- Integration with the Go analytics service

Example:

```text
WorkOrderController
        |
        v
WorkOrderService
        |
        +--> WorkOrderRepository
        |
        +--> EngineerRepository
        |
        +--> EquipmentRepository
```

Business rules should be implemented in the service layer rather than directly inside controllers or repositories.

---

## 7. Repository Layer

The repository layer handles database persistence.

Spring Data JPA will be used for repository implementations.

Responsibilities include:

- Retrieving entities
- Saving entities
- Updating entities
- Deleting entities where appropriate
- Executing database queries
- Supporting filtering and pagination

Example:

```text
EquipmentService
       |
       v
EquipmentRepository
       |
       v
PostgreSQL
```

---

## 8. Entity Layer

The entity layer contains JPA entities representing persistent data.

Core entities include:

- User
- Engineer
- Equipment
- WorkOrder
- MaintenanceRecord

Entities will use JPA annotations to define:

- Table mappings
- Primary keys
- Relationships
- Column mappings
- Constraints where appropriate

Hibernate provides the ORM implementation for JPA.

---

## 9. DTO Layer

DTOs are used for API request and response data.

The API should not directly expose persistence entities as its primary API contract.

The DTO layer provides separation between:

```text
Database Model
      |
      v
Persistence Entity
      |
      v
DTO
      |
      v
REST API
```

This allows the database model and API contract to evolve independently.

---

## 10. Mapper Layer

The mapper layer converts between entities and DTOs.

Example:

```text
Equipment Entity
       |
       v
Equipment Mapper
       |
       v
Equipment Response DTO
```

For incoming requests:

```text
Equipment Request DTO
       |
       v
Equipment Mapper
       |
       v
Equipment Entity
```

Mapping responsibilities remain separate from controllers and services.

---

## 11. Exception Handling

The backend will use centralized exception handling.

The exception layer will provide consistent responses for situations such as:

- Resource not found
- Invalid request
- Validation failure
- Invalid work order transition
- Database-related application errors
- External service failures

The API should return appropriate HTTP status codes and structured error information.

---

## 12. Database Architecture

PostgreSQL is the primary relational database.

The main database entities are:

```text
Users
  |
  +---- Engineers

Equipment
  |
  +---- Work Orders
  |
  +---- Maintenance Records

Engineers
  |
  +---- Work Orders
  |
  +---- Maintenance Records

Work Orders
  |
  +---- Maintenance Records
```

The database uses:

- Primary keys
- Foreign keys
- Constraints
- Indexes
- Transactions

Database schema changes are managed using versioned migration files.

The detailed database design is documented separately in:

```text
docs/architecture/database-design.md
```

---

## 13. REST API Architecture

Angular communicates with the Java backend through REST APIs.

The general flow is:

```text
Angular Component
       |
       v
Angular Service
       |
       | HTTP
       v
Spring Boot Controller
       |
       v
Service Layer
       |
       v
Repository Layer
       |
       v
PostgreSQL
```

The backend exposes REST endpoints for the primary business resources.

Examples include:

```text
GET    /api/equipment
GET    /api/equipment/{id}
POST   /api/equipment
PUT    /api/equipment/{id}
DELETE /api/equipment/{id}
```

API contracts will be documented using OpenAPI.

Postman collections will provide API testing examples.

---

## 14. Java to Go Integration

The Go analytics service is a separate application.

The integration flow is:

```text
Java Spring Boot Backend
          |
          | HTTP REST
          v
Go Analytics Service
          |
          v
Risk Calculation
```

The Go service provides:

```text
GET /api/analytics/equipment/{id}
```

The service returns equipment risk information.

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

The Java backend can use this information when presenting equipment risk to the frontend.

---

## 15. Go Analytics Architecture

The Go service follows a simple layered structure:

```text
analytics-service/
├── cmd/
│   └── server/
│       └── main.go
│
├── internal/
│   ├── handler/
│   ├── service/
│   ├── repository/
│   ├── model/
│   └── config/
│
└── pkg/
    └── risk/
```

### Handler

Responsible for:

- HTTP endpoints
- Request handling
- Response formatting
- HTTP status codes

### Service

Responsible for:

- Analytics business logic
- Coordinating risk calculation
- Calling required data sources

### Repository

Responsible for retrieving required equipment information.

### Model

Contains Go data structures used by the service.

### Risk Package

Contains reusable risk calculation logic.

---

## 16. Request Flow

### Equipment Request

A typical equipment request follows:

```text
User
 |
 v
Angular Equipment Page
 |
 v
Angular Equipment Service
 |
 | HTTP GET
 v
EquipmentController
 |
 v
EquipmentService
 |
 v
EquipmentRepository
 |
 v
Hibernate / JPA
 |
 v
PostgreSQL
 |
 v
Equipment Entity
 |
 v
Equipment DTO
 |
 v
REST Response
 |
 v
Angular
```

---

## 17. Work Order Request Flow

A work order creation request follows:

```text
User
 |
 v
Angular Work Order Form
 |
 v
Angular Work Order Service
 |
 | HTTP POST
 v
WorkOrderController
 |
 v
WorkOrderService
 |
 +---- Validate equipment
 |
 +---- Validate engineer
 |
 +---- Validate priority
 |
 +---- Apply lifecycle rules
 |
 v
WorkOrderRepository
 |
 v
PostgreSQL
 |
 v
Work Order Response
 |
 v
Angular
```

The service layer is responsible for enforcing work order business rules.

---

## 18. Risk Request Flow

A risk request follows:

```text
Angular
   |
   v
Java Spring Boot
   |
   | REST request
   v
Go Analytics Service
   |
   v
Risk Calculation
   |
   v
Risk Response
   |
   v
Java Backend
   |
   v
Angular
```

This separation allows the risk calculation capability to remain independently deployable.

---

## 19. Error Flow

Errors should be handled at the appropriate layer.

Example:

```text
Request
   |
   v
Controller
   |
   v
Service
   |
   X
Business Rule Failure
   |
   v
Exception Handler
   |
   v
Structured HTTP Error
   |
   v
Angular Error Handling
```

External Go service failures should also be handled by the Java backend so that an analytics failure does not produce an unstructured application error.

---

## 20. Security Boundary

Authentication and authorization are part of the application architecture.

The frontend communicates with protected backend endpoints through HTTP requests.

The backend is responsible for enforcing authorization rules.

Sensitive configuration such as database credentials and service configuration shall be externalized through environment-specific configuration.

Passwords shall not be stored in plaintext.

---

## 21. Deployment Architecture

The final development and deployment environment is intended to use Docker.

The application components are:

```text
                 Docker Compose
                       |
       +---------------+---------------+
       |               |               |
       v               v               v
   Angular         Spring Boot       Go Service
       |               |               |
       |               |               |
       +---------------+---------------+
                       |
                       v
                  PostgreSQL
```

An optional reverse proxy may be introduced through:

```text
docker/nginx/
```

The reverse proxy is not required for the initial application implementation.

---

## 22. CI Architecture

GitHub Actions will validate the major application components.

```text
GitHub Repository
        |
        v
GitHub Actions
        |
        +---- Java build/tests
        |
        +---- Angular tests/build
        |
        +---- Go tests/build
        |
        +---- Docker validation
```

The CI pipeline should prevent broken builds and failing tests from being treated as complete changes.

---

## 23. Design Principles

The implementation follows these principles:

### Separation of Responsibilities

Each application layer has a defined responsibility.

### Simplicity

Technologies are introduced only when required by the system.

### Testability

Business logic should be independently testable.

### Maintainability

The codebase should remain understandable to another developer.

### Clear Integration Boundaries

The Go analytics service communicates with the Java backend through a defined REST interface.

### API and Persistence Separation

DTOs prevent persistence entities from becoming the API contract.

### Incremental Development

Each major feature is implemented and tested before moving to the next feature.

---

## 24. Architecture Decision Summary

| Decision | Choice |
|---|---|
| Frontend | Angular |
| Primary backend | Java Spring Boot |
| Analytics service | Go |
| Database | PostgreSQL |
| ORM | JPA / Hibernate |
| API style | REST |
| Frontend structure | Feature-based |
| Backend structure | Layered |
| Database changes | Versioned migrations |
| Testing | Unit + integration + API + regression |
| Containerization | Docker |
| Local orchestration | Docker Compose |
| CI | GitHub Actions |
| API documentation | OpenAPI |
| API testing | Postman |

---

## 25. Architecture Boundaries

The system intentionally maintains the following boundaries:

```text
Angular
    |
    | REST
    v
Java Backend
    |
    +---- JPA / Hibernate ---- PostgreSQL
    |
    | REST
    v
Go Analytics Service
```

The Java backend remains responsible for the core application domain.

The Go service remains responsible for equipment risk analytics.

PostgreSQL remains the primary relational data store.

Angular remains responsible for the user interface.

This keeps the architecture aligned with the project's functional requirements without introducing unnecessary infrastructure.