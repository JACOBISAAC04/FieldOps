CREATE TABLE work_orders (
    id BIGSERIAL PRIMARY KEY,
    equipment_id BIGINT NOT NULL,
    engineer_id BIGINT,
    priority VARCHAR(50) NOT NULL,
    description TEXT NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    due_date TIMESTAMP,
    completed_at TIMESTAMP,

    CONSTRAINT fk_work_orders_equipment
        FOREIGN KEY (equipment_id)
        REFERENCES equipment(id),

    CONSTRAINT fk_work_orders_engineer
        FOREIGN KEY (engineer_id)
        REFERENCES engineers(id)
);

CREATE INDEX idx_work_orders_equipment
    ON work_orders(equipment_id);

CREATE INDEX idx_work_orders_engineer
    ON work_orders(engineer_id);

CREATE INDEX idx_work_orders_status
    ON work_orders(status);

CREATE INDEX idx_work_orders_priority
    ON work_orders(priority);

CREATE INDEX idx_work_orders_due_date
    ON work_orders(due_date);