CREATE TABLE maintenance_records (
    id BIGSERIAL PRIMARY KEY,
    equipment_id BIGINT NOT NULL,
    engineer_id BIGINT NOT NULL,
    work_order_id BIGINT,
    description TEXT NOT NULL,
    performed_at TIMESTAMP NOT NULL,

    CONSTRAINT fk_maintenance_equipment
        FOREIGN KEY (equipment_id)
        REFERENCES equipment(id),

    CONSTRAINT fk_maintenance_engineer
        FOREIGN KEY (engineer_id)
        REFERENCES engineers(id),

    CONSTRAINT fk_maintenance_work_order
        FOREIGN KEY (work_order_id)
        REFERENCES work_orders(id)
);

CREATE INDEX idx_maintenance_equipment
    ON maintenance_records(equipment_id);

CREATE INDEX idx_maintenance_engineer
    ON maintenance_records(engineer_id);

CREATE INDEX idx_maintenance_work_order
    ON maintenance_records(work_order_id);

CREATE INDEX idx_maintenance_performed_at
    ON maintenance_records(performed_at);