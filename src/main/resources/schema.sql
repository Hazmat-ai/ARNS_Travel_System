CREATE TABLE IF NOT EXISTS schema_version (version INTEGER PRIMARY KEY CHECK(version>0));
CREATE TABLE IF NOT EXISTS catalog_items (
 id VARCHAR(80) PRIMARY KEY, category VARCHAR(30) NOT NULL, title VARCHAR(200) NOT NULL,
 city VARCHAR(100) NOT NULL, description VARCHAR(2000) NOT NULL,
 unit_price_cents BIGINT NOT NULL CHECK(unit_price_cents BETWEEN 0 AND 100000000),
 currency VARCHAR(3) NOT NULL DEFAULT 'CAD' CHECK(currency='CAD')
);
CREATE TABLE IF NOT EXISTS contacts (
 id UUID PRIMARY KEY, full_name VARCHAR(120) NOT NULL, email VARCHAR(254) NOT NULL,
 UNIQUE(full_name,email)
);
CREATE TABLE IF NOT EXISTS reservations (
 id UUID PRIMARY KEY, item_id VARCHAR(80) NOT NULL REFERENCES catalog_items(id),
 contact_id UUID NOT NULL REFERENCES contacts(id), travel_date DATE NOT NULL,
 units INTEGER NOT NULL CHECK(units BETWEEN 1 AND 365),
 unit_price_cents BIGINT NOT NULL CHECK(unit_price_cents BETWEEN 0 AND 100000000),
 status VARCHAR(15) NOT NULL CHECK(status IN ('DRAFT','REQUESTED','CANCELLED')),
 version INTEGER NOT NULL DEFAULT 1 CHECK(version>0), created_at TIMESTAMP WITH TIME ZONE NOT NULL
);
CREATE INDEX IF NOT EXISTS reservation_status_date ON reservations(status,travel_date);
