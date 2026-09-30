# FieldOps Database Design

## 1. Database Overview

FieldOps uses PostgreSQL as its primary relational database.

The database stores the core operational information required by the application:

- Users
- Field engineers
- Equipment
- Work orders
- Maintenance records

The database is designed around relational integrity using:

- Primary keys
- Foreign keys
- Constraints
- Indexes
- Transactions

Database schema changes are managed through versioned migration files.

---

## 2. Core Tables

The initial database contains five core tables:

```text
users
engineers
equipment
work_orders
maintenance_records
```

The high-level relationship is:

```text
users
  |
  | 1 : 0..1
  v
engineers ------------------+
  |                          |
  | 1 : many                 | 1 : many
  v                          v
work_orders ---------> maintenance_records
  ^                          ^
  | 1 : many                 | 1 : many
  |                          |
equipment -------------------+
```

---

## 3. Users Table

The `users` table stores application user information.

### Columns

| Column | Type | Constraints | Description |
|---|---|---|---|
| id | BIGSERIAL | PRIMARY KEY | Unique user identifier |
| name | VARCHAR | NOT NULL | User name |
| email | VARCHAR | NOT NULL, UNIQUE | User email |
| password_hash | VARCHAR | NOT NULL | Stored password hash |
| role | VARCHAR | NOT NULL | User role |
| status | VARCHAR | NOT NULL | User account status |

### Role

The role identifies the user's application permissions.

Supported roles are:

```text
ADMINISTRATOR
FIELD_ENGINEER
OPERATIONS_USER
```

### Status

User status represents whether the account can be used.

Example values:

```text
ACTIVE
INACTIVE
```

Passwords must never be stored in plaintext.

---

## 4. Engineers Table

The `engineers` table stores field engineer-specific information.

### Columns

| Column | Type | Constraints | Description |
|---|---|---|---|
| id | BIGSERIAL | PRIMARY KEY | Unique engineer identifier |
| user_id | BIGINT | NOT NULL, UNIQUE, FK | Associated user |
| specialization | VARCHAR | NOT NULL | Engineering specialization |
| location | VARCHAR | NOT NULL | Engineer location |
| availability | VARCHAR | NOT NULL | Current availability |

The `user_id` column references:

```text
users(id)
```

The unique constraint ensures that a user has at most one engineer profile.

### Availability

Example values:

```text
AVAILABLE
BUSY
UNAVAILABLE
```

The exact values will be enforced consistently by the application.

---

## 5. Equipment Table

The `equipment` table stores industrial equipment information.

### Columns

| Column | Type | Constraints | Description |
|---|---|---|---|
| id | BIGSERIAL | PRIMARY KEY | Unique equipment identifier |
| name | VARCHAR | NOT NULL | Equipment name |
| type | VARCHAR | NOT NULL | Equipment type |
| location | VARCHAR | NOT NULL | Equipment location |
| status | VARCHAR | NOT NULL | Equipment status |
| installation_date | DATE | NOT NULL | Installation date |
| next_maintenance_date | DATE | NULL | Next scheduled maintenance |

### Status

Example equipment statuses:

```text
OPERATIONAL
MAINTENANCE
OFFLINE
DEACTIVATED
```

Equipment status will be controlled through application-level business rules.

---

## 6. Work Orders Table

The `work_orders` table stores maintenance requests and their lifecycle.

### Columns

| Column | Type | Constraints | Description |
|---|---|---|---|
| id | BIGSERIAL | PRIMARY KEY | Unique work order identifier |
| equipment_id | BIGINT | NOT NULL, FK | Related equipment |
| engineer_id | BIGINT | NULL, FK | Assigned engineer |
| priority | VARCHAR | NOT NULL | Work order priority |
| description | TEXT | NOT NULL | Maintenance description |
| status | VARCHAR | NOT NULL | Current lifecycle state |
| created_at | TIMESTAMP | NOT NULL | Creation timestamp |
| due_date | TIMESTAMP | NULL | Required completion date |
| completed_at | TIMESTAMP | NULL | Completion timestamp |

The `equipment_id` references:

```text
equipment(id)
```

The `engineer_id` references:

```text
engineers(id)
```

An engineer is optional when a work order is first created because a work order can initially have the status:

```text
OPEN
```

---

## 7. Work Order Lifecycle

The primary lifecycle is:

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

A work order may also be cancelled:

```text
OPEN --------> CANCELLED
ASSIGNED ----> CANCELLED
IN_PROGRESS -> CANCELLED
```

The application service layer will enforce valid transitions.

The database will store the current state.

---

## 8. Maintenance Records Table

The `maintenance_records` table stores completed maintenance history.

### Columns

| Column | Type | Constraints | Description |
|---|---|---|---|
| id | BIGSERIAL | PRIMARY KEY | Unique maintenance record |
| equipment_id | BIGINT | NOT NULL, FK | Maintained equipment |
| engineer_id | BIGINT | NOT NULL, FK | Engineer who performed maintenance |
| work_order_id | BIGINT | NOT NULL, FK | Related work order |
| description | TEXT | NOT NULL | Maintenance performed |
| performed_at | TIMESTAMP | NOT NULL | Maintenance timestamp |

The relationships are:

```text
maintenance_records.equipment_id
        ↓
equipment.id
```

```text
maintenance_records.engineer_id
        ↓
engineers.id
```

```text
maintenance_records.work_order_id
        ↓
work_orders.id
```

---

## 9. Entity Relationships

### User to Engineer

```text
users
  |
  | 1 : 0..1
  v
engineers
```

A user may have an engineer profile.

The engineer profile is associated with exactly one user.

### Equipment to Work Orders

```text
equipment
    |
    | 1 : many
    v
work_orders
```

One equipment item can have multiple work orders.

Each work order belongs to one equipment item.

### Engineer to Work Orders

```text
engineers
    |
    | 1 : many
    v
work_orders
```

One engineer can be assigned multiple work orders.

A work order can have zero or one assigned engineer.

### Equipment to Maintenance Records

```text
equipment
    |
    | 1 : many
    v
maintenance_records
```

One equipment item can have many maintenance records.

Each maintenance record belongs to one equipment item.

### Engineer to Maintenance Records

```text
engineers
    |
    | 1 : many
    v
maintenance_records
```

One engineer can perform multiple maintenance activities.

Each maintenance record identifies the engineer who performed the maintenance.

### Work Order to Maintenance Records

```text
work_orders
    |
    | 1 : many
    v
maintenance_records
```

A work order can be associated with maintenance records.

A maintenance record references the work order that resulted in the maintenance activity.

---

## 10. Foreign Keys

The database uses foreign keys to maintain referential integrity.

Required relationships:

```text
engineers.user_id
    → users.id

work_orders.equipment_id
    → equipment.id

work_orders.engineer_id
    → engineers.id

maintenance_records.equipment_id
    → equipment.id

maintenance_records.engineer_id
    → engineers.id

maintenance_records.work_order_id
    → work_orders.id
```

Foreign key constraints prevent invalid references between related records.

---

## 11. Constraints

The database will use constraints to protect data integrity.

Examples include:

### Primary Keys

Every core table has a unique primary key.

### Unique Constraints

The following values should be unique:

```text
users.email
engineers.user_id
```

### NOT NULL Constraints

Required business fields should not accept null values.

### Foreign Key Constraints

Related records must reference existing records.

### Application-Level Constraints

Some business rules are more appropriately enforced by the application.

Examples:

- Valid work order state transitions
- Engineer assignment rules
- Equipment lifecycle rules
- Role-based operations

---

## 12. Indexes

Indexes will be added to fields that are frequently used for:

- Searching
- Filtering
- Joining
- Ordering
- Dashboard aggregation

Potential indexes include:

```text
users.email
equipment.status
equipment.location
equipment.next_maintenance_date
work_orders.status
work_orders.priority
work_orders.equipment_id
work_orders.engineer_id
work_orders.due_date
maintenance_records.equipment_id
maintenance_records.engineer_id
maintenance_records.work_order_id
maintenance_records.performed_at
```

Indexes will be introduced based on actual query requirements rather than adding indexes indiscriminately.

---

## 13. SQL Query Requirements

The application shall demonstrate practical SQL operations.

### Joins

Examples:

```text
equipment → work_orders
work_orders → engineers
equipment → maintenance_records
```

### Aggregation

Examples:

```text
COUNT work orders by status
COUNT equipment by status
COUNT assignments by engineer
```

### Filtering

Examples:

```text
equipment by status
equipment by location
work orders by priority
work orders by status
work orders by due date
maintenance records by date
```

### Ordering

Examples:

```text
newest work orders
nearest maintenance dates
highest priority work orders
recent maintenance records
```

### Pagination

Large result sets should be retrieved using pagination rather than loading every record at once.

---

## 14. Dashboard Queries

The dashboard will require aggregated database information.

Potential dashboard queries include:

### Equipment Status

```text
Count equipment grouped by status
```

### Work Order Status

```text
Count work orders grouped by status
```

### High Priority Work

```text
Filter work orders by high priority
```

### Engineer Workload

```text
Count active work orders grouped by engineer
```

### Maintenance Due

```text
Find equipment whose next maintenance date
is approaching or has passed
```

These queries will be implemented through appropriate repository queries and service methods.

---

## 15. Transactions

Database operations that require multiple related changes shall use transactions.

Examples include:

- Completing a work order and creating a maintenance record
- Assigning an engineer while updating related work order information
- Operations that modify multiple related records

A transaction ensures that related changes either complete successfully together or are rolled back.

---

## 16. Migration Strategy

Database schema changes shall be version controlled.

Migration files will be stored under:

```text
database/migrations/
```

Migration files will be applied in sequence.

Example structure:

```text
database/
└── migrations/
    ├── V1__create_users.sql
    ├── V2__create_engineers.sql
    ├── V3__create_equipment.sql
    ├── V4__create_work_orders.sql
    └── V5__create_maintenance_records.sql
```

The exact migration tool will be selected during backend/database implementation.

The important requirement is that schema changes are versioned and reproducible.

---

## 17. Seed Data

Development seed data will be stored under:

```text
database/seed/
```

Seed data may contain:

- Development users
- Engineers
- Equipment
- Example work orders
- Maintenance records

Seed data is intended for local development and testing.

Production credentials and secrets must never be stored in seed files.

---

## 18. Database Security

Database credentials shall not be committed to Git.

Environment-specific configuration shall be provided through environment variables or external configuration.

The repository may contain:

```text
.env.example
```

but must not contain real production credentials.

---

## 19. Database Access Through JPA

The Java backend will use:

```text
Spring Data JPA
        ↓
Hibernate
        ↓
JDBC
        ↓
PostgreSQL
```

JPA entities will represent the database tables.

Spring Data repositories will provide standard persistence operations.

Custom queries will be introduced where application requirements require specific SQL behavior.

---

## 20. Database Design Principles

The database design follows these principles:

### Referential Integrity

Relationships are protected using foreign keys.

### Data Integrity

Constraints prevent invalid database states where appropriate.

### Query Efficiency

Indexes support common search, filter, join, and ordering operations.

### Version Control

Schema changes are represented through migration files.

### Transactional Consistency

Related changes are executed within transactions when required.

### Relational Design

PostgreSQL remains the single primary relational data store.

---

## 21. Database Summary

```text
users
  |
  | 1 : 0..1
  v
engineers ------------------+
  |                          |
  | 1 : many                 | 1 : many
  v                          v
work_orders ---------> maintenance_records
  ^                          ^
  | 1 : many                 | 1 : many
  |                          |
equipment -------------------+
```

Core tables:

```text
users
engineers
equipment
work_orders
maintenance_records
```

Core database capabilities:

- Primary Keys
- Foreign Keys
- Constraints
- Indexes
- Joins
- Aggregation
- Filtering
- Ordering
- Pagination
- Transactions
- Versioned Migrations

The database provides the persistent foundation for the FieldOps application while the Java service layer remains responsible for business rules and application behavior.