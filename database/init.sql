-- Initialize MRP Database
\echo 'Creating MRP database tables...'

-- Create users table
CREATE TABLE IF NOT EXISTS users (
                                     id SERIAL PRIMARY KEY,
                                     username VARCHAR(50) NOT NULL UNIQUE,
                                     password VARCHAR(255) NOT NULL,
                                     email VARCHAR(100),
                                     created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Insert sample users for testing
INSERT INTO users (username, password, email) VALUES
                                                                  ('testuser1', 'password123', 'test1@mrp.local'),
                                                                  ('testuser2', 'password456', 'test2@mrp.local'),
                                                                  ('admin', 'admin123', 'admin@mrp.local')
ON CONFLICT (username) DO NOTHING;

-- Create indexes for better performance
CREATE INDEX IF NOT EXISTS idx_users_username ON users(username);
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);

\echo 'Database initialization complete!'