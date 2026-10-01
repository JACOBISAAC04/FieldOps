INSERT INTO users (name, email, password_hash, role, status)
VALUES
    ('Jacob Isaac', 'jacob@fieldops.local', 'dev-password', 'ENGINEER', 'ACTIVE'),
    ('Evan Binu', 'evan@fieldops.local', 'dev-password', 'ENGINEER', 'ACTIVE'),
    ('David Shibu', 'david@fieldops.local', 'dev-password', 'ENGINEER', 'ACTIVE');

INSERT INTO engineers (user_id, specialization, location, availability)
SELECT id, 'Mechanical Maintenance', 'Kerala', 'AVAILABLE'
FROM users
WHERE email = 'jacob@fieldops.local';

INSERT INTO engineers (user_id, specialization, location, availability)
SELECT id, 'Electrical Systems', 'Kerala', 'AVAILABLE'
FROM users
WHERE email = 'evan@fieldops.local';

INSERT INTO engineers (user_id, specialization, location, availability)
SELECT id, 'Equipment Diagnostics', 'Kerala', 'AVAILABLE'
FROM users
WHERE email = 'david@fieldops.local';