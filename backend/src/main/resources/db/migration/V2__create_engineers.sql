CREATE TABLE engineers (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    specialization VARCHAR(150) NOT NULL,
    location VARCHAR(150) NOT NULL,
    availability VARCHAR(50) NOT NULL,

    CONSTRAINT fk_engineers_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
);

CREATE INDEX idx_engineers_location ON engineers(location);
CREATE INDEX idx_engineers_availability ON engineers(availability);
CREATE INDEX idx_engineers_specialization ON engineers(specialization);