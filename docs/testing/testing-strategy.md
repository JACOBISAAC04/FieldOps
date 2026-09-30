# FieldOps Testing Strategy

## 1. Purpose

The FieldOps testing strategy defines how the application will be tested throughout development.

Testing is treated as part of the development process rather than as a final activity. Each major feature should have relevant automated tests before it is considered complete.

The testing strategy covers:

- Java Spring Boot backend
- Angular frontend
- Go analytics service
- PostgreSQL integration
- REST APIs
- Java-Go integration
- Regression testing
- CI validation
- Manual API verification

The objective is to ensure that FieldOps is reliable, maintainable, testable, and suitable for demonstrating production-oriented software development practices.

---

## 2. Testing Principles

FieldOps follows these principles:

1. Business logic should be independently testable.
2. API behavior should be tested independently from the UI.
3. Database interactions should be covered by integration tests.
4. Bugs should result in regression tests where appropriate.
5. Tests should be automated wherever practical.
6. Tests should run during CI.
7. Tests should verify both successful and failure scenarios.
8. Tests should remain understandable and maintainable.
9. A feature is not considered complete until its relevant tests pass.

---

## 3. Testing Levels

FieldOps uses multiple levels of testing.

```text
                 FieldOps Testing
                       |
        +--------------+--------------+
        |              |              |
     Unit Tests   Integration Tests  API Tests
        |              |              |
        +--------------+--------------+
                       |
                 Regression Tests
                       |
                  CI Validation
```

The different levels serve different purposes.

### Unit Tests

Verify individual classes or functions in isolation.

Examples:

- Work order business rules
- Equipment validation
- Risk calculation
- Service-layer logic
- Angular services/components

### Integration Tests

Verify that multiple application components work together.

Examples:

- Spring Boot + PostgreSQL
- Repository + database
- Controller + service + repository
- Java service + Go analytics service

### API Tests

Verify REST API behavior using HTTP requests.

Examples:

- Create equipment
- Retrieve equipment
- Update equipment
- Create work order
- Assign engineer
- Retrieve maintenance history
- Request equipment risk information

### Regression Tests

Verify that previously fixed bugs do not reappear.

---

## 4. Test-Driven Development Approach

FieldOps follows a practical Test-Driven Development workflow for business logic.

The intended workflow is:

```text
1. Define expected behavior
          ↓
2. Write a failing test
          ↓
3. Implement the smallest solution
          ↓
4. Make the test pass
          ↓
5. Refactor if necessary
          ↓
6. Run relevant test suite
```

This approach will primarily be used for important business rules and service-layer behavior.

For example, before implementing a work-order status transition, the expected valid and invalid transitions should be defined.

---

## 5. Backend Testing

The Java Spring Boot backend is tested at multiple levels.

### 5.1 Unit Testing

JUnit 5 will be used for Java unit tests.

Mockito will be used when dependencies need to be mocked.

The primary focus will be:

- Service classes
- Business rules
- Validation behavior
- Exception handling
- Mapping logic where appropriate

Example areas:

```text
EquipmentService
EngineerService
WorkOrderService
MaintenanceRecordService
```

A service test should verify business behavior without requiring a real HTTP request.

---

## 6. Service-Layer Testing

Service tests verify application business rules.

Examples include:

### Equipment

- Equipment can be created with valid information.
- Invalid equipment information is rejected.
- Existing equipment can be updated.
- Equipment can be deactivated.
- Equipment maintenance information can be retrieved.

### Engineers

- Engineers can be created.
- Engineer availability can be updated.
- Engineers can be associated with work orders.
- Engineer workload information can be retrieved.

### Work Orders

- A work order can be created.
- A work order can be assigned to an engineer.
- A work order can move through valid lifecycle states.
- Invalid status transitions are rejected.
- Completed work orders record completion information.
- Cancelled work orders cannot be incorrectly treated as active work.

---

## 7. Controller/API Testing

Spring Boot testing tools and MockMvc will be used for REST API testing.

Controller tests verify:

- HTTP methods
- URL paths
- Request validation
- Response status codes
- Response structure
- Error responses
- DTO handling

Example:

```text
GET    /api/equipment
GET    /api/equipment/{id}
POST   /api/equipment
PUT    /api/equipment/{id}
DELETE /api/equipment/{id}
```

Additional endpoints will be tested as the corresponding features are implemented.

---

## 8. Database Integration Testing

Database integration tests verify that the Java application correctly communicates with PostgreSQL.

These tests will cover:

- Entity persistence
- Entity retrieval
- Updates
- Deletes/deactivation
- Foreign-key relationships
- Query behavior
- Constraints
- Transactions
- Maintenance history retrieval
- Work-order relationships

The tests should use a controlled test database strategy so that test execution does not modify development data unexpectedly.

The exact database test setup will be selected during backend implementation.

---

## 9. Repository Testing

Repository-level testing verifies database queries and persistence behavior.

Important areas include:

- Equipment queries
- Equipment filtering
- Work-order filtering
- Engineer workload queries
- Maintenance-history queries
- Dashboard aggregation queries
- Pagination and ordering
- Relationship-based queries

The database design requires relational operations such as joins, filtering, aggregation, ordering, and pagination. These queries should therefore have appropriate integration coverage.

---

## 10. REST API Testing

REST APIs will be tested using automated tests and manual API testing tools.

Postman will be used for manual API verification and documented API examples.

API testing should cover:

### Successful requests

```text
200 OK
201 Created
204 No Content
```

where appropriate for the endpoint.

### Client errors

```text
400 Bad Request
404 Not Found
409 Conflict
```

where applicable.

### Server errors

Unexpected server failures should produce a consistent error response rather than exposing internal implementation details.

---

## 11. Angular Testing

Angular tests will use Angular's testing utilities.

Testing will focus on:

- Components
- Services
- Forms
- User interactions
- API communication behavior
- Validation
- Loading states
- Error states

Important feature areas include:

- Authentication
- Dashboard
- Equipment
- Work Orders
- Engineers

Examples:

- Equipment list displays retrieved equipment.
- Equipment forms validate required fields.
- API service calls use the correct endpoint.
- Work-order forms reject invalid input.
- Loading states are displayed while requests are active.
- API errors are handled appropriately.

---

## 12. Go Analytics Service Testing

The Go analytics service will use Go's standard testing facilities.

Testing will cover:

- Risk calculation
- Risk classification
- Maintenance-due evaluation
- Reason generation
- HTTP handlers
- Service behavior
- Error handling

The reusable risk calculation logic under:

```text
pkg/risk
```

should be independently testable.

The HTTP layer should also have tests verifying that the endpoint returns the expected response.

---

## 13. Java-Go Integration Testing

The Java backend communicates with the Go analytics service for equipment risk analysis.

Integration testing should verify the complete flow:

```text
Angular
   ↓
Java Spring Boot
   ↓
Go Analytics Service
   ↓
Risk Calculation
   ↓
Go Response
   ↓
Java
   ↓
Angular
```

Tests should verify:

- Java sends the correct request.
- Go receives and processes the request.
- Go returns the expected response structure.
- Java correctly handles the response.
- Java handles analytics-service failures.
- Invalid or unavailable equipment is handled appropriately.

The integration should also be tested when the Go service is unavailable so that failure behavior is explicitly verified.

---

## 14. Regression Testing

Every significant bug fixed during development should be evaluated for regression coverage.

The preferred workflow is:

```text
Bug discovered
      ↓
Reproduce the bug
      ↓
Identify the failing layer
      ↓
Create/update regression test
      ↓
Fix the implementation
      ↓
Run focused test
      ↓
Run broader test suite
```

This prevents previously solved problems from returning during later development.

---

## 15. Dashboard Testing

The dashboard combines information from multiple areas of the system.

Testing should verify:

- Equipment totals
- Equipment status counts
- Open work orders
- High-priority work orders
- Engineer availability
- Recent work orders
- Maintenance due information
- Risk summary

Where dashboard information comes from backend aggregation queries, those queries should also have database integration coverage.

---

## 16. Error Handling Tests

FieldOps should test expected failure scenarios rather than only successful operations.

Examples include:

- Equipment does not exist.
- Engineer does not exist.
- Work order does not exist.
- Invalid request data.
- Invalid work-order status transition.
- Duplicate data where constraints prevent it.
- Database relationship violations.
- Analytics service unavailable.
- Invalid analytics response.
- Unauthorized access where authentication/authorization is implemented.

Error responses should be consistent and should not expose sensitive implementation details.

---

## 17. Test Data

Test data should be controlled and predictable.

Test data may include:

- Users
- Engineers
- Equipment
- Work orders
- Maintenance records

Development seed data and automated test data should be treated separately where appropriate.

Tests should not depend on manually created records in a developer's local database.

---

## 18. Test Naming

Tests should clearly describe the expected behavior.

Examples:

```text
shouldCreateEquipmentWhenRequestIsValid
shouldRejectEquipmentWhenRequiredDataIsMissing
shouldAssignWorkOrderToAvailableEngineer
shouldRejectInvalidWorkOrderTransition
shouldReturnNotFoundWhenEquipmentDoesNotExist
shouldCalculateHighRiskWhenMaintenanceIsOverdue
```

The exact naming convention may be adapted to the testing framework while maintaining readable behavior-focused names.

---

## 19. CI Testing

GitHub Actions will execute automated tests as part of the CI pipeline.

The CI process will eventually validate:

```text
Java
 ├── Build
 └── Tests

Angular
 ├── Install dependencies
 ├── Tests
 └── Build

Go
 ├── Tests
 └── Build
```

Docker image builds may also be included as a final validation step.

A pull request should not be considered ready for integration if required CI checks fail.

---

## 20. Testing During Agile Sprints

Testing will be performed throughout each sprint rather than postponed until the end.

For each feature:

```text
Requirement
    ↓
Acceptance Criteria
    ↓
Implementation
    ↓
Unit Tests
    ↓
Integration/API Tests
    ↓
Regression Check
    ↓
CI Validation
    ↓
Feature Complete
```

This keeps testing aligned with the Agile development process defined for FieldOps.

---

## 21. Definition of Test Completion

A feature's testing is considered complete when:

- Relevant unit tests pass.
- Relevant integration tests pass.
- API behavior is verified where applicable.
- Failure cases are tested.
- Regression coverage exists for important bug fixes.
- CI passes.
- No known blocking defect remains.
- Test-related documentation is updated when necessary.

---

## 22. Testing Tools

| Area | Tool |
|---|---|
| Java unit tests | JUnit 5 |
| Java mocking | Mockito |
| Spring API tests | Spring Boot Test / MockMvc |
| Database integration | Spring integration testing with test database strategy |
| Angular testing | Angular TestBed |
| Go testing | Go testing package |
| API verification | Postman |
| CI | GitHub Actions |
| Database | PostgreSQL |

---

## 23. Testing and Definition of Done

Testing contributes directly to the FieldOps Definition of Done.

A feature should not be marked complete simply because the implementation works manually.

The feature should have:

```text
Implementation
+
Relevant automated tests
+
Integration/API verification
+
Regression consideration
+
CI validation
```

This ensures that FieldOps demonstrates not only feature development but also software quality practices expected in a professional development environment.

---

## 24. Testing Strategy Summary

FieldOps uses a layered testing strategy:

```text
                    FieldOps
                       |
             +---------+---------+
             |                   |
          Frontend            Backend
             |                   |
        Angular Tests      +------+------+
                           |             |
                       Unit Tests   Integration
                           |             |
                           +------+------+
                                  |
                              REST/API
                                  |
                           Java ↔ Go
                                  |
                              PostgreSQL
```

The strategy is designed to support the project's goals of maintainability, reliability, testability, quality assurance, and continuous integration.

Testing will be implemented alongside each feature rather than being treated as a separate final phase.