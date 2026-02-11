# Admin / Client Feature Backend Notes

## 1. Database Objects

```sql
CREATE TABLE IF NOT EXISTS users (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  email VARCHAR(255) NOT NULL UNIQUE,
  full_name VARCHAR(120) NOT NULL,
  password_hash VARCHAR(255) NOT NULL,
  role ENUM('CLIENT','ADMIN') NOT NULL DEFAULT 'CLIENT',
  enabled TINYINT(1) NOT NULL DEFAULT 1,
  created_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6),
  updated_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6)
);

ALTER TABLE orders
  MODIFY status VARCHAR(32) NOT NULL DEFAULT 'PENDING';

CREATE INDEX IF NOT EXISTS idx_orders_status_created ON orders (status, date_created);

-- Normalize historical statuses to uppercase to match the enum the service enforces
UPDATE orders SET status = UPPER(status);
```

Run the above after pulling the code so Hibernate can keep the schema in sync.

## 2. Seed an Admin Account

Use BCrypt hashes when inserting passwords. Example hash for the password `Admin@123`:

```
$2b$12$I00KEi4rLA13XG2H/B1TnOHbLgFUNDcBEM1A2/W9v9MtAf8Qip1mu
```

Sample insert:

```sql
INSERT INTO users (email, full_name, password_hash, role)
VALUES ('admin@shop.local', 'Store Admin', '$2b$12$I00KEi4rLA13XG2H/B1TnOHbLgFUNDcBEM1A2/W9v9MtAf8Qip1mu', 'ADMIN');
```

Clients should register through `POST /api/auth/register` so their accounts stay consistent with the `customer` table.

## 3. API Quick Reference

- `POST /api/auth/register` → create client + JWT (needs first/last/full name, email, password)
- `POST /api/auth/login` → exchange credentials for JWT
- `GET /api/auth/me` → returns the authenticated profile
- `POST /api/admin/products` → admin-only create/update/delete product catalog
- `GET /api/admin/orders?status=pending&page=0&size=20` → admin queue
- `PATCH /api/admin/orders/{id}` → update order status (`PENDING|PAID|SHIPPED|CANCELLED`)
- `GET /api/admin/reports/sales?range=daily|weekly` → data for graphs (labels + totals)

Send the JWT as `Authorization: Bearer <token>` from the Angular app; guards can decode the embedded `role` claim to route admins to the protected module.

### Base URL / Environment wiring

- Backend runs on `http://localhost:8081` with all REST routes prefixed by `/api`.  
- In Angular, set `environment.apiBaseUrl = 'http://localhost:8081/api'` (or use a proxy) so `/auth/login`, `/admin/products`, etc., resolve correctly.
