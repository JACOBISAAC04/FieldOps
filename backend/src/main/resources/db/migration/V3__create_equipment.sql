CREATE TABLE equipment (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    type VARCHAR(100) NOT NULL,
    location VARCHAR(150) NOT NULL,
    status VARCHAR(50) NOT NULL,
    installation_date DATE NOT NULL,
    next_maintenance_date DATE
);

CREATE INDEX idx_equipment_status ON equipment(status);
CREATE INDEX idx_equipment_location ON equipment(location);
CREATE INDEX idx_equipment_next_maintenance
    ON equipment(next_maintenance_date);