-- Normalize existing order statuses for admin filters
UPDATE orders SET status = UPPER(status);
ALTER TABLE product MODIFY COLUMN image_url LONGTEXT;

-- Seed / refresh a default admin account (password: Admin@123)
INSERT INTO user_account (email, full_name, password, role, enabled)
VALUES ('admin@shop.local', 'Store Admin', '$2b$12$I00KEi4rLA13XG2H/B1TnOHbLgFUNDcBEM1A2/W9v9MtAf8Qip1mu', 'ADMIN', 1)
ON DUPLICATE KEY UPDATE
    full_name = VALUES(full_name),
    role = VALUES(role),
    enabled = VALUES(enabled);

CREATE TABLE IF NOT EXISTS customer_messages (
    id CHAR(36) PRIMARY KEY,
    name VARCHAR(120) NOT NULL,
    email VARCHAR(180) NOT NULL,
    phone VARCHAR(40) NULL,
    subject VARCHAR(200) NOT NULL,
    message TEXT NOT NULL,
    type VARCHAR(40) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'NEW',
    admin_note TEXT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

ALTER TABLE customer_messages MODIFY COLUMN id CHAR(36) NOT NULL;
ALTER TABLE customer_messages MODIFY COLUMN status VARCHAR(20) NOT NULL DEFAULT 'NEW';
