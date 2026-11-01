-- Create apartment schema
CREATE SCHEMA IF NOT EXISTS apartment;

-- Create apartments table
CREATE TABLE IF NOT EXISTS apartment.apartments (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) UNIQUE NOT NULL,
    secret_code VARCHAR(255) NOT NULL,
    address TEXT,
    city VARCHAR(100),
    state VARCHAR(100),
    total_flats INTEGER,
    monthly_maintenance_amount DOUBLE PRECISION,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create users table
CREATE TABLE IF NOT EXISTS apartment.users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    flat_number VARCHAR(50) NOT NULL,
    role VARCHAR(50) NOT NULL,
    active BOOLEAN DEFAULT TRUE,
    treasurer_key VARCHAR(255),
    apartment_id BIGINT REFERENCES apartment.apartments(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create payments table
CREATE TABLE IF NOT EXISTS apartment.payments (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES apartment.users(id),
    apartment_id BIGINT NOT NULL REFERENCES apartment.apartments(id),
    amount DOUBLE PRECISION NOT NULL,
    month INTEGER NOT NULL,
    year INTEGER NOT NULL,
    status VARCHAR(50) NOT NULL,
    payment_method VARCHAR(50) NOT NULL,
    transaction_id VARCHAR(255),
    razorpay_order_id VARCHAR(255),
    receipt_url TEXT,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create announcements table
CREATE TABLE IF NOT EXISTS apartment.announcements (
    id BIGSERIAL PRIMARY KEY,
    apartment_id BIGINT NOT NULL REFERENCES apartment.apartments(id),
    user_id BIGINT REFERENCES apartment.users(id),
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    type VARCHAR(50) NOT NULL,
    published BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create expenses table
CREATE TABLE IF NOT EXISTS apartment.expenses (
    id BIGSERIAL PRIMARY KEY,
    apartment_id BIGINT NOT NULL REFERENCES apartment.apartments(id),
    user_id BIGINT REFERENCES apartment.users(id),
    title VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    amount DOUBLE PRECISION NOT NULL,
    month INTEGER NOT NULL,
    year INTEGER NOT NULL,
    receipt_url TEXT,
    bill_number VARCHAR(255),
    vendor_name VARCHAR(255),
    category VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create monthly_reports table
CREATE TABLE IF NOT EXISTS apartment.monthly_reports (
    id BIGSERIAL PRIMARY KEY,
    apartment_id BIGINT NOT NULL REFERENCES apartment.apartments(id),
    user_id BIGINT REFERENCES apartment.users(id),
    month INTEGER NOT NULL,
    year INTEGER NOT NULL,
    opening_balance DOUBLE PRECISION NOT NULL,
    total_income DOUBLE PRECISION NOT NULL,
    total_expenses DOUBLE PRECISION NOT NULL,
    closing_balance DOUBLE PRECISION NOT NULL,
    total_residents INTEGER,
    residents_paid INTEGER,
    residents_pending INTEGER,
    notes TEXT,
    published BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create notifications table
CREATE TABLE IF NOT EXISTS apartment.notifications (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES apartment.users(id),
    title VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    type VARCHAR(50) NOT NULL,
    read BOOLEAN DEFAULT FALSE,
    link TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for better performance
CREATE INDEX IF NOT EXISTS idx_users_username ON apartment.users(username);
CREATE INDEX IF NOT EXISTS idx_users_email ON apartment.users(email);
CREATE INDEX IF NOT EXISTS idx_users_apartment ON apartment.users(apartment_id);
CREATE INDEX IF NOT EXISTS idx_payments_user ON apartment.payments(user_id);
CREATE INDEX IF NOT EXISTS idx_payments_apartment ON apartment.payments(apartment_id);
CREATE INDEX IF NOT EXISTS idx_payments_month_year ON apartment.payments(month, year);
CREATE INDEX IF NOT EXISTS idx_announcements_apartment ON apartment.announcements(apartment_id);
CREATE INDEX IF NOT EXISTS idx_expenses_apartment ON apartment.expenses(apartment_id);
CREATE INDEX IF NOT EXISTS idx_expenses_month_year ON apartment.expenses(month, year);
CREATE INDEX IF NOT EXISTS idx_monthly_reports_apartment ON apartment.monthly_reports(apartment_id);
CREATE INDEX IF NOT EXISTS idx_monthly_reports_month_year ON apartment.monthly_reports(month, year);
CREATE INDEX IF NOT EXISTS idx_notifications_user ON apartment.notifications(user_id);

-- Insert default apartment (you can change the secret code)
INSERT INTO apartment.apartments (name, secret_code, address, city, state, total_flats, monthly_maintenance_amount)
VALUES ('Satya Hyma Residency', 'SATYA2024', '123 Main Street', 'Hyderabad', 'Telangana', 50, 1500.00)
ON CONFLICT (name) DO NOTHING;

-- Insert default watchman (password: watchman123)
INSERT INTO apartment.users (username, email, password, flat_number, role, apartment_id)
VALUES ('watchman', 'watchman@apartment.com', '$2a$10$YourHashedPasswordHere', 'WATCHMAN', 'WATCHMAN', 1)
ON CONFLICT (username) DO NOTHING;
