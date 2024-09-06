-- Supprimer les tables si elles existent
DROP TABLE IF EXISTS friendships;
DROP TABLE IF EXISTS transactions;
DROP TABLE IF EXISTS bank_accounts;
DROP TABLE IF EXISTS users;

-- Créer la table users
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    first_name VARCHAR(250) NOT NULL,
    last_name VARCHAR(250) NOT NULL,
    email VARCHAR(250) NOT NULL,
    password VARCHAR(250) NOT NULL
);

-- Insérer des données dans la table users
INSERT INTO users (first_name, last_name, email, password) VALUES
    ('John', 'Dupont', '1@gmail.com', '$2a$10$rgVyJt18/S/QFuDoL1oqoOOYPjLvyN2eLJDurWgOzeD/khrqLvjAK'),
    ('Henry', 'Charles', '2@gmail.com', '$2a$10$rgVyJt18/S/QFuDoL1oqoOOYPjLvyN2eLJDurWgOzeD/khrqLvjAK'),
    ('Amelie', 'Boudea', '3@gmail.com', '$2a$10$rgVyJt18/S/QFuDoL1oqoOOYPjLvyN2eLJDurWgOzeD/khrqLvjAK'),
    ('Loïc', 'Dubrulle', 'loutix@hotmail.com', '$2a$10$rgVyJt18/S/QFuDoL1oqoOOYPjLvyN2eLJDurWgOzeD/khrqLvjAK');

-- Créer la table bank_accounts
CREATE TABLE bank_accounts (
    id SERIAL PRIMARY KEY,
    balance DECIMAL(10, 2) DEFAULT 0.00,
    user_id INTEGER REFERENCES users(id),
    iban VARCHAR(50)
);

-- Insérer des données dans la table bank_accounts
INSERT INTO bank_accounts (balance, user_id, iban) VALUES
    (100, 1, 'FR12345678901234567890123456'),
    (200, 2, 'FRABCDEF1234567890123456789'),
    (300, 3, 'FRABCDEF1234567890123456789'),
    (400, 4, 'FR12345678901234567890123456');

-- Créer la table friendships
CREATE TABLE friendships (
    id SERIAL PRIMARY KEY,
    user_id INTEGER REFERENCES users(id),
    friend_id INTEGER REFERENCES users(id)
);

-- Insérer des données dans la table friendships
INSERT INTO friendships (user_id, friend_id) VALUES
    (1, 2),
    (4, 1),
    (4, 3);

-- Créer la table transactions
CREATE TABLE transactions (
    id SERIAL PRIMARY KEY,
    amount DECIMAL(10, 2) DEFAULT 0.00,
    description VARCHAR(50),
    transaction_type VARCHAR(25),
    date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    bank_account_id INTEGER REFERENCES bank_accounts(id),
    friend_bank_id INTEGER
);

-- Insérer des données dans la table transactions
INSERT INTO transactions (amount, description, transaction_type, bank_account_id, friend_bank_id, date) VALUES
    (100, 'cadeau de noël', 'DEBIT', 1, 4, NOW() - INTERVAL '124 MINUTE'),
    (100, 'cadeau de noël', 'CREDIT', 4, 1, NOW() - INTERVAL '124 MINUTE'),
    (200, 'repas', 'DEBIT', 4, 2, NOW() - INTERVAL '3625 MINUTE'),
    (200, 'repas', 'CREDIT', 2, 4, NOW() - INTERVAL '3625 MINUTE');
