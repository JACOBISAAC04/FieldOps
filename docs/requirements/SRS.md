# FieldOps Software Requirements Specification

## 1. Introduction

### 1.1 Purpose

FieldOps is a full-stack equipment and maintenance management platform for industrial field operations.

The system provides a centralized platform for managing:

- Industrial equipment
- Field engineers
- Maintenance work orders
- Maintenance records
- Equipment status
- Maintenance schedules
- Operational risk information

The system is designed to demonstrate a complete software development lifecycle from requirements and database design through implementation, testing, integration, CI, deployment, and documentation.

### 1.2 Objectives

The main objectives of FieldOps are:

- Provide centralized equipment management
- Manage field engineers and their availability
- Manage maintenance work orders
- Track maintenance history
- Provide operational monitoring through a dashboard
- Provide equipment risk information through a Go analytics service
- Demonstrate Java and Spring Boot backend development
- Demonstrate Angular frontend development
- Demonstrate PostgreSQL and SQL
- Demonstrate JPA and Hibernate
- Demonstrate service-to-service integration
- Demonstrate automated testing and CI

### 1.3 Scope

The system includes:

- User roles and access control
- Equipment management
- Engineer management
- Work order management
- Maintenance records
- Operational dashboard
- Equipment risk analysis
- REST APIs
- PostgreSQL persistence
- Java-to-Go service integration
- Automated testing
- Docker-based development
- CI using GitHub Actions

The system does not include unnecessary enterprise infrastructure such as Kafka, Kubernetes, GraphQL, multiple databases, or additional microservices without a real requirement.

---

## 2. User Roles

FieldOps defines three primary user roles.

### 2.1 Administrator

The Administrator can:

- Manage equipment
- Manage engineers
- Manage users
- Manage system configuration

### 2.2 Field Engineer

The Field Engineer can:

- View assigned work orders
- Update work order progress
- Record maintenance activities
- Complete assigned maintenance tasks

### 2.3 Operations User

The Operations User can:

- Monitor equipment
- Create maintenance work orders
- View operational dashboards
- Monitor equipment status
- Review maintenance and risk information

---

## 3. Functional Requirements

### 3.1 Equipment Management

The system shall allow authorized users to register equipment.

Equipment shall contain:

- Name
- Type
- Location
- Status
- Installation date
- Next maintenance date

The system shall allow users to:

- View equipment
- Search equipment
- Filter equipment
- Create equipment
- Update equipment
- Deactivate equipment
- View equipment maintenance history
- Identify equipment approaching maintenance

#### Acceptance Criteria

- A valid equipment record can be created.
- Equipment information can be retrieved through the API.
- Existing equipment can be updated.
- Equipment can be deactivated.
- Equipment can be searched and filtered.
- Equipment maintenance history can be retrieved.
- Equipment approaching its maintenance date can be identified.

---

## 4. Engineer Management

The system shall maintain field engineer information.

An engineer shall contain:

- User information
- Specialization
- Location
- Availability

The system shall allow authorized users to:

- View engineers
- Create engineers
- Update engineer information
- Track engineer availability
- View current workload
- Assign engineers to work orders

### Acceptance Criteria

- An engineer can be created.
- Engineer specialization and location can be stored.
- Engineer availability can be updated.
- Current workload can be determined from assigned work orders.
- Engineers can be assigned to work orders.

---

## 5. Work Order Management

The system shall allow operations users to create maintenance work orders.

A work order shall contain:

- Equipment
- Assigned engineer
- Priority
- Description
- Status
- Creation date
- Due date
- Completion date

### 5.1 Work Order Lifecycle

The normal work order lifecycle is:

```text
OPEN
  ↓
ASSIGNED
  ↓
IN_PROGRESS
  ↓
COMPLETED
```

A work order may also transition to:

```text
CANCELLED
```

The application shall enforce valid work order state transitions through business logic.

### Acceptance Criteria

- A work order can be created for equipment.
- A work order can be assigned to an engineer.
- Work order priority can be specified.
- Work order status can be updated.
- Engineers can update assigned work.
- Completed work orders record completion information.
- Work orders can be cancelled.
- Invalid lifecycle transitions are rejected.

---

## 6. Maintenance Records

The system shall maintain maintenance history for equipment.

A maintenance record shall contain:

- Equipment
- Engineer
- Work order
- Description
- Date and time performed

The system shall allow authorized users to view maintenance history for equipment.

### Acceptance Criteria

- A maintenance record can be associated with equipment.
- A maintenance record can reference the engineer who performed the work.
- A maintenance record can reference the related work order.
- Maintenance history can be retrieved for equipment.

---

## 7. Dashboard

The system shall provide an operational dashboard.

The dashboard shall provide information including:

- Total equipment
- Equipment status summary
- Open work orders
- High-priority work orders
- Engineer availability
- Recent work orders
- Maintenance due information
- Equipment risk summary

Dashboard information shall be obtained from the application data and the Go analytics service where risk information is required.

---

## 8. Equipment Risk Analytics

FieldOps shall contain an independently deployable Go analytics service.

The service shall provide equipment risk assessment.

The analytics API shall expose:

```text
GET /api/analytics/equipment/{id}
```

The response shall contain information including:

- Equipment ID
- Risk level
- Maintenance due status
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

The risk calculation shall be implemented as business logic within the Go service.

---

## 9. REST API Requirements

The Java backend shall expose REST APIs for the primary business resources.

Example equipment operations include:

```text
GET    /api/equipment
GET    /api/equipment/{id}
POST   /api/equipment
PUT    /api/equipment/{id}
DELETE /api/equipment/{id}
```

The exact API contract shall be documented separately through OpenAPI documentation.

API responses shall use appropriate HTTP status codes.

Invalid requests shall return structured error responses.

---

## 10. Database Requirements

The primary relational database shall be PostgreSQL.

The core tables shall include:

- users
- engineers
- equipment
- work_orders
- maintenance_records

The database shall use:

- Primary keys
- Foreign keys
- Constraints
- Indexes
- Transactions

The database shall maintain referential integrity between related records.

Database schema changes shall be implemented using versioned migration files.

Permanent manual schema changes shall not be used as the primary schema management approach.

---

## 11. SQL Requirements

The application shall demonstrate practical SQL usage including:

- Joins
- Grouping
- Aggregation
- Filtering
- Ordering
- Pagination
- Date-based filtering
- Status filtering
- Priority filtering
- Location filtering

Queries shall be designed around actual application requirements.

---

## 12. Backend Requirements

The primary backend shall be implemented using Java and Spring Boot.

The backend shall follow a layered architecture:

```text
Controller
    ↓
Service
    ↓
Repository
    ↓
Database
```

### Controller Layer

Responsible for:

- REST endpoints
- HTTP requests
- HTTP responses
- Request validation

### Service Layer

Responsible for:

- Business rules
- Application logic
- Transactions
- Service integration

### Repository Layer

Responsible for:

- Database access
- JPA operations
- Query execution

### Entity Layer

Responsible for representing persistent database entities using JPA and Hibernate.

### DTO Layer

Responsible for API request and response objects.

DTOs shall be kept separate from persistence entities.

### Mapper Layer

Responsible for converting between entities and DTOs.

### Exception Layer

Responsible for centralized exception handling and consistent API error responses.

---

## 13. Frontend Requirements

The frontend shall be implemented using Angular.

The frontend shall use a feature-based structure.

Major frontend features shall include:

- auth
- dashboard
- equipment
- work-orders
- engineers

Core frontend infrastructure shall contain:

- API services
- Guards
- Interceptors
- Models

Shared functionality shall contain reusable:

- Components
- Directives
- Pipes
- Tables
- Forms
- Status indicators
- Loading states
- Error states

---

## 14. Service Integration Requirements

The system shall contain integration between:

```text
Angular
    ↓
Java Spring Boot
    ↓
PostgreSQL
```

and:

```text
Java Spring Boot
    ↓
Go Analytics Service
```

The Java backend shall communicate with the Go analytics service through its REST API.

The Go analytics service shall remain independently deployable.

---

## 15. Testing Requirements

Testing shall be performed throughout development.

The development workflow shall follow a TDD-oriented process:

```text
Define expected behavior
        ↓
Write failing test
        ↓
Implement solution
        ↓
Refactor
        ↓
Run focused tests
        ↓
Run regression tests
```

### Backend Unit Testing

Java unit tests shall use:

- JUnit 5
- Mockito

Business logic shall be tested independently where practical.

### API Testing

Spring Boot Test and MockMvc shall be used to test REST API behavior.

Tests shall verify:

- Successful requests
- Validation failures
- Not-found scenarios
- Invalid operations
- Correct HTTP responses

### Integration Testing

Integration tests shall verify interaction between application layers and the database.

### Frontend Testing

Angular tests shall cover:

- Components
- Services
- Important user interactions
- Error states

Angular TestBed shall be used where appropriate.

### Go Testing

The Go service shall contain tests for:

- Risk calculation logic
- Service behavior
- HTTP handlers

### Regression Testing

Whenever a defect is fixed:

1. Reproduce the problem.
2. Add or update a regression test.
3. Implement the fix.
4. Run the focused test.
5. Run the broader relevant test suite.

---

## 16. Security Requirements

The application shall follow basic application security practices.

The system shall:

- Avoid storing plaintext passwords.
- Externalize secrets.
- Validate incoming requests.
- Apply role-based access where required.
- Avoid exposing sensitive configuration.
- Use environment variables for environment-specific configuration.

---

## 17. Non-Functional Requirements

### 17.1 Maintainability

The application shall use clear separation of responsibilities and understandable project structure.

### 17.2 Reliability

The system shall handle expected application errors through centralized error handling and appropriate validation.

### 17.3 Testability

Business logic and application components shall be structured so that they can be tested independently.

### 17.4 Security

Sensitive credentials and configuration shall not be committed to the repository.

### 17.5 Observability

Application logs and structured errors shall provide enough information to troubleshoot common failures.

### 17.6 Performance

The application shall provide acceptable performance under normal development and demonstration workloads.

### 17.7 Scalability

The Go analytics service shall remain independently deployable so that analytics functionality can be scaled separately if required.

---

## 18. Development and Delivery Requirements

The project shall use Git and GitHub for source control.

Development shall follow an Agile-oriented workflow.

The project shall use:

- GitHub Issues
- Milestones
- Meaningful commits
- Pull requests for major features
- Issue-to-implementation relationships

The development process shall include:

- Requirements
- Design
- Implementation
- Testing
- Integration
- Debugging
- Deployment
- Documentation

---

## 19. CI Requirements

GitHub Actions shall validate the project.

The CI pipeline shall eventually include:

**Java**

- Dependency installation
- Build
- Unit tests
- Integration tests where configured

**Angular**

- Dependency installation
- Tests
- Production build

**Go**

- Tests
- Build

**Docker**

- Docker image builds may be included as final validation.

CI must pass before a feature is considered complete.

---

## 20. Documentation Requirements

The project shall maintain documentation for:

- Requirements
- System architecture
- Database design
- API design
- Testing strategy
- Test plan
- Deployment
- Environment setup

API documentation shall be maintained using OpenAPI.

Postman collections shall be maintained for API testing.

---

## 21. Definition of Done

A feature is considered complete when:

- The requirement is documented.
- Acceptance criteria are defined.
- The implementation follows the project architecture.
- Relevant tests are implemented.
- Relevant tests pass.
- Database changes use migrations.
- API documentation is updated when required.
- CI passes.
- No blocking defects remain.
- The implementation has meaningful Git history.
- Relevant documentation is updated.

---

## 22. Success Criteria

FieldOps will be considered complete when:

- The README provides clear setup and project information.
- The Angular frontend communicates with the Java backend.
- The Java backend persists relational data in PostgreSQL.
- REST APIs are documented and testable.
- The Go analytics service runs independently.
- The Java backend successfully integrates with the Go service.
- Equipment risk analysis is available through the analytics service.
- Business logic has automated tests.
- CI builds and tests the project successfully.
- Required architecture, testing, deployment, and requirements documentation is complete.
- The complete application lifecycle can be explained and demonstrated.