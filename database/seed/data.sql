INSERT INTO users (
    name,
    email,
    password_hash,
    role,
    status
)
VALUES
(
    'Admin User',
    'admin@fieldops.local',
    'development-only-hash',
    'ADMIN',
    'ACTIVE'
),
(
    'John Engineer',
    'john.engineer@fieldops.local',
    'development-only-hash',
    'FIELD_ENGINEER',
    'ACTIVE'
),
(
    'Operations User',
    'operations@fieldops.local',
    'development-only-hash',
    'OPERATIONS',
    'ACTIVE'
);

INSERT INTO engineers (
    user_id,
    specialization,
    location,
    availability
)
VALUES
(
    (
        SELECT id
        FROM users
        WHERE email = 'john.engineer@fieldops.local'
    ),
    'Mechanical Maintenance',
    'Kerala',
    'AVAILABLE'
);

INSERT INTO equipment (
    name,
    type,
    location,
    status,
    installation_date,
    next_maintenance_date
)
VALUES
(
    'Compressor Unit A',
    'Compressor',
    'Facility A',
    'OPERATIONAL',
    '2024-01-15',
    '2026-10-15'
),
(
    'Pump Unit B',
    'Pump',
    'Facility B',
    'MAINTENANCE_REQUIRED',
    '2023-06-20',
    '2026-09-25'
);