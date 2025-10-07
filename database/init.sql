-- Initialize MRP Database
\echo 'Creating MRP database tables...'

-- Create users table
CREATE TABLE IF NOT EXISTS users (
                                     id SERIAL PRIMARY KEY,
                                     username VARCHAR(50) NOT NULL UNIQUE,
                                     password VARCHAR(255) NOT NULL,
                                     email VARCHAR(100),
                                     createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS sessions (
     token VARCHAR(255) PRIMARY KEY,
     userId INTEGER REFERENCES users(id) ON DELETE CASCADE,
     createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
     expiresAt TIMESTAMP NOT NULL
);




-- Create indexes for better performance
CREATE INDEX IF NOT EXISTS idxUsersUsername ON users(username);
CREATE INDEX IF NOT EXISTS idxUsersEmail ON users(email);
CREATE INDEX IF NOT EXISTS idxSessionsToken ON sessions(token);

\echo 'Database initialization complete!'