CREATE TABLE documents (
    id BIGSERIAL PRIMARY KEY,
    file_name VARCHAR(255) NOT NULL,
    stored_file_name VARCHAR(255) NOT NULL UNIQUE,
    content_type VARCHAR(100) NOT NULL,
    file_size BIGINT NOT NULL,
    storage_path VARCHAR(500) NOT NULL,
    document_type VARCHAR(50) NOT NULL,
    equipment_id BIGINT,
    work_order_id BIGINT,
    uploaded_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_documents_equipment
        FOREIGN KEY (equipment_id)
        REFERENCES equipment(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_documents_work_order
        FOREIGN KEY (work_order_id)
        REFERENCES work_orders(id)
        ON DELETE CASCADE,

    CONSTRAINT chk_documents_parent
        CHECK (
            (equipment_id IS NOT NULL AND work_order_id IS NULL)
            OR
            (equipment_id IS NULL AND work_order_id IS NOT NULL)
        )
);

CREATE INDEX idx_documents_equipment_id
    ON documents(equipment_id);

CREATE INDEX idx_documents_work_order_id
    ON documents(work_order_id);