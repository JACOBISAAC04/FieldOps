INSERT INTO users (name, email, password_hash, role, status)
VALUES
    ('Arjun Nair', 'arjun@fieldops.local', 'dev-password', 'ENGINEER', 'ACTIVE'),
    ('Rahul Menon', 'rahul@fieldops.local', 'dev-password', 'ENGINEER', 'ACTIVE'),
    ('Nikhil Kumar', 'nikhil@fieldops.local', 'dev-password', 'ENGINEER', 'ACTIVE'),
    ('Aditya Raj', 'aditya@fieldops.local', 'dev-password', 'ENGINEER', 'ACTIVE'),
    ('Vishnu Prasad', 'vishnu@fieldops.local', 'dev-password', 'ENGINEER', 'ACTIVE');

INSERT INTO engineers (user_id, specialization, location, availability)
SELECT id, 'Hydraulic Systems', 'Kerala', 'AVAILABLE'
FROM users
WHERE email = 'arjun@fieldops.local';

INSERT INTO engineers (user_id, specialization, location, availability)
SELECT id, 'Electrical Maintenance', 'Tamil Nadu', 'AVAILABLE'
FROM users
WHERE email = 'rahul@fieldops.local';

INSERT INTO engineers (user_id, specialization, location, availability)
SELECT id, 'Mechanical Systems', 'Karnataka', 'AVAILABLE'
FROM users
WHERE email = 'nikhil@fieldops.local';

INSERT INTO engineers (user_id, specialization, location, availability)
SELECT id, 'Instrumentation', 'Kerala', 'BUSY'
FROM users
WHERE email = 'aditya@fieldops.local';

INSERT INTO engineers (user_id, specialization, location, availability)
SELECT id, 'Preventive Maintenance', 'Andhra Pradesh', 'AVAILABLE'
FROM users
WHERE email = 'vishnu@fieldops.local';